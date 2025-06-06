package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;

@Getter
@ToString(callSuper = true)
@Valid
public class FeatureService extends AbstractFeastRequest {

  @Schema(
      description =
          "A map where the key represents the name of a feature view, and the value is a list of feature names to be fetched from that feature view. Each key must specify a valid feature view name, and the associated list contains the feature names to be retrieved.",
      example = "{\"driver_hourly_stats_batch\": [\"conv_rate\", \"acc_rate\"]}")
  @NotEmpty
  private final Map<@NotEmpty String, Set<String>> featureReferences;

  private FeatureService(
      @Schema(description = "Feast FeatureService name", example = "driver_hourly_stats")
          final String name,
      final Map<String, Set<String>> featureReferences,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, description, owner, tags);
    this.featureReferences = featureReferences;
  }

  @JsonCreator
  public static FeatureService of(
      @Schema(description = "Feature Service name", example = "driver_activity_v1")
          @JsonProperty("name")
          final String name,
      @JsonProperty("featureReferences") final Map<String, Set<String>> featureReferences,
      @Schema(
              description = "Feature Service description",
              example = "Driver hourly stats feature service v1 for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Feature Service Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<@NotEmpty String, @NotEmpty String> tags) {
    return new FeatureService(name, featureReferences, description, owner, tags);
  }

  // Asking only for required arguments
  public static FeatureService of(final String name, final Map<String, Set<String>> featureViews) {
    return FeatureService.of(name, featureViews, null, null, null);
  }

  // Asking only for required arguments
  public static FeatureService of(final String name, final Set<String> featureViews) {
    Assert.notEmpty(featureViews, "'featureViews' must not be null or empty");
    Assert.noNullElements(featureViews, "'featureViews' must not contain null elements");
    Map<String, Set<String>> featureViewsMap =
        featureViews.stream().collect(Collectors.toMap(f -> f, f -> Collections.emptySet()));
    return FeatureService.of(name, featureViewsMap, null, null, null);
  }

  public Map<String, Set<String>> getFeatureReferences() {
    return MapUtils.isNotEmpty(this.featureReferences)
        ? Collections.unmodifiableMap(this.featureReferences)
        : Collections.emptyMap();
  }
}
