package com.ksoot.feast.dto.registry;

import static com.ksoot.feast.FeatureStoreConstants.EVENT_TIMESTAMP_FIELD;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import feast.proto.core.DataSourceProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@JsonTypeName("STREAM_KAFKA")
@ToString(callSuper = true)
@Valid
public class KafkaSource extends DataSource {

  @Schema(
      description = "Kafka Bootstrap Servers. In case of multiples, separate with commas",
      example = "localhost:9092")
  @NotEmpty
  private final String bootstrapServers;

  @Schema(description = "Kafka Topic name", example = "driver_stats_topic")
  @NotEmpty
  private final String topicName;

  @Schema(
      description = "Kafka Message Schema Json",
      example =
          "driver_id integer, event_timestamp timestamp, conv_rate double, acc_rate double, avg_daily_trips double, created timestamp")
  @NotEmpty
  private final String schemaJson;

  @Schema(
      description = "Stream Watermark Delay Threshold in java.time.Duration format. e.g PT5M",
      example = "PT5M")
  @NotNull
  private final Duration watermarkDelayThreshold;

  @Schema(description = "Backing Batch Source name", example = "driver_stats_source")
  @NotEmpty
  private final String batchSource;

  @Schema(
      description = "Feature event timestamp Column name",
      example = "event_timestamp",
      defaultValue = "event_timestamp")
  @NotEmpty
  private final String eventTimestampField;

  private KafkaSource(
      final String name,
      final String bootstrapServers,
      final String topicName,
      final String schemaJson,
      final Duration watermarkDelayThreshold,
      final String batchSource,
      final String eventTimestampField,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.STREAM_KAFKA, description, owner, tags);
    this.bootstrapServers = bootstrapServers;
    this.topicName = topicName;
    this.schemaJson = schemaJson;
    this.watermarkDelayThreshold = watermarkDelayThreshold;
    this.batchSource = batchSource;
    this.eventTimestampField =
        StringUtils.isBlank(eventTimestampField) ? EVENT_TIMESTAMP_FIELD : eventTimestampField;
  }

  @JsonCreator
  public static KafkaSource of(
      @Schema(description = "Kafka Source name", example = "driver_stats_stream_source")
          @JsonProperty("name")
          final String name,
      @JsonProperty("bootstrapServers") final String bootstrapServers,
      @JsonProperty("topicName") final String topicName,
      @JsonProperty("schemaJson") final String schemaJson,
      @JsonProperty("watermarkDelayThreshold") final Duration watermarkDelayThreshold,
      @JsonProperty("batchSource") final String batchSource,
      @JsonProperty("eventTimestampField") final String eventTimestampField,
      @Schema(description = "Kafka Source description", example = "Kafka Source for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Kafka Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new KafkaSource(
        name,
        bootstrapServers,
        topicName,
        schemaJson,
        watermarkDelayThreshold,
        batchSource,
        eventTimestampField,
        description,
        owner,
        tags);
  }

  // Asking only for required arguments
  public static KafkaSource of(
      final String name,
      final String bootstrapServers,
      final String topicName,
      final String schemaJson,
      final Duration watermarkDelayThreshold,
      final String batchSource,
      final String eventTimestampField) {
    return new KafkaSource(
        name,
        bootstrapServers,
        topicName,
        schemaJson,
        watermarkDelayThreshold,
        batchSource,
        eventTimestampField,
        null,
        null,
        null);
  }
}
