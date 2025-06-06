package com.ksoot.feast.dto.serving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@Valid
@Getter
@ToString
public class OnlineFeaturesRequest {

  @Schema(description = "Feature service name", example = "driver_activity_v1")
  private final String featureService;

  @Schema(description = "Feature references")
  private final Set<FeatureReference> featureReferences;

  @Schema(description = "Entities", example = "{ \"driver_id\": [ 1001, 1002 ] }")
  @NotEmpty
  private final Map<@NotEmpty String, @NotEmpty Set<@NotNull Object>> entities;

  private OnlineFeaturesRequest(
      final String featureService,
      final Set<FeatureReference> featureReferences,
      final Map<String, Set<Object>> entities) {
    Assert.state(
        (StringUtils.isNotBlank(featureService) && CollectionUtils.isEmpty(featureReferences))
            || (StringUtils.isBlank(featureService)
                && CollectionUtils.isNotEmpty(featureReferences)),
        "Exactly one of 'featureService' or 'featureReferences' required");
    this.featureService = featureService;
    this.featureReferences = featureReferences;
    this.entities = entities;
  }

  @JsonCreator
  public static OnlineFeaturesRequest of(
      @JsonProperty("featureService") final String featureService,
      @JsonProperty("featureReferences") final Set<FeatureReference> featureReferences,
      @JsonProperty("entities") final Map<String, Set<Object>> entities) {
    return new OnlineFeaturesRequest(featureService, featureReferences, entities);
  }

  public boolean hasFeatureService() {
    return StringUtils.isNoneBlank(this.featureService);
  }

  public boolean hasFeatureReferences() {
    return CollectionUtils.isNotEmpty(this.featureReferences);
  }

  public GetOnlineFeaturesRequest features() {
    return GetOnlineFeaturesRequest.of(this);
  }

  @Valid
  @Getter
  @AllArgsConstructor
  public static class GetOnlineFeaturesRequest {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<@NotEmpty String> features;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String feature_service;

    @NotEmpty private final Map<@NotEmpty String, @NotEmpty Set<@NotNull Object>> entities;

    static GetOnlineFeaturesRequest of(final OnlineFeaturesRequest onlineFeaturesRequest) {
      final Set<String> reqFeatures =
          CollectionUtils.isNotEmpty(onlineFeaturesRequest.featureReferences)
              ? onlineFeaturesRequest.featureReferences.stream()
                  .map(FeatureReference::value)
                  .collect(Collectors.toSet())
              : null;
      return new GetOnlineFeaturesRequest(
          reqFeatures, onlineFeaturesRequest.featureService, onlineFeaturesRequest.entities);
    }
  }
}
