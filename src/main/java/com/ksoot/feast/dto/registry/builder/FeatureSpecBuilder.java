package com.ksoot.feast.dto.registry.builder;

import com.ksoot.feast.dto.registry.Feature;
import feast.proto.core.FeatureProto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class FeatureSpecBuilder {

  public FeatureProto.FeatureSpecV2 buildFeatureSpec(final Feature feature) {
    final FeatureProto.FeatureSpecV2.Builder featureSpecBuilder =
        FeatureProto.FeatureSpecV2.newBuilder()
            .setName(feature.getName())
            .setValueType(feature.getValueType());
    if (StringUtils.isNotBlank(feature.getDescription())) {
      featureSpecBuilder.setDescription(feature.getDescription());
    }
    if (MapUtils.isNotEmpty(feature.getTags())) {
      featureSpecBuilder.putAllTags(feature.getTags());
    }
    return featureSpecBuilder.build();
  }
}
