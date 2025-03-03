package org.example.alvin.flyway.extension.api.callback.mssql;

import lombok.extern.slf4j.Slf4j;
import org.example.alvin.flyway.extension.api.exception.JiraInfoUpdateException;
import org.example.alvin.flyway.extension.api.migration.JiraMigration;
import org.example.alvin.flyway.extension.util.JiraInfoUtil;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(name = "com.microsoft.sqlserver.jdbc.SQLServerDriver")
@ConditionalOnProperty(name = "spring.flyway.extension.jira.enabled", havingValue = "true")
public class MSSQLJiraInfoUpdateCallback extends BaseCallback {

  private static final String UPDATE_SQL = "UPDATE %s.%s.%s SET jira = ?, jira_sprint = ?, author = ? WHERE checksum = ?";

  @Override
  public void handle(Event event, Context context) {
    Map<Integer, JiraMigration> appliedMigrations = JiraInfoUtil.getJiraMigrationMap();
    if (appliedMigrations == null || appliedMigrations.isEmpty()) {
      log.warn("Jira migration info is empty, no need to update");
      return;
    }
    Connection connection = context.getConnection();
    Configuration configuration = context.getConfiguration();
    try {
      String catalog = connection.getCatalog();
      String schema = connection.getSchema();
      String table = configuration.getTable();
      String updateSql = String.format(UPDATE_SQL, catalog, schema, table);
      PreparedStatement statement = connection.prepareStatement(updateSql);
      appliedMigrations.forEach((checksum, jiraMigration) -> {
        try {
          statement.setString(1, jiraMigration.getJira());
          statement.setString(2, jiraMigration.getJiraSprint());
          statement.setString(3, jiraMigration.getAuthor());
          statement.setInt(4, checksum);
          statement.addBatch();
        } catch (SQLException e) {
          throw new JiraInfoUpdateException(e);
        }
      });
      statement.executeBatch();
      connection.commit();
    } catch (SQLException e) {
      throw new JiraInfoUpdateException(e);
    }
  }

  @Override
  public boolean supports(Event event, Context context) {
    return event == Event.AFTER_MIGRATE || event == Event.AFTER_MIGRATE_ERROR;
  }
}
