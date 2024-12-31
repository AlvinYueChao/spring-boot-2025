package org.example.alvin.flyway.extension.api.migration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraMigration {
  private String jira;
  private String jiraSprint;
  private String author;
}
