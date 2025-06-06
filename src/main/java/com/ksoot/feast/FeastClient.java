package com.ksoot.feast;

import com.ksoot.feast.dsl.*;
import com.ksoot.feast.dto.registry.DataSource;
import com.ksoot.feast.dto.registry.Entity;
import com.ksoot.feast.dto.registry.FeatureService;
import com.ksoot.feast.dto.registry.FeatureView;
import com.ksoot.feast.dto.serving.*;
import feast.proto.core.DataSourceProto;
import feast.proto.core.EntityProto;
import feast.proto.core.FeatureServiceProto;
import feast.proto.core.FeatureViewProto;
import feast.registry.RegistryServerOuterClass;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@RequiredArgsConstructor
public class FeastClient {

  private final FeastRegistryClient feastRegistryClient;

  private final FeastServingClient feastServingClient;

  //  ---------- Feast Registry operations ----------
  //  Entities
  public void applyEntity(final Entity entity) {
    this.feastRegistryClient.applyEntity(entity);
  }

  public EntityDsl.JoinKeyBuilder entity(final String name) {
    return new EntityDsl.Executor(this.feastRegistryClient, name);
  }

  public boolean entityExists(final String name) {
    return this.feastRegistryClient.entityExists(name);
  }

  public EntityProto.Entity getEntity(final String name) {
    return this.feastRegistryClient.getEntity(name);
  }

  public RegistryServerOuterClass.ListEntitiesResponse listEntities() {
    return this.feastRegistryClient.listEntities();
  }

  public void deleteEntity(final String name) {
    this.feastRegistryClient.deleteEntity(name);
  }

  //  Data Sources
  public void applyDataSource(final DataSource dataSource) {
    this.feastRegistryClient.applyDataSource(dataSource);
  }

  public PostgresSqlSourceDsl.QueryOrTableBuilder postgresSqlSource(final String name) {
    return new PostgresSqlSourceDsl.Executor(this.feastRegistryClient, name);
  }

  public SparkSourceDsl.Executor sparkSource(final String name) {
    return new SparkSourceDsl.Executor(this.feastRegistryClient, name);
  }

  public PushSourceDsl.BatchSourceBuilder pushSource(final String name) {
    return new PushSourceDsl.Executor(this.feastRegistryClient, name);
  }

  public KafkaSourceDsl.BootstrapServersBuilder kafkaSource(final String name) {
    return new KafkaSourceDsl.Executor(this.feastRegistryClient, name);
  }

  public RequestSourceDsl.Executor requestSource(final String name) {
    return new RequestSourceDsl.Executor(this.feastRegistryClient, name);
  }

  public boolean dataSourceExists(final String name) {
    return this.feastRegistryClient.dataSourceExists(name);
  }

  public DataSourceProto.DataSource getDataSource(final String name) {
    return this.feastRegistryClient.getDataSource(name);
  }

  public RegistryServerOuterClass.ListDataSourcesResponse listDataSources() {
    return this.feastRegistryClient.listDataSources();
  }

  public void deleteDataSource(final String name) {
    this.feastRegistryClient.deleteDataSource(name);
  }

  //  Feature Views
  public void applyFeatureView(final FeatureView featureView) {
    this.feastRegistryClient.applyFeatureView(featureView);
  }

  public FeatureViewDsl.EntitiesBuilder featureView(final String name) {
    return new FeatureViewDsl.Executor(this.feastRegistryClient, name);
  }

  public boolean featureViewExists(final String name) {
    return this.feastRegistryClient.featureViewExists(name);
  }

  public FeatureViewProto.FeatureView getFeatureView(final String name) {
    return this.feastRegistryClient.getFeatureView(name);
  }

  public RegistryServerOuterClass.ListFeatureViewsResponse listFeatureViews() {
    return this.feastRegistryClient.listFeatureViews();
  }

  public void deleteFeatureView(final String name) {
    this.feastRegistryClient.deleteFeatureView(name);
  }

  //  Feature Services
  public void applyFeatureService(final FeatureService featureService) {
    this.feastRegistryClient.applyFeatureService(featureService);
  }

  public FeatureServiceDsl.FeatureReferenceBuilder featureService(final String name) {
    return new FeatureServiceDsl.Executor(this.feastRegistryClient, name);
  }

  public boolean featureServiceExists(final String name) {
    return this.feastRegistryClient.featureServiceExists(name);
  }

  public FeatureServiceProto.FeatureService getFeatureService(final String name) {
    return this.feastRegistryClient.getFeatureService(name);
  }

  public RegistryServerOuterClass.ListFeatureServicesResponse listFeatureServices() {
    return this.feastRegistryClient.listFeatureServices();
  }

  public void deleteFeatureService(final String name) {
    this.feastRegistryClient.deleteFeatureService(name);
  }

  //  Misc registry operations
  public void refreshRegistry() {
    this.feastRegistryClient.refreshRegistry();
  }

  //  ---------- Feast Serving operations ----------
  // To check health of Serving Server
  public boolean isServing() {
    return this.feastServingClient.isServing();
  }

  public void materialize(final MaterializationRequest materializationRequest) {
    this.feastServingClient.materialize(materializationRequest);
  }

  public MaterializationDsl.EndTimestampBuilder materializeFrom(
      final OffsetDateTime startTimestamp) {
    return MaterializationDsl.of(this.feastServingClient, startTimestamp);
  }

  public MaterializationDsl.FeatureViewBuilder materializeIncremental(
      final OffsetDateTime endTimestamp) {
    return MaterializationDsl.ofIncremental(this.feastServingClient, endTimestamp);
  }

  public MaterializationDsl.FeatureViewBuilder materializeIncrementalTillNow() {
    return MaterializationDsl.ofIncremental(this.feastServingClient, OffsetDateTime.now());
  }

  public void pushFeatures(final PushFeaturesRequest pushFeaturesRequest) {
    this.feastServingClient.pushFeatures(pushFeaturesRequest);
  }

  public PushFeaturesDsl.PushModeBuilder pushTo(final String pushSourceName) {
    return new PushFeaturesDsl.Executor(this.feastServingClient, pushSourceName);
  }

  public OnlineFeatures getOnlineFeatures(final OnlineFeaturesRequest onlineFeaturesRequest) {
    return this.feastServingClient.getOnlineFeatures(onlineFeaturesRequest);
  }

  public GetOnlineFeaturesDsl.Executor onlineFeatures(final String featureService) {
    return new GetOnlineFeaturesDsl.Executor(this.feastServingClient, featureService);
  }

  public GetOnlineFeaturesDsl.Executor onlineFeatures(
      final Set<FeatureReference> featureReferences) {
    return new GetOnlineFeaturesDsl.Executor(this.feastServingClient, featureReferences);
  }

  public GetOnlineFeaturesDsl.Executor onlineFeatures(final FeatureReference... featureReferences) {
    return new GetOnlineFeaturesDsl.Executor(this.feastServingClient, featureReferences);
  }
}
