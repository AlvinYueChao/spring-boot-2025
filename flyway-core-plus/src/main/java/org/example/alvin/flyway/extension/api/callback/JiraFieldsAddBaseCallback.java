package org.example.alvin.flyway.extension.api.callback;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.example.alvin.flyway.extension.api.exception.JiraFieldsAlterException;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.Configuration;

@Slf4j
public abstract class JiraFieldsAddBaseCallback extends BaseCallback {

  @Override
  public void handle(Event event, Context context) {
    Configuration configuration = context.getConfiguration();
    Connection connection = context.getConnection();
    try {
      createJiraFieldsIfAbsent(connection, configuration);
    } catch (SQLException e) {
      throw new JiraFieldsAlterException(e);
    }
  }

  public abstract void createJiraFieldsIfAbsent(Connection connection, Configuration configuration) throws SQLException;

  @Override
  public boolean supports(Event event, Context context) {
    return event == Event.BEFORE_MIGRATE;
  }
}
