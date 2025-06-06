package com.ksoot.feast.dto.registry.builder;

import com.ksoot.feast.FeastClientException;
import com.ksoot.feast.config.FeastProperties;
import com.ksoot.feast.dto.registry.FeatureService;
import feast.proto.core.*;
import feast.registry.RegistryServerOuterClass;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class FeatureServiceRequestBuilder {

  private final FeastProperties feastProperties;

  // Validate Feature View names and Feature View column names before creating a feature service
  public RegistryServerOuterClass.ApplyFeatureServiceRequest buildApplyFeatureServiceRequest(
      final FeatureService featureServiceRequest,
      final List<FeatureViewProto.FeatureView> featureViews) {
    final List<FeatureReferenceProto.FeatureViewProjection> featureViewProjections =
        featureViews.stream()
            .map(
                fv -> {
                  final String featureViewName = fv.getSpec().getName();
                  final List<String> featureViewColumnNames =
                      fv.getSpec().getFeaturesList().stream()
                          .map(FeatureProto.FeatureSpecV2::getName)
                          .toList();
                  final Set<String> requestedFeatureViewColumnNames =
                      featureServiceRequest.getFeatureReferences().get(featureViewName);
                  final List<FeatureProto.FeatureSpecV2> featureColumns;
                  if (CollectionUtils.isEmpty(requestedFeatureViewColumnNames)) {
                    featureColumns = fv.getSpec().getFeaturesList();
                  } else {
                    final List<String> invalidColumnNames =
                        requestedFeatureViewColumnNames.stream()
                            .filter(col -> !featureViewColumnNames.contains(col))
                            .toList();
                    if (CollectionUtils.isNotEmpty(invalidColumnNames)) {
                      throw new FeastClientException(
                          "Invalid feature names: "
                              + invalidColumnNames
                              + " in Feature view: "
                              + featureViewName);
                    } else {
                      featureColumns =
                          fv.getSpec().getFeaturesList().stream()
                              .filter(
                                  colSpec ->
                                      requestedFeatureViewColumnNames.contains(colSpec.getName()))
                              .toList();
                    }
                  }
                  return FeatureReferenceProto.FeatureViewProjection.newBuilder()
                      .setFeatureViewName(fv.getSpec().getName())
                      .addAllFeatureColumns(featureColumns)
                      .build();
                })
            .toList();
    final FeatureServiceProto.FeatureServiceSpec.Builder featureServiceSpecBuilder =
        FeatureServiceProto.FeatureServiceSpec.newBuilder()
            .setName(featureServiceRequest.getName())
            .addAllFeatures(featureViewProjections);
    if (StringUtils.isNotBlank(featureServiceRequest.getDescription())) {
      featureServiceSpecBuilder.setDescription(featureServiceRequest.getDescription());
    }
    if (StringUtils.isNotBlank(featureServiceRequest.getOwner())) {
      featureServiceSpecBuilder.setOwner(featureServiceRequest.getOwner());
    }
    if (MapUtils.isNotEmpty(featureServiceRequest.getTags())) {
      featureServiceSpecBuilder.putAllTags(featureServiceRequest.getTags());
    }
    final FeatureServiceProto.FeatureService featureService =
        FeatureServiceProto.FeatureService.newBuilder()
            .setSpec(featureServiceSpecBuilder.build())
            .build();
    return RegistryServerOuterClass.ApplyFeatureServiceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setCommit(true)
        .setFeatureService(featureService)
        .build();
  }

  public RegistryServerOuterClass.GetFeatureServiceRequest buildGetFeatureServiceRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.GetFeatureServiceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .setName(name)
        .build();
  }

  public RegistryServerOuterClass.ListFeatureServicesRequest buildListFeatureServicesRequest() {
    return RegistryServerOuterClass.ListFeatureServicesRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .build();
  }

  public RegistryServerOuterClass.DeleteFeatureServiceRequest buildDeleteFeatureServiceRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.DeleteFeatureServiceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setName(name)
        .setCommit(true)
        .build();
  }
}
