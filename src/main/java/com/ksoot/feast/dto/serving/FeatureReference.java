package com.ksoot.feast.dto.serving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Valid
public class FeatureReference {
  @Schema(description = "Feature View Name", example = "driver_hourly_stats")
  @NotEmpty
  private final String featureView;

  @Schema(description = "Feature Name", example = "conv_rate")
  @NotEmpty
  private final String feature;

  @JsonCreator
  public static FeatureReference of(
      @JsonProperty("featureView") final String featureView,
      @JsonProperty("feature") final String feature) {
    return new FeatureReference(featureView, feature);
  }

  public String value() {
    return this.featureView + ":" + this.feature;
  }
}
