package org.example.alvin.flyway.extension.api.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.example.alvin.flyway.extension.api.exception.JiraFieldsAlterException;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(name = "oracle.jdbc.driver.OracleDriver")
@ConditionalOnProperty(name = "spring.flyway.extension.jira.enabled", havingValue = "true")
public class OracleJiraFieldsAddCallback extends BaseCallback {

  private static final String ADD_JIRA_FIELD_SQL_TEMPLATE = "ALTER TABLE %s.%s ADD %s %s";
  private static final String JIRA_FIELD_TYPE = "VARCHAR(30)";

  @Override
  public void handle(Event event, Context context) {
    Configuration configuration = context.getConfiguration();
    Connection connection = context.getConnection();
    try {
      String catalog = connection.getCatalog();
      String schema = connection.getSchema();
      String table = configuration.getTable();
      ResultSet jiraFieldCheckResult = connection.getMetaData().getColumns(catalog, schema, table, "jira");
      ResultSet jiraSprintFieldCheckResult = connection.getMetaData().getColumns(catalog, schema, table, "jira_sprint");
      ResultSet authorFieldCheckResult = connection.getMetaData().getColumns(catalog, schema, table, "author");
      boolean jiraFieldExists = jiraFieldCheckResult != null && jiraFieldCheckResult.next();
      boolean jiraSprintFieldExists = jiraSprintFieldCheckResult != null && jiraSprintFieldCheckResult.next();
      boolean authorFieldExists = authorFieldCheckResult != null && authorFieldCheckResult.next();
      if (!(jiraFieldExists || jiraSprintFieldExists || authorFieldExists)) {
        try (Statement statement = connection.createStatement()) {
          statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, table, "jira", JIRA_FIELD_TYPE));
          statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, table, "jira_sprint", JIRA_FIELD_TYPE));
          statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, table, "author", JIRA_FIELD_TYPE));
          statement.executeBatch();
        } catch (SQLException e) {
          throw new JiraFieldsAlterException(e);
        }
      } else {
        log.warn("Jira migration fields already exists, no need to add");
      }
    } catch (SQLException e) {
      throw new JiraFieldsAlterException(e);
    }
  }

  @Override
  public boolean supports(Event event, Context context) {
    return event == Event.BEFORE_MIGRATE;
  }
}
