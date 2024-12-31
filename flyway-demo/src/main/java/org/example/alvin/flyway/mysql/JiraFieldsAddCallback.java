package org.example.alvin.flyway.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.example.alvin.flyway.extension.api.callback.JiraFieldsAddBaseCallback;
import org.example.alvin.flyway.extension.api.exception.JiraFieldsAlterException;
import org.flywaydb.core.api.configuration.Configuration;

@Slf4j
public class JiraFieldsAddCallback extends JiraFieldsAddBaseCallback {

  private static final String ADD_JIRA_FIELD_SQL_TEMPLATE = "ALTER TABLE `%s`.`%s`.%s ADD %s %s";
  private static final String JIRA_FIELD_TYPE = "VARCHAR(30)";

  @Override
  public void createJiraFieldsIfAbsent(Connection connection, Configuration configuration) throws SQLException {
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
        statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, schema, table, "jira", JIRA_FIELD_TYPE));
        statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, schema, table, "jira_sprint", JIRA_FIELD_TYPE));
        statement.addBatch(String.format(ADD_JIRA_FIELD_SQL_TEMPLATE, catalog, schema, table, "author", JIRA_FIELD_TYPE));
        statement.executeBatch();
      } catch (SQLException e) {
        throw new JiraFieldsAlterException(e);
      }
    } else {
      log.warn("Jira migration fields already exists, no need to add");
    }
  }
}
