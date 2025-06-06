package com.ksoot.feast.dto.serving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

@Valid
@Getter
@ToString
public class MaterializationRequest {

  @Schema(
      description =
          "Materialization start date time, not required in case of Incremental Materialization",
      nullable = true,
      example = "2024-10-01T23:59:59.99999+05:30")
  //  @Past
  @JsonProperty("start_ts")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final String startTimestamp;

  @Schema(
      description =
          "Materialization end date time, not required in case of Incremental Materialization",
      example = "2024-10-31T23:59:59.99999+05:30")
  @NotNull
  @JsonProperty("end_ts")
  private final String endTimestamp;

  @Schema(
      description =
          "List of feature view names to materialize in case only specific feature views are to be materialized. "
              + "If not specified all feature views are materialized.",
      nullable = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonProperty("feature_views")
  private final Set<@NotEmpty String> featureViews;

  private MaterializationRequest(
      final OffsetDateTime startTimestamp,
      final OffsetDateTime endTimestamp,
      final Set<String> featureViews) {
    Assert.state(
        Objects.isNull(startTimestamp) || !endTimestamp.isBefore(startTimestamp),
        "Materialization 'startTimestamp' should be before 'endTimestamp'");
    this.startTimestamp = Objects.isNull(startTimestamp) ? null : startTimestamp.toString();
    this.endTimestamp = endTimestamp.toString();
    this.featureViews = featureViews;
  }

  public static MaterializationRequest ofIncremental(
      final OffsetDateTime endTimestamp, final String... featureViews) {
    return new MaterializationRequest(
        null,
        endTimestamp,
        ArrayUtils.isNotEmpty(featureViews)
            ? Sets.newLinkedHashSet(Lists.newArrayList(featureViews))
            : Collections.emptySet());
  }

  public static MaterializationRequest ofIncremental(
      final OffsetDateTime endTimestamp, final Set<String> featureViews) {
    return new MaterializationRequest(
        null,
        endTimestamp,
        CollectionUtils.isNotEmpty(featureViews) ? featureViews : Collections.emptySet());
  }

  public static MaterializationRequest of(
      final OffsetDateTime startTimestamp,
      final OffsetDateTime endTimestamp,
      final String... featureViews) {
    return new MaterializationRequest(
        startTimestamp,
        endTimestamp,
        ArrayUtils.isNotEmpty(featureViews)
            ? Sets.newLinkedHashSet(Lists.newArrayList(featureViews))
            : Collections.emptySet());
  }

  @JsonCreator
  public static MaterializationRequest of(
      @JsonProperty("start_ts") final OffsetDateTime startTimestamp,
      @JsonProperty("end_ts") final OffsetDateTime endTimestamp,
      @JsonProperty("feature_views") final Set<String> featureViews) {
    return new MaterializationRequest(
        startTimestamp,
        endTimestamp,
        CollectionUtils.isNotEmpty(featureViews) ? featureViews : Collections.emptySet());
  }

  @JsonIgnore
  public boolean isIncremental() {
    return Objects.isNull(this.startTimestamp);
  }
}
