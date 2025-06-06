package com.ksoot.feast.dto.registry;

import static com.ksoot.feast.FeatureStoreConstants.CREATED_TIMESTAMP_FIELD;
import static com.ksoot.feast.FeatureStoreConstants.EVENT_TIMESTAMP_FIELD;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString(callSuper = true)
@Valid
public class FeatureView extends AbstractFeastRequest {

  @Schema(description = "Entities names list", example = "[\"driver\"]")
  @NotEmpty
  private final Set<@NotEmpty String> entities;

  @Schema(description = "Features")
  @NotEmpty
  private final Set<@NotNull Feature> features;

  @Schema(description = "DataSource name", example = "driver_stats_source")
  @NotEmpty
  private final String dataSource;

  @Schema(
      description = "Time to live in java.time.Duration format. e.g P1D for 1 day",
      example = "P1D")
  @NotNull
  private final Duration ttl;

  @Schema(
      description = "Whether to make this feature view available for online retrieval.",
      example = "true")
  private final boolean online;

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

  private FeatureView(
      final String name,
      final Set<String> entities,
      final Set<Feature> features,
      final String dataSource,
      final Duration ttl,
      final boolean online,
      final String eventTimestampField,
      final String createdTimestampField,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, description, owner, tags);
    this.entities = entities;
    this.features = features;
    this.dataSource = dataSource;
    this.ttl = ttl;
    this.online = online;
    this.eventTimestampField =
        StringUtils.isBlank(eventTimestampField) ? EVENT_TIMESTAMP_FIELD : eventTimestampField;
    this.createdTimestampField =
        StringUtils.isBlank(createdTimestampField)
            ? CREATED_TIMESTAMP_FIELD
            : createdTimestampField;
  }

  @JsonCreator
  public static FeatureView of(
      @Schema(description = "Feature View name", example = "driver_hourly_stats")
          @JsonProperty("name")
          final String name,
      @JsonProperty("entities") final Set<String> entities,
      @JsonProperty("features") final Set<Feature> features,
      @JsonProperty("dataSource") final String dataSource,
      @JsonProperty("ttl") final Duration ttl,
      @JsonProperty("online") final boolean online,
      @JsonProperty("eventTimestampField") final String eventTimestampField,
      @JsonProperty("createdTimestampField") final String createdTimestampField,
      @Schema(
              description = "Feature View description",
              example = "Driver hourly stats feature view for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Feature View Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<@NotEmpty String, @NotEmpty String> tags) {
    return new FeatureView(
        name,
        entities,
        features,
        dataSource,
        ttl,
        online,
        eventTimestampField,
        createdTimestampField,
        description,
        owner,
        tags);
  }

  // Asking only for required arguments
  public static FeatureView of(
      final String name,
      final Set<String> entities,
      final Set<Feature> features,
      final String dataSource,
      final Duration ttl,
      final boolean online) {
    return FeatureView.of(
        name, entities, features, dataSource, ttl, online, null, null, null, null, null);
  }

  public Set<String> getEntities() {
    return CollectionUtils.isNotEmpty(this.entities)
        ? Collections.unmodifiableSet(this.entities)
        : Collections.emptySet();
  }

  public Set<Feature> getFeatures() {
    return CollectionUtils.isNotEmpty(this.features)
        ? Collections.unmodifiableSet(this.features)
        : Collections.emptySet();
  }
}
