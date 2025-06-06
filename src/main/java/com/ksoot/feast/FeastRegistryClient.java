package com.ksoot.feast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import com.ksoot.feast.config.FeastProperties;
import com.ksoot.feast.dto.registry.*;
import com.ksoot.feast.dto.registry.builder.*;
import feast.proto.core.*;
import feast.registry.RegistryServerGrpc;
import feast.registry.RegistryServerOuterClass;
import io.grpc.StatusRuntimeException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
@RequiredArgsConstructor
public class FeastRegistryClient {

  private final RegistryServerGrpc.RegistryServerBlockingStub registryServerBlockingStub;

  private final EntityRequestBuilder entityRequestBuilder;

  private final DataSourceRequestBuilder dataSourceRequestBuilder;

  private final FeatureViewRequestBuilder featureViewRequestBuilder;

  private final FeatureServiceRequestBuilder featureServiceRequestBuilder;

  private final FeastProperties feastProperties;

  public FeastRegistryClient(
      final RegistryServerGrpc.RegistryServerBlockingStub registryServerBlockingStub,
      //      final TypeConverter<ValueMetadata, ValueProto.ValueType.Enum> feastTypeConverter,
      final FeastProperties feastProperties,
      final ObjectMapper objectMapper) {
    this.registryServerBlockingStub = registryServerBlockingStub;
    this.feastProperties = feastProperties;
    final FeatureSpecBuilder featureSpecBuilder = new FeatureSpecBuilder();
    this.entityRequestBuilder = new EntityRequestBuilder(this.feastProperties);
    this.dataSourceRequestBuilder =
        new DataSourceRequestBuilder(featureSpecBuilder, this.feastProperties, objectMapper);
    this.featureViewRequestBuilder =
        new FeatureViewRequestBuilder(featureSpecBuilder, this.feastProperties);
    this.featureServiceRequestBuilder = new FeatureServiceRequestBuilder(this.feastProperties);
  }

  public void applyEntity(@Valid final Entity entity) {
    final RegistryServerOuterClass.ApplyEntityRequest applyEntityRequest =
        this.entityRequestBuilder.buildApplyEntityRequest(entity);
    log.info(
        "Creating Entity: [project: {}, name: {}, description: {}, joinKey: {}]",
        this.feastProperties.getProject(),
        entity.getName(),
        entity.getDescription(),
        entity.getJoinKey());

    final Empty result = this.registryServerBlockingStub.applyEntity(applyEntityRequest);
    log.info("Created entity: [name: {}]", entity.getName());
    this.refreshRegistry();
  }

  public boolean entityExists(@NotEmpty final String name) {
    try {
      this.registryServerBlockingStub.getEntity(
          this.entityRequestBuilder.buildGetEntityRequest(name));
      return true;
    } catch (final StatusRuntimeException e) {
      return false;
    }
  }

  public EntityProto.Entity getEntity(@NotEmpty final String name) {
    final EntityProto.Entity entity =
        this.registryServerBlockingStub.getEntity(
            this.entityRequestBuilder.buildGetEntityRequest(name));
    return entity;
  }

  public RegistryServerOuterClass.ListEntitiesResponse listEntities() {
    final RegistryServerOuterClass.ListEntitiesResponse entities =
        this.registryServerBlockingStub.listEntities(
            this.entityRequestBuilder.buildListEntitiesRequest());
    return entities;
  }

  public void deleteEntity(@NotEmpty final String name) {
    final Empty result =
        this.registryServerBlockingStub.deleteEntity(
            this.entityRequestBuilder.buildDeleteEntityRequest(name));
    log.info("Deleted Entity: [name: {}]", name);
  }

  public void applyDataSource(@Valid final DataSource dataSource) {
    if (dataSource instanceof PostgresSqlSource req) {
      this.applyPostgresSqlSource(req);
    } else if (dataSource instanceof PushSource req) {
      this.applyPushSource(req);
    } else if (dataSource instanceof KafkaSource req) {
      this.applyKafkaSource(req);
    } else if (dataSource instanceof RequestSource req) {
      this.applyRequestSource(req);
    } else if (dataSource instanceof SparkSource req) {
      this.applySparkSource(req);
    } else {
      throw new IllegalArgumentException("Unsupported DataSource type: " + dataSource.getType());
    }
    this.refreshRegistry();
  }

  private void applyPostgresSqlSource(@Valid final PostgresSqlSource postgresSqlSourceRequest) {
    final RegistryServerOuterClass.ApplyDataSourceRequest applyDataSourceRequest;
    try {
      applyDataSourceRequest =
          this.dataSourceRequestBuilder.buildApplyPostgresDataSourceRequest(
              postgresSqlSourceRequest);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    log.info(
        "Creating PostgresDataSource: [project: {},name: {}, description: {}, table: {}, query: {}]",
        this.feastProperties.getProject(),
        postgresSqlSourceRequest.getName(),
        postgresSqlSourceRequest.getDescription(),
        postgresSqlSourceRequest.getTable(),
        postgresSqlSourceRequest.getQuery());

    final Empty result = this.registryServerBlockingStub.applyDataSource(applyDataSourceRequest);
    log.info("Created PostgresDataSource: [name: {}]", postgresSqlSourceRequest.getName());
  }

  private void applySparkSource(@Valid final SparkSource sparkSourceRequest) {
    final RegistryServerOuterClass.ApplyDataSourceRequest applyDataSourceRequest;
    applyDataSourceRequest =
        this.dataSourceRequestBuilder.buildSparkSourceRequest(sparkSourceRequest);

    log.info(
        "Creating SparkSource: [project: {},name: {}, description: {}, table: {}, query: {}, filePath: {}, fileFormat: {}]",
        this.feastProperties.getProject(),
        sparkSourceRequest.getName(),
        sparkSourceRequest.getDescription(),
        sparkSourceRequest.getTable(),
        sparkSourceRequest.getQuery(),
        sparkSourceRequest.getFilePath(),
        sparkSourceRequest.getFileFormat());

    final Empty result = this.registryServerBlockingStub.applyDataSource(applyDataSourceRequest);
    log.info("Created SparkSource: [name: {}]", sparkSourceRequest.getName());
  }

  private void applyPushSource(@Valid final PushSource pushSourceRequest) {
    final DataSourceProto.DataSource batchSource =
        this.getDataSource(pushSourceRequest.getBatchSource());

    final RegistryServerOuterClass.ApplyDataSourceRequest applyDataSourceRequest =
        this.dataSourceRequestBuilder.buildApplyPushSourceRequest(pushSourceRequest, batchSource);

    log.info(
        "Creating PushSource: [project: {}, name: {}, description: {}, batchSource: {}]",
        this.feastProperties.getProject(),
        pushSourceRequest.getName(),
        pushSourceRequest.getDescription(),
        pushSourceRequest.getBatchSource());

    final Empty result = this.registryServerBlockingStub.applyDataSource(applyDataSourceRequest);
    log.info("Created PushSource: [name: {}]", pushSourceRequest.getName());
  }

  private void applyKafkaSource(@Valid final KafkaSource kafkaSourceRequest) {
    final DataSourceProto.DataSource batchSource =
        this.getDataSource(kafkaSourceRequest.getBatchSource());

    final RegistryServerOuterClass.ApplyDataSourceRequest applyDataSourceRequest =
        this.dataSourceRequestBuilder.buildApplyKafkaSourceRequest(kafkaSourceRequest, batchSource);

    log.info(
        "Creating KafkaSource: [project: {}, name: {}, description: {}, batchSource: {}]",
        this.feastProperties.getProject(),
        kafkaSourceRequest.getName(),
        kafkaSourceRequest.getDescription(),
        kafkaSourceRequest.getBatchSource());

    final Empty result = this.registryServerBlockingStub.applyDataSource(applyDataSourceRequest);
    log.info("Created KafkaSource: [name: {}]", kafkaSourceRequest.getName());
  }

  private void applyRequestSource(@Valid final RequestSource requestSourceRequest) {
    final RegistryServerOuterClass.ApplyDataSourceRequest applyDataSourceRequest =
        this.dataSourceRequestBuilder.buildApplyRequestSourceRequest(requestSourceRequest);

    log.info(
        "Creating RequestSource: [project: {}, name: {}, description: {}]",
        this.feastProperties.getProject(),
        requestSourceRequest.getName(),
        requestSourceRequest.getDescription());

    final Empty result = this.registryServerBlockingStub.applyDataSource(applyDataSourceRequest);
    log.info("Created RequestSource: [name: {}]", requestSourceRequest.getName());
  }

  public boolean dataSourceExists(@NotEmpty final String name) {
    try {
      this.registryServerBlockingStub.getDataSource(
          this.dataSourceRequestBuilder.buildGetDataSourceRequest(name));
      return true;
    } catch (final StatusRuntimeException e) {
      return false;
    }
  }

  public DataSourceProto.DataSource getDataSource(@NotEmpty final String name) {
    final DataSourceProto.DataSource dataSource =
        this.registryServerBlockingStub.getDataSource(
            this.dataSourceRequestBuilder.buildGetDataSourceRequest(name));
    return dataSource;
  }

  public RegistryServerOuterClass.ListDataSourcesResponse listDataSources() {
    final RegistryServerOuterClass.ListDataSourcesResponse dataSources =
        this.registryServerBlockingStub.listDataSources(
            this.dataSourceRequestBuilder.buildListDataSourcesRequest());
    return dataSources;
  }

  public void deleteDataSource(@NotEmpty final String name) {
    final Empty result =
        this.registryServerBlockingStub.deleteDataSource(
            this.dataSourceRequestBuilder.buildDeleteDataSourceRequest(name));

    log.info("Deleted DataSource: [name: {}]", name);
    this.refreshRegistry();
  }

  public void applyFeatureView(@Valid final FeatureView featureView) {
    final List<EntityProto.Entity> entities =
        featureView.getEntities().stream().map(this::getEntity).toList();

    final DataSourceProto.DataSource dataSource = this.getDataSource(featureView.getDataSource());

    final RegistryServerOuterClass.ApplyFeatureViewRequest applyFeatureViewRequest =
        this.featureViewRequestBuilder.buildApplyFeatureViewRequest(
            featureView, entities, dataSource);

    final Empty result = this.registryServerBlockingStub.applyFeatureView(applyFeatureViewRequest);
    log.info("Created FeatureView: [name: {}]", featureView.getName());
    this.refreshRegistry();
  }

  public boolean featureViewExists(@NotEmpty final String name) {
    try {
      this.registryServerBlockingStub.getFeatureView(
          this.featureViewRequestBuilder.buildGetFeatureViewRequest(name));
      return true;
    } catch (final StatusRuntimeException e) {
      return false;
    }
  }

  public FeatureViewProto.FeatureView getFeatureView(@NotEmpty final String name) {
    final FeatureViewProto.FeatureView featureView =
        this.registryServerBlockingStub.getFeatureView(
            this.featureViewRequestBuilder.buildGetFeatureViewRequest(name));
    return featureView;
  }

  public RegistryServerOuterClass.ListFeatureViewsResponse listFeatureViews() {
    final RegistryServerOuterClass.ListFeatureViewsResponse featureViews =
        this.registryServerBlockingStub.listFeatureViews(
            this.featureViewRequestBuilder.buildListFeatureViewsRequest());
    return featureViews;
  }

  public void deleteFeatureView(@NotEmpty final String name) {
    final Empty result =
        this.registryServerBlockingStub.deleteFeatureView(
            this.featureViewRequestBuilder.buildDeleteFeatureViewRequest(name));

    log.info("Deleted FeatureView: [name: {}]", name);
    this.refreshRegistry();
  }

  public void applyFeatureService(@Valid final FeatureService featureService) {
    // It will throw exception if some feature view is not found
    final List<FeatureViewProto.FeatureView> featureViews =
        featureService.getFeatureReferences().keySet().stream().map(this::getFeatureView).toList();

    final RegistryServerOuterClass.ApplyFeatureServiceRequest applyFeatureServiceRequest =
        this.featureServiceRequestBuilder.buildApplyFeatureServiceRequest(
            featureService, featureViews);

    final Empty result =
        this.registryServerBlockingStub.applyFeatureService(applyFeatureServiceRequest);
    log.info("Created FeatureService: [name: {}]", featureService.getName());
    log.info("Created FeatureService: [name: {}]", featureService.getName());
    this.refreshRegistry();
  }

  public boolean featureServiceExists(@NotEmpty final String name) {
    try {
      this.registryServerBlockingStub.getFeatureService(
          this.featureServiceRequestBuilder.buildGetFeatureServiceRequest(name));
      return true;
    } catch (final StatusRuntimeException e) {
      return false;
    }
  }

  public FeatureServiceProto.FeatureService getFeatureService(@NotEmpty final String name) {
    final FeatureServiceProto.FeatureService featureService =
        this.registryServerBlockingStub.getFeatureService(
            this.featureServiceRequestBuilder.buildGetFeatureServiceRequest(name));
    return featureService;
  }

  public RegistryServerOuterClass.ListFeatureServicesResponse listFeatureServices() {
    final RegistryServerOuterClass.ListFeatureServicesResponse featureServices =
        this.registryServerBlockingStub.listFeatureServices(
            this.featureServiceRequestBuilder.buildListFeatureServicesRequest());
    return featureServices;
  }

  public void deleteFeatureService(@NotEmpty final String name) {
    final Empty result =
        this.registryServerBlockingStub.deleteFeatureService(
            this.featureServiceRequestBuilder.buildDeleteFeatureServiceRequest(name));

    log.info("Deleted FeatureService: [name: {}]", name);
    this.refreshRegistry();
  }

  public void refreshRegistry() {
    final Empty result =
        this.registryServerBlockingStub.refresh(
            RegistryServerOuterClass.RefreshRequest.newBuilder()
                .setProject(this.feastProperties.getProject())
                .build());

    log.info("Registry refreshed for Project: {}", this.feastProperties.getProject());
  }
}
