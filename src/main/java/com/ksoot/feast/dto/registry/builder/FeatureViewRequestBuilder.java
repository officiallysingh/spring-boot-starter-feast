package com.ksoot.feast.dto.registry.builder;

import com.google.protobuf.Duration;
import com.ksoot.feast.config.FeastProperties;
import com.ksoot.feast.dto.registry.FeatureView;
import feast.proto.core.*;
import feast.registry.RegistryServerOuterClass;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class FeatureViewRequestBuilder {

  private final FeatureSpecBuilder featureSpecBuilder;

  private final FeastProperties feastProperties;

  public RegistryServerOuterClass.ApplyFeatureViewRequest buildApplyFeatureViewRequest(
      final FeatureView featureViewRequest,
      final List<EntityProto.Entity> entities,
      final DataSourceProto.DataSource dataSource) {
    final List<FeatureProto.FeatureSpecV2> entityColumns =
        entities.stream()
            .map(
                e -> {
                  final FeatureProto.FeatureSpecV2.Builder featureSpecBuilder =
                      FeatureProto.FeatureSpecV2.newBuilder()
                          .setName(e.getSpec().getJoinKey())
                          .setValueType(e.getSpec().getValueType());
                  if (StringUtils.isNotBlank(e.getSpec().getDescription())) {
                    featureSpecBuilder.setDescription(e.getSpec().getDescription());
                  }
                  if (MapUtils.isNotEmpty(e.getSpec().getTagsMap())) {
                    featureSpecBuilder.putAllTags(e.getSpec().getTagsMap());
                  }
                  return featureSpecBuilder.build();
                })
            .toList();
    final List<FeatureProto.FeatureSpecV2> features =
        featureViewRequest.getFeatures().stream()
            .map(this.featureSpecBuilder::buildFeatureSpec)
            .toList();
    final FeatureViewProto.FeatureViewSpec.Builder featureViewSpecBuilder =
        FeatureViewProto.FeatureViewSpec.newBuilder()
            .setProject(this.feastProperties.getProject())
            .setName(featureViewRequest.getName())
            .addAllEntities(featureViewRequest.getEntities())
            .addAllEntityColumns(entityColumns)
            .addAllFeatures(features)
            .setTtl(
                Duration.newBuilder()
                    .setSeconds(featureViewRequest.getTtl().getSeconds())
                    .setNanos(featureViewRequest.getTtl().getNano())
                    .build())
            .setOnline(featureViewRequest.isOnline());

    final FeatureViewType featureViewType = this.getFeatureViewType(dataSource);
    if (featureViewType.isStream()) {
      featureViewSpecBuilder.setStreamSource(dataSource);
    } else {
      featureViewSpecBuilder.setBatchSource(dataSource);
    }
    if (StringUtils.isNotBlank(featureViewRequest.getDescription())) {
      featureViewSpecBuilder.setDescription(featureViewRequest.getDescription());
    }
    if (StringUtils.isNotBlank(featureViewRequest.getOwner())) {
      featureViewSpecBuilder.setOwner(featureViewRequest.getOwner());
    }
    if (MapUtils.isNotEmpty(featureViewRequest.getTags())) {
      featureViewSpecBuilder.putAllTags(featureViewRequest.getTags());
    }

    final FeatureViewProto.FeatureView featureView =
        FeatureViewProto.FeatureView.newBuilder().setSpec(featureViewSpecBuilder.build()).build();
    return RegistryServerOuterClass.ApplyFeatureViewRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setCommit(true)
        .setFeatureView(featureView)
        .build();
  }

  private void buildApplyStreamFeatureViewRequest() {
    StreamFeatureViewProto.StreamFeatureViewSpec streamFeatureViewSpec =
        StreamFeatureViewProto.StreamFeatureViewSpec.newBuilder()
            .setMode("spark")
            .setStreamSource(DataSourceProto.DataSource.newBuilder())
            .setTimestampField("")
            //              .se
            .build();
    StreamFeatureViewProto.StreamFeatureView streamFeatureView =
        StreamFeatureViewProto.StreamFeatureView.newBuilder()
            .setSpec(StreamFeatureViewProto.StreamFeatureViewSpec.newBuilder())
            .build();
  }

  public RegistryServerOuterClass.GetFeatureViewRequest buildGetFeatureViewRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.GetFeatureViewRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .setName(name)
        .build();
  }

  public RegistryServerOuterClass.ListFeatureViewsRequest buildListFeatureViewsRequest() {
    return RegistryServerOuterClass.ListFeatureViewsRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .build();
  }

  public RegistryServerOuterClass.DeleteFeatureViewRequest buildDeleteFeatureViewRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.DeleteFeatureViewRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setName(name)
        .setCommit(true)
        .build();
  }

  private FeatureViewType getFeatureViewType(final DataSourceProto.DataSource dataSource) {
    if (dataSource.hasPushOptions()
        || dataSource.hasKafkaOptions()
        || dataSource.hasKinesisOptions()) {
      return FeatureViewType.STREAM;
    } else {
      return FeatureViewType.BATCH;
    }
  }

  enum FeatureViewType {
    BATCH,
    STREAM,
    ON_DEMAND;

    public boolean isBatch() {
      return this == BATCH;
    }

    public boolean isStream() {
      return this == STREAM;
    }

    public boolean isOnDemand() {
      return this == ON_DEMAND;
    }
  }
}
