package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastServingClient;
import com.ksoot.feast.dto.serving.PushFeaturesRequest;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

public class PushFeaturesDsl {

  public interface PushModeBuilder {

    FeatureBuilder to(final PushFeaturesRequest.PushMode mode);

    FeatureBuilder toOnline();

    FeatureBuilder toOffline();

    FeatureBuilder toOnlineAndOffline();
  }

  public interface FeatureBuilder {

    MoreFeatureBuilder feature(final String feature, final List<Object> values);

    MoreFeatureBuilder feature(final String feature, final Object... values);

    MoreFeatureBuilder features(final Map<String, List<Object>> features);
  }

  public interface MoreFeatureBuilder extends ApplierBuilder<PushFeaturesRequest> {

    MoreFeatureBuilder and(final String feature, final List<Object> values);

    MoreFeatureBuilder and(final String feature, final Object... values);

    MoreFeatureBuilder and(final Map<String, List<Object>> features);
  }

  public static class Executor implements PushModeBuilder, FeatureBuilder, MoreFeatureBuilder {

    private final FeastServingClient feastServingClient;

    private final String pushSourceName;

    private PushFeaturesRequest.PushMode mode = PushFeaturesRequest.PushMode.online;

    private Map<String, List<Object>> features = new LinkedHashMap<>();

    public Executor(final FeastServingClient feastServingClient, final String pushSourceName) {
      this.feastServingClient = feastServingClient;
      this.pushSourceName = pushSourceName;
    }

    @Override
    public FeatureBuilder to(final PushFeaturesRequest.PushMode mode) {
      this.mode = mode;
      return this;
    }

    @Override
    public FeatureBuilder toOnline() {
      this.mode = PushFeaturesRequest.PushMode.online;
      return this;
    }

    @Override
    public FeatureBuilder toOffline() {
      this.mode = PushFeaturesRequest.PushMode.offline;
      return this;
    }

    @Override
    public FeatureBuilder toOnlineAndOffline() {
      this.mode = PushFeaturesRequest.PushMode.online_and_offline;
      return this;
    }

    @Override
    public MoreFeatureBuilder feature(final String feature, final List<Object> values) {
      Assert.hasText(feature, "feature name required");
      Assert.state(CollectionUtils.isNotEmpty(values), "'values' must not be null or empty list");
      this.features.computeIfAbsent(feature, fv -> new LinkedList<>()).addAll(values);
      return this;
    }

    @Override
    public MoreFeatureBuilder feature(final String feature, final Object... values) {
      Assert.hasText(feature, "feature name required");
      Assert.state(ArrayUtils.isNotEmpty(values), "'values' must not be null or empty array");
      this.features.computeIfAbsent(feature, fv -> new LinkedList<>()).addAll(List.of(values));
      return this;
    }

    @Override
    public MoreFeatureBuilder features(final Map<String, List<Object>> features) {
      Assert.state(MapUtils.isNotEmpty(features), "'features' must not be null or empty array");
      features.forEach(this::feature);
      return this;
    }

    @Override
    public MoreFeatureBuilder and(final String feature, final List<Object> values) {
      this.feature(feature, values);
      return this;
    }

    @Override
    public MoreFeatureBuilder and(final String feature, final Object... values) {
      this.feature(feature, values);
      return this;
    }

    @Override
    public MoreFeatureBuilder and(final Map<String, List<Object>> features) {
      this.features(features);
      return this;
    }

    @Override
    public PushFeaturesRequest build() {
      return PushFeaturesRequest.of(this.pushSourceName, this.mode, this.features);
    }

    @Override
    public void apply() {
      final PushFeaturesRequest pushFeaturesRequest = this.build();
      this.feastServingClient.pushFeatures(pushFeaturesRequest);
    }
  }
}
