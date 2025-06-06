package com.ksoot.feast.dto.registry;

import static com.ksoot.feast.FeatureStoreConstants.CREATED_TIMESTAMP_FIELD;
import static com.ksoot.feast.FeatureStoreConstants.EVENT_TIMESTAMP_FIELD;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import feast.proto.core.DataSourceProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@Getter
@JsonTypeName("CUSTOM_SOURCE")
@ToString(callSuper = true)
@Valid
public class PostgresSqlSource extends DataSource {

  @Schema(
      description = "Data source query. Either query or table required",
      example = "select * from driver_stats_source")
  private final String query;

  @Schema(
      description = "Data source SQL table name. Either query or table required",
      example = "driver_stats_source")
  private final String table;

  @Schema(
      description = "Feature event timestamp column name",
      example = "event_timestamp",
      defaultValue = "event_timestamp")
  @NotEmpty
  private final String eventTimestampField;

  @Schema(
      description = "Feature created timestamp in feast to be used for deduplication",
      example = "created")
  @NotEmpty
  private final String createdTimestampField;

  private PostgresSqlSource(
      final String name,
      final String query,
      final String table,
      final String eventTimestampField,
      final String createdTimestampField,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.CUSTOM_SOURCE, description, owner, tags);
    this.query = query;
    this.table = table;
    this.eventTimestampField =
        StringUtils.isBlank(eventTimestampField) ? EVENT_TIMESTAMP_FIELD : eventTimestampField;
    this.createdTimestampField =
        StringUtils.isBlank(createdTimestampField)
            ? CREATED_TIMESTAMP_FIELD
            : createdTimestampField;
  }

  @JsonCreator
  public static PostgresSqlSource of(
      @Schema(description = "Postgres Data Source name", example = "driver_stats_batch_source")
          @JsonProperty("name")
          final String name,
      @JsonProperty("query") final String query,
      @JsonProperty("table") final String table,
      @JsonProperty("eventTimestampField") final String eventTimestampField,
      @JsonProperty("createdTimestampField") final String createdTimestampField,
      @Schema(
              description = "Postgres Data Source description",
              example = "Postgres Data Source for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Postgres Data Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    Assert.state(
        (StringUtils.isNotBlank(query) && StringUtils.isBlank(table))
            || (StringUtils.isBlank(query) && StringUtils.isNotBlank(table)),
        "Exactly one of 'query' or 'table' is required.");
    return new PostgresSqlSource(
        name, query, table, eventTimestampField, createdTimestampField, description, owner, tags);
  }

  // Asking only for required arguments
  public static PostgresSqlSource of(
      final String name,
      final String query,
      final String table,
      final String eventTimestampField,
      final String createdTimestampField) {
    return new PostgresSqlSource(
        name, query, table, eventTimestampField, createdTimestampField, null, null, null);
  }

  public SqlSource sqlSource() {
    return new SqlSource(
        this.name,
        this.description,
        this.query,
        this.table,
        this.eventTimestampField,
        this.createdTimestampField);
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PACKAGE)
  @Valid
  public static class SqlSource {

    @NotEmpty private final String name;

    @NotEmpty private final String description;

    private final String query;

    private final String table;

    @NotEmpty private final String timestamp_field;

    @NotEmpty private final String created_timestamp_column;
  }
}
