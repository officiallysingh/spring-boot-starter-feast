package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastServingClient;
import com.ksoot.feast.dto.serving.FeatureReference;
import com.ksoot.feast.dto.serving.OnlineFeatures;
import com.ksoot.feast.dto.serving.OnlineFeaturesRequest;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

public class GetOnlineFeaturesDsl {

  public interface EntityBuilder extends EntitiesBuilder {

    EntityBuilder entity(final String id, final Set<Object> values);

    EntityBuilder entity(final String id, final Object... values);
  }

  public interface EntitiesBuilder extends GetFeaturesBuilder {

    GetFeaturesBuilder entities(final Map<String, List<Object>> entities);
  }

  public interface GetFeaturesBuilder {

    OnlineFeatures get();
  }

  public static class Executor implements EntityBuilder {

    private final FeastServingClient feastServingClient;

    private String featureService;

    private Set<FeatureReference> featureReferences;

    private final Map<String, Set<Object>> entities = new LinkedHashMap<>();

    public Executor(final FeastServingClient feastServingClient, final String featureService) {
      this.feastServingClient = feastServingClient;
      this.featureService = featureService;
    }

    public Executor(
        final FeastServingClient feastServingClient, Set<FeatureReference> featureReferences) {
      this.feastServingClient = feastServingClient;
      this.featureReferences = featureReferences;
    }

    public Executor(
        final FeastServingClient feastServingClient, final FeatureReference... featureReferences) {
      Assert.state(
          ArrayUtils.isNotEmpty(featureReferences),
          "'featureReferences' must not be null or empty array");
      this.feastServingClient = feastServingClient;
      this.featureReferences = Set.of(featureReferences);
    }

    @Override
    public EntityBuilder entity(final String id, Set<Object> values) {
      Assert.hasText(id, "entity id required");
      Assert.state(CollectionUtils.isNotEmpty(values), "'values' must not be null or empty list");
      this.entities.computeIfAbsent(id, fv -> new LinkedHashSet<>()).addAll(values);
      return null;
    }

    @Override
    public EntityBuilder entity(final String id, Object... values) {
      Assert.hasText(id, "entity id required");
      Assert.state(ArrayUtils.isNotEmpty(values), "'values' must not be null or empty array");
      this.entities.computeIfAbsent(id, fv -> new LinkedHashSet<>()).addAll(List.of(values));
      return this;
    }

    @Override
    public GetFeaturesBuilder entities(final Map<String, List<Object>> entities) {
      Assert.state(MapUtils.isNotEmpty(entities), "'entities' must not be null or empty array");
      entities.forEach(this::entity);
      return this;
    }

    @Override
    public OnlineFeatures get() {
      OnlineFeaturesRequest onlineFeaturesRequest =
          OnlineFeaturesRequest.of(this.featureService, this.featureReferences, this.entities);
      return this.feastServingClient.getOnlineFeatures(onlineFeaturesRequest);
    }
  }
}
