package com.ksoot.feast.dto.registry;

import static com.ksoot.feast.FeatureStoreConstants.CREATED_TIMESTAMP_FIELD;
import static com.ksoot.feast.FeatureStoreConstants.EVENT_TIMESTAMP_FIELD;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonTypeName("BATCH_SPARK")
@ToString(callSuper = true)
@Valid
public class SparkSource extends DataSource {

  @Schema(
      description = "Data source table name. It could be a BigQuery or Iceberg table",
      example = "catalog.namespace.driver_hourly_stats")
  private final String table;

  @Schema(
      description = "Data source query. Either query or table required",
      example = "select * from catalog.namespace.driver_hourly_stats")
  private final String query;

  @Schema(description = "Data source File path.", example = "/data/driver_hourly_stats.parquet")
  private final String filePath;

  @Schema(description = "Data source File format. Example parquet, csv etc.", example = "parquet")
  private final String fileFormat;

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

  private SparkSource(
      final String name,
      final String table,
      final String query,
      final String filePath,
      final String fileFormat,
      final String eventTimestampField,
      final String createdTimestampField,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.BATCH_SPARK, description, owner, tags);
    this.table = table;
    this.query = query;
    this.filePath = filePath;
    this.fileFormat = fileFormat;
    this.eventTimestampField =
        StringUtils.isBlank(eventTimestampField) ? EVENT_TIMESTAMP_FIELD : eventTimestampField;
    this.createdTimestampField =
        StringUtils.isBlank(createdTimestampField)
            ? CREATED_TIMESTAMP_FIELD
            : createdTimestampField;
  }

  @JsonCreator
  public static SparkSource of(
      @Schema(description = "Spark Source name", example = "driver_stats_spark_source")
          @JsonProperty("name")
          final String name,
      @JsonProperty("table") final String table,
      @JsonProperty("query") final String query,
      @JsonProperty("filePath") final String filePath,
      @JsonProperty("fileFormat") final String fileFormat,
      @JsonProperty("eventTimestampField") final String eventTimestampField,
      @JsonProperty("createdTimestampField") final String createdTimestampField,
      @Schema(description = "Spark Source description", example = "Spark Source for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Spark Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    Assert.state(
        (StringUtils.isNotBlank(table)
                && StringUtils.isBlank(query)
                && StringUtils.isBlank(filePath)
                && StringUtils.isBlank(fileFormat))
            || (StringUtils.isBlank(table)
                && StringUtils.isNotBlank(query)
                && StringUtils.isBlank(filePath)
                && StringUtils.isBlank(fileFormat))
            || (StringUtils.isBlank(table)
                && StringUtils.isBlank(query)
                && StringUtils.isNotBlank(filePath)
                && StringUtils.isNotBlank(fileFormat)),
        "Spark Source can either be of with Table or Query, or with File path and format.");
    return new SparkSource(
        name,
        table,
        query,
        filePath,
        fileFormat,
        eventTimestampField,
        createdTimestampField,
        description,
        owner,
        tags);
  }

  // Spark Source with Table
  public static SparkSource ofTable(
      final String name,
      final String table,
      final String eventTimestampField,
      final String createdTimestampField) {
    return new SparkSource(
        name,
        table,
        null,
        null,
        null,
        eventTimestampField,
        createdTimestampField,
        null,
        null,
        null);
  }

  // Spark Source with Table
  public static SparkSource ofTable(final String name, final String table) {
    return new SparkSource(name, table, null, null, null, null, null, null, null, null);
  }

  // Spark Source with Table
  public static SparkSource ofQuery(
      final String name,
      final String query,
      final String eventTimestampField,
      final String createdTimestampField) {
    return new SparkSource(
        name,
        null,
        query,
        null,
        null,
        eventTimestampField,
        createdTimestampField,
        null,
        null,
        null);
  }

  // Spark Source with Table
  public static SparkSource ofQuery(final String name, final String query) {
    return new SparkSource(name, null, query, null, null, null, null, null, null, null);
  }

  // Spark Source with File
  public static SparkSource ofFile(
      final String name,
      final String filePath,
      final String fileFormat,
      final String eventTimestampField,
      final String createdTimestampField) {
    return new SparkSource(
        name,
        null,
        null,
        filePath,
        fileFormat,
        eventTimestampField,
        createdTimestampField,
        null,
        null,
        null);
  }

  // Spark Source with File
  public static SparkSource ofFile(
      final String name, final String filePath, final String fileFormat) {
    return new SparkSource(name, null, null, filePath, fileFormat, null, null, null, null, null);
  }

  @JsonIgnore
  public boolean isTableType() {
    return StringUtils.isNotBlank(this.table);
  }

  @JsonIgnore
  public boolean isQueryType() {
    return StringUtils.isNotBlank(this.query);
  }

  @JsonIgnore
  public boolean isFileType() {
    return StringUtils.isNotBlank(this.filePath) && StringUtils.isNotBlank(this.fileFormat);
  }
}
