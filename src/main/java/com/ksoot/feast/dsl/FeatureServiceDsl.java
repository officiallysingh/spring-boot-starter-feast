package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.FeatureService;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

public class FeatureServiceDsl {

  public interface FeatureReferenceBuilder {

    MoreFeatureReferencesBuilder featureReference(
        final String featureView, final Set<String> features);

    MoreFeatureReferencesBuilder featureReference(
        final String featureView, final String... features);

    MoreFeatureReferencesBuilder featureReferences(
        final Map<String, Set<String>> featureReferences);

    DescriptionBuilder<FeatureService> featureViews(final Set<String> featureViews);

    DescriptionBuilder<FeatureService> featureViews(
        final String featureView, final String... featureViews);
  }

  public interface MoreFeatureReferencesBuilder extends DescriptionBuilder<FeatureService> {

    MoreFeatureReferencesBuilder and(final String featureView, final Set<String> features);

    MoreFeatureReferencesBuilder and(final String featureView, final String... features);

    MoreFeatureReferencesBuilder and(final Map<String, Set<String>> featureReferences);
  }

  public static class Executor extends AbstractRegistryDslExecutor<FeatureService>
      implements FeatureReferenceBuilder, MoreFeatureReferencesBuilder {

    private final Map<String, Set<String>> featureReferences = new LinkedHashMap<>();

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    @Override
    public MoreFeatureReferencesBuilder featureReference(
        final String featureView, final Set<String> features) {
      Assert.hasText(featureView, "Feature view name required");
      //      Assert.state(
      //          CollectionUtils.isNotEmpty(features), "'features' must not be null or empty
      // list");
      if (CollectionUtils.isNotEmpty(features)) {
        this.featureReferences
            .computeIfAbsent(featureView, fv -> new LinkedHashSet<>())
            .addAll(features);
      }
      return this;
    }

    @Override
    public MoreFeatureReferencesBuilder featureReference(
        final String featureView, final String... features) {
      Assert.hasText(featureView, "Feature view name required");
      //      Assert.state(ArrayUtils.isNotEmpty(features), "'features' must not be null or empty
      // array");
      if (ArrayUtils.isNotEmpty(features)) {
        this.featureReferences
            .computeIfAbsent(featureView, fv -> new LinkedHashSet<>())
            .addAll(List.of(features));
      }
      return this;
    }

    @Override
    public MoreFeatureReferencesBuilder featureReferences(
        final Map<String, Set<String>> featureReferences) {
      Assert.state(
          MapUtils.isNotEmpty(featureReferences),
          "'featureReferences' must not be null or empty array");
      featureReferences.forEach(this::featureReference);
      return this;
    }

    @Override
    public DescriptionBuilder<FeatureService> featureViews(final Set<String> featureViews) {
      Assert.notEmpty(featureViews, "'featureViews' must not be null or empty set");
      Assert.noNullElements(featureViews, "'featureViews' must not contain null elements");
      featureViews.forEach(
          featureView -> this.featureReferences.put(featureView, new LinkedHashSet<>()));
      return this;
    }

    @Override
    public DescriptionBuilder<FeatureService> featureViews(
        final String featureView, final String... featureViews) {
      Assert.hasText(featureView, "'featureView' must not be null or empty set");
      Assert.notEmpty(featureViews, "'featureViews' must not be null or empty");
      Assert.noNullElements(featureViews, "'featureViews' must not contain null elements");
      this.featureReferences.put(featureView, new LinkedHashSet<>());
      Arrays.stream(featureViews)
          .forEach(fv -> this.featureReferences.put(fv, new LinkedHashSet<>()));
      return this;
    }

    @Override
    public MoreFeatureReferencesBuilder and(String featureView, Set<String> features) {
      this.featureReference(featureView, features);
      return this;
    }

    @Override
    public MoreFeatureReferencesBuilder and(String featureView, String... features) {
      this.featureReference(featureView, features);
      return this;
    }

    @Override
    public MoreFeatureReferencesBuilder and(Map<String, Set<String>> featureReferences) {
      this.featureReferences(featureReferences);
      return this;
    }

    @Override
    public FeatureService build() {
      return FeatureService.of(
          this.name, this.featureReferences, this.description, this.owner, this.tags);
    }

    @Override
    public void apply() {
      final FeatureService featureService = this.build();
      this.feastRegistryClient.applyFeatureService(featureService);
    }
  }
}
