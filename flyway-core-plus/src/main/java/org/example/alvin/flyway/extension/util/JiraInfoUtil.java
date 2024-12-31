package org.example.alvin.flyway.extension.util;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.alvin.flyway.extension.api.migration.JiraMigration;

@Data
public class JiraInfoUtil {

  private JiraInfoUtil() {
  }

  private static ThreadLocal<Map<Integer, JiraMigration>> jiraMigrationLocalMap = new ThreadLocal<>();

  static {
    jiraMigrationLocalMap.remove();
    Map<Integer, JiraMigration> jiraMigrationMap = new ConcurrentHashMap<>();
    jiraMigrationLocalMap.set(jiraMigrationMap);
  }

  public static void putJiraMigration(Integer checksum, JiraMigration jiraMigration) {
    Map<Integer, JiraMigration> map = jiraMigrationLocalMap.get();
    map.computeIfAbsent(checksum, k -> jiraMigration);
  }

  public static Map<Integer, JiraMigration> getJiraMigrationMap() {
    return jiraMigrationLocalMap.get();
  }
}
