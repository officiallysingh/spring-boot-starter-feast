package com.ksoot.feast.dto.serving;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class OnlineFeatures {

  private Metadata metadata;

  private List<Result> results;

  // Method to transform data into a tabular list of maps
  public List<Map<String, Object>> getFeatures() {
    if (CollectionUtils.isEmpty(this.results)) {
      return Collections.emptyList();
    }
    final List<Map<String, Object>> tableData = new ArrayList<>();
    final int numRows = this.results.get(0).getValues().size();

    // Iterate over rows
    for (int i = 0; i < numRows; i++) {
      final Map<String, Object> row = new HashMap<>();
      for (int j = 0; j < this.metadata.getFeatureNames().size(); j++) {
        final String columnName = this.metadata.getFeatureNames().get(j);
        final Object value = this.results.get(j).getValues().get(i);
        row.put(columnName, value);
      }
      tableData.add(row);
    }
    return tableData;
  }

  @Getter
  @Setter
  public static class Metadata {

    @JsonProperty("feature_names")
    private List<String> featureNames;
  }

  @Getter
  @Setter
  public static class Result {
    private List<Object> values;
    private List<String> statuses;

    @JsonProperty("event_timestamps")
    private List<OffsetDateTime> eventTimestamps;
  }
}
