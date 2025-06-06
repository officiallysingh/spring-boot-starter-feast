package com.ksoot.feast;

import com.ksoot.feast.dsl.Applier;
import com.ksoot.feast.dto.registry.Entity;
import com.ksoot.feast.dto.registry.FeatureService;
import com.ksoot.feast.dto.registry.FeatureView;
import com.ksoot.feast.dto.registry.PushSource;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@Slf4j
public class FeatureStoreDsl {

  public interface FeatureViewsBuilder {

    //        PushSourcesBuilder featureViews(final String name, final String... featureViews);

    PushSourcesBuilder featureViews(
        final FeatureView featureView, final FeatureView... featureViews);

    PushSourcesBuilder featureViews(final Collection<FeatureView> featureViews);
  }

  public interface PushSourcesBuilder extends FeatureServicesBuilder {

    //        FeatureServicesBuilder pushSources(final String name, final PushSourceRequest...
    // pushSources);

    FeatureServicesBuilder pushSources(
        final PushSource pushSource, final PushSource... pushSources);

    FeatureServicesBuilder pushSources(final Collection<PushSource> pushSources);
  }

  public interface FeatureServicesBuilder extends Applier {

    Applier featureService(final String featureService);

    Applier featureServices(
        final FeatureService featureService, final FeatureService... featureServices);

    Applier featureServices(final Collection<FeatureService> featureServices);
  }

  @RequiredArgsConstructor
  public static class Executor implements FeatureViewsBuilder, PushSourcesBuilder, Applier {

    private final FeastRegistryClient feastRegistryClient;

    private final Set<Entity> entities;

    private final Set<FeatureView> featureViews;

    private final Set<PushSource> pushSources;

    private final Set<FeatureService> featureServices;

    private String featureService;

    Executor(
        final FeastRegistryClient feastRegistryClient,
        //        final ObjectProvider<IcebergClient> icebergClientProvider,
        final Entity entity,
        final Entity... entities) {
      Assert.notNull(entity, "'entity' must not be null");
      Assert.noNullElements(entities, "'entities' must not contain null elements");
      this.feastRegistryClient = feastRegistryClient;
      this.entities = new LinkedHashSet<>();
      this.entities.add(entity);
      this.entities.addAll(Set.of(entities));
      this.featureViews = new LinkedHashSet<>();
      this.pushSources = new LinkedHashSet<>();
      this.featureServices = new LinkedHashSet<>();
    }

    Executor(final FeastRegistryClient feastRegistryClient, final Collection<Entity> entities) {
      Assert.notEmpty(entities, "'entities' must not be null or empty");
      Assert.noNullElements(entities, "'entities' must not contain null elements");
      this.feastRegistryClient = feastRegistryClient;
      this.entities = new LinkedHashSet<>();
      this.entities.addAll(entities);
      this.featureViews = new LinkedHashSet<>();
      this.pushSources = new LinkedHashSet<>();
      this.featureServices = new LinkedHashSet<>();
    }

    @Override
    public PushSourcesBuilder featureViews(
        final FeatureView featureView, final FeatureView... featureViews) {
      Assert.notNull(featureView, "'featureView' must not be null");
      Assert.noNullElements(featureViews, "'featureViews' must not contain null elements");
      this.featureViews.add(featureView);
      this.featureViews.addAll(Set.of(featureViews));
      return this;
    }

    @Override
    public PushSourcesBuilder featureViews(final Collection<FeatureView> featureViews) {
      Assert.notEmpty(featureViews, "'featureViews' must not be null or empty");
      Assert.noNullElements(featureViews, "'featureViews' must not contain null elements");
      this.featureViews.addAll(featureViews);
      return this;
    }

    @Override
    public FeatureServicesBuilder pushSources(
        final PushSource pushSource, final PushSource... pushSources) {
      Assert.notNull(pushSource, "'pushSource' must not be null");
      Assert.noNullElements(pushSources, "'pushSources' must not contain null elements");
      this.pushSources.add(pushSource);
      this.pushSources.addAll(Set.of(pushSources));
      return this;
    }

    @Override
    public FeatureServicesBuilder pushSources(final Collection<PushSource> pushSources) {
      Assert.notEmpty(pushSources, "'pushSources' must not be null or empty");
      Assert.noNullElements(pushSources, "'pushSources' must not contain null elements");
      this.pushSources.addAll(pushSources);
      return this;
    }

    @Override
    public Applier featureService(final String featureService) {
      Assert.hasText(featureService, "'featureService' must not be null or empty");
      this.featureService = featureService;
      return this;
    }

    @Override
    public Applier featureServices(
        final FeatureService featureService, final FeatureService... featureServices) {
      Assert.notNull(featureService, "'featureService' must not be null");
      Assert.noNullElements(featureServices, "'featureServices' must not contain null elements");
      this.featureServices.add(featureService);
      this.featureServices.addAll(Set.of(featureServices));
      return this;
    }

    @Override
    public Applier featureServices(final Collection<FeatureService> featureServices) {
      Assert.notEmpty(featureServices, "'featureServices' must not be null or empty");
      Assert.noNullElements(featureServices, "'featureServices' must not contain null elements");
      this.featureServices.addAll(featureServices);
      return this;
    }

    @Override
    public void apply() {
      this.entities.forEach(this.feastRegistryClient::applyEntity);
      for (final FeatureView featureView : this.featureViews) {
        //        final Table batchDataSorceTable = this.createBatchSource(featureView);
        this.feastRegistryClient.applyFeatureView(featureView);
      }
      if (CollectionUtils.isNotEmpty(this.pushSources)) {
        this.pushSources.forEach(this.feastRegistryClient::applyDataSource);
      }
      if (StringUtils.isNotBlank(this.featureService)) {
        Set<String> fvs =
            this.featureViews.stream().map(FeatureView::getName).collect(Collectors.toSet());
        this.feastRegistryClient.applyFeatureService(FeatureService.of(this.featureService, fvs));
      } else {
        this.featureServices.forEach(this.feastRegistryClient::applyFeatureService);
      }
    }

    //    private Table createBatchSource(final FeatureView featureView) {
    //      final String name = featureView.getDataSource();
    //      final Set<JoinKey> entityColumns =
    //          this.entities.stream()
    //              .filter(entity -> featureView.getEntities().contains(entity.getName()))
    //              .map(Entity::getJoinKey)
    //              .collect(Collectors.toSet());
    //      if (CollectionUtils.isEmpty(entityColumns)) {
    //        throw new FeastClientException(
    //            "Entity columns found for feature view: " + featureView.getName());
    //      }
    //      final String eventTimestamp = featureView.getEventTimestampField();
    //      final String createdTimestamp = featureView.getCreatedTimestampField();
    //      final Set<Feature> features = featureView.getFeatures();
    //
    //      final IcebergClient icebergClient = this.icebergClientProvider.getIfAvailable();
    //      if (Objects.nonNull(icebergClient)) {
    //        Table table =
    //            icebergClient.createOrUpdateTable(
    //                name, entityColumns, eventTimestamp, createdTimestamp, features);
    //        final String feastTableName =
    //            icebergClient.getCatalogProperties().tablePrefix() + featureView.getDataSource();
    //        final SparkSource sparkSource =
    //            SparkSource.ofTable(
    //                featureView.getDataSource(), feastTableName, eventTimestamp,
    // createdTimestamp);
    //        this.feastRegistryClient.applyDataSource(sparkSource);
    //        return table;
    //      } else {
    //        log.warn("IcebergClient is not available, skipping Iceberg table creation");
    //        return null;
    //      }
    //    }
  }
}
