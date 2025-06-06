package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastServingClient;
import com.ksoot.feast.dto.serving.MaterializationRequest;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

public class MaterializationDsl {

  public static EndTimestampBuilder of(
      final FeastServingClient feastServingClient, final OffsetDateTime startTimestamp) {
    return new Executor(feastServingClient, startTimestamp);
  }

  public static FeatureViewBuilder ofIncremental(
      final FeastServingClient feastServingClient, final OffsetDateTime endTimestamp) {
    return new Executor(feastServingClient, null, endTimestamp);
  }

  public interface EndTimestampBuilder {

    FeatureViewBuilder till(final OffsetDateTime endTimestamp);

    FeatureViewBuilder tillNow();
  }

  public interface FeatureViewBuilder extends FeatureViewsBuilder {

    FeatureViewBuilder featureView(final String featureView);
  }

  public interface FeatureViewsBuilder extends ApplierBuilder<MaterializationRequest> {

    FeatureViewBuilder featureViews(final String... featureViews);

    FeatureViewBuilder featureViews(final Set<String> featureViews);
  }

  public static class Executor implements EndTimestampBuilder, FeatureViewBuilder {

    private final FeastServingClient feastServingClient;

    private final OffsetDateTime startTimestamp;

    private OffsetDateTime endTimestamp;

    private final Set<String> featureViews = new LinkedHashSet<>();

    Executor(final FeastServingClient feastServingClient, final OffsetDateTime startTimestamp) {
      this.feastServingClient = feastServingClient;
      this.startTimestamp = startTimestamp;
    }

    Executor(
        final FeastServingClient feastServingClient,
        final OffsetDateTime startTimestamp,
        final OffsetDateTime endTimestamp) {
      this.feastServingClient = feastServingClient;
      this.startTimestamp = startTimestamp;
      this.endTimestamp = endTimestamp;
    }

    @Override
    public FeatureViewBuilder till(final OffsetDateTime endTimestamp) {
      this.endTimestamp = endTimestamp;
      return this;
    }

    @Override
    public FeatureViewBuilder tillNow() {
      this.endTimestamp = OffsetDateTime.now();
      return this;
    }

    @Override
    public FeatureViewBuilder featureView(final String featureView) {
      Assert.hasText(featureView, "'featureView' must not be null or empty");
      this.featureViews.add(featureView);
      return this;
    }

    @Override
    public FeatureViewBuilder featureViews(final String... featureViews) {
      Assert.state(
          ArrayUtils.isNotEmpty(featureViews), "'featureViews' must not be null or empty array");
      Arrays.stream(featureViews).forEach(this::featureView);
      return this;
    }

    @Override
    public FeatureViewBuilder featureViews(final Set<String> featureViews) {
      Assert.state(
          CollectionUtils.isNotEmpty(featureViews),
          "'featureViews' must not be null or empty list");
      featureViews.forEach(this::featureView);
      return this;
    }

    @Override
    public MaterializationRequest build() {
      return MaterializationRequest.of(this.startTimestamp, this.endTimestamp, this.featureViews);
    }

    @Override
    public void apply() {
      final MaterializationRequest materializationRequest = this.build();
      this.feastServingClient.materialize(materializationRequest);
    }
  }
}
