package org.example.alvin.flyway.extension.api.callback;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.example.alvin.flyway.extension.api.exception.JiraInfoReadException;
import org.example.alvin.flyway.extension.api.migration.JiraMigration;
import org.example.alvin.flyway.extension.util.JiraInfoUtil;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JiraInfoCollectCallback extends BaseCallback {

  @Override
  public void handle(Event event, Context context) {
    log.info("JiraMigrationCallback: handle event: {}", event);
    MigrationInfo migrationInfo = context.getMigrationInfo();

    Map<String, String> parsed = new HashMap<>();
    Pattern pattern = Pattern.compile("@([\\w|_]+):\\s*(.*?)(?=\\n|$)");

    String physicalLocation = migrationInfo.getPhysicalLocation();

    try (Scanner scanner = new Scanner(new File(physicalLocation))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        log.info("JiraMigrationCallback: line: {}", line);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
          parsed.put(matcher.group(1), matcher.group(2));
        }
      }
    } catch (IOException e) {
      throw new JiraInfoReadException(e);
    }
    log.info("JiraMigrationCallback: parsed: {}", parsed);

    Objects.requireNonNull(parsed.get("jira"), "Jira is required");
    Objects.requireNonNull(parsed.get("jira_sprint"), "Jira sprint is required");
    Objects.requireNonNull(parsed.get("author"), "Author is required");

    JiraMigration newJiraMigration = JiraMigration.builder().jira(parsed.get("jira")).jiraSprint(parsed.get("jira_sprint")).author(parsed.get("author")).build();
    JiraInfoUtil.putJiraMigration(migrationInfo.getChecksum(), newJiraMigration);
  }

  @Override
  public boolean supports(Event event, Context context) {
    return event == Event.BEFORE_EACH_MIGRATE;
  }
}
