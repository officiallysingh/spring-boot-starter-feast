package com.ksoot.feast.dto.registry.builder;

import static com.ksoot.feast.FeatureStoreConstants.DATA_SOURCE_CLASS_TYPE_POSTGRESQL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.ksoot.feast.config.FeastProperties;
import com.ksoot.feast.dto.registry.*;
import feast.proto.core.DataFormatProto;
import feast.proto.core.DataSourceProto;
import feast.proto.core.FeatureProto;
import feast.registry.RegistryServerOuterClass;
import jakarta.validation.constraints.NotEmpty;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class DataSourceRequestBuilder {

  private final FeatureSpecBuilder featureSpecBuilder;

  private final FeastProperties feastProperties;

  private final ObjectMapper objectMapper;

  public RegistryServerOuterClass.ApplyDataSourceRequest buildApplyPostgresDataSourceRequest(
      final PostgresSqlSource postgresSqlSourceRequest) throws JsonProcessingException {
    // Create a JSON structure for the PostgreSQL source
    final PostgresSqlSource.SqlSource sqlSource = postgresSqlSourceRequest.sqlSource();
    final String sqlSourceJson = this.objectMapper.writeValueAsString(sqlSource);

    // Encode the JSON string to Base64
    final String encodedSqlSource = Base64.getEncoder().encodeToString(sqlSourceJson.getBytes());
    log.info("PostgresDataSource CustomOptions Encoded Configuration: {}", encodedSqlSource);

    final DataSourceProto.DataSource.Builder dataSourceProtoBuilder =
        this.dataSourceProtoBuilder(postgresSqlSourceRequest);
    final DataSourceProto.DataSource dataSource =
        dataSourceProtoBuilder
            .setType(DataSourceProto.DataSource.SourceType.CUSTOM_SOURCE)
            .setDataSourceClassType(DATA_SOURCE_CLASS_TYPE_POSTGRESQL)
            .setTimestampField(postgresSqlSourceRequest.getEventTimestampField())
            .setCreatedTimestampColumn(postgresSqlSourceRequest.getCreatedTimestampField())
            .setCustomOptions(
                DataSourceProto.DataSource.CustomSourceOptions.newBuilder()
                    .setConfiguration(ByteString.copyFrom(sqlSourceJson.getBytes()))
                    .build())
            .build();
    return this.applyDataSourceRequestBuilder().setDataSource(dataSource).build();
  }

  public RegistryServerOuterClass.ApplyDataSourceRequest buildApplyPushSourceRequest(
      final PushSource pushSourceRequest, final DataSourceProto.DataSource batchSource) {
    final DataSourceProto.DataSource.Builder dataSourceProtoBuilder =
        this.dataSourceProtoBuilder(pushSourceRequest);
    final DataSourceProto.DataSource dataSource =
        dataSourceProtoBuilder
            .setType(DataSourceProto.DataSource.SourceType.PUSH_SOURCE)
            .setBatchSource(batchSource)
            .build();
    return this.applyDataSourceRequestBuilder().setDataSource(dataSource).build();
  }

  public RegistryServerOuterClass.ApplyDataSourceRequest buildSparkSourceRequest(
      final SparkSource sparkSourceRequest) {
    final DataSourceProto.DataSource.Builder dataSourceProtoBuilder =
        this.dataSourceProtoBuilder(sparkSourceRequest);

    DataSourceProto.DataSource.SparkOptions.Builder sparkOptionsBuilder =
        DataSourceProto.DataSource.SparkOptions.newBuilder();
    if (sparkSourceRequest.isTableType()) {
      sparkOptionsBuilder.setTable(sparkSourceRequest.getTable());
    } else if (sparkSourceRequest.isQueryType()) {
      sparkOptionsBuilder.setQuery(sparkSourceRequest.getQuery());
    } else {
      sparkOptionsBuilder.setPath(sparkSourceRequest.getFilePath());
      sparkOptionsBuilder.setFileFormat(sparkSourceRequest.getFileFormat());
    }

    final DataSourceProto.DataSource dataSource =
        dataSourceProtoBuilder
            .setType(DataSourceProto.DataSource.SourceType.BATCH_SPARK)
            .setTimestampField(sparkSourceRequest.getEventTimestampField())
            .setCreatedTimestampColumn(sparkSourceRequest.getCreatedTimestampField())
            .setSparkOptions(sparkOptionsBuilder.build())
            .build();
    return this.applyDataSourceRequestBuilder().setDataSource(dataSource).build();
  }

  public RegistryServerOuterClass.ApplyDataSourceRequest buildApplyKafkaSourceRequest(
      final KafkaSource kafkaSourceRequest, final DataSourceProto.DataSource batchSource) {
    final DataSourceProto.DataSource.Builder dataSourceProtoBuilder =
        this.dataSourceProtoBuilder(kafkaSourceRequest);
    final DataSourceProto.DataSource dataSource =
        dataSourceProtoBuilder
            .setType(DataSourceProto.DataSource.SourceType.STREAM_KAFKA)
            .setTimestampField(kafkaSourceRequest.getEventTimestampField())
            .setBatchSource(batchSource)
            .setKafkaOptions(
                DataSourceProto.DataSource.KafkaOptions.newBuilder()
                    .setKafkaBootstrapServers(kafkaSourceRequest.getBootstrapServers())
                    .setTopic(kafkaSourceRequest.getTopicName())
                    .setMessageFormat(
                        DataFormatProto.StreamFormat.newBuilder()
                            .setJsonFormat(
                                DataFormatProto.StreamFormat.JsonFormat.newBuilder()
                                    .setSchemaJson(kafkaSourceRequest.getSchemaJson())
                                    .build())
                            .build())
                    .setWatermarkDelayThreshold(
                        Duration.newBuilder()
                            .setSeconds(
                                kafkaSourceRequest.getWatermarkDelayThreshold().getSeconds())
                            .setNanos(kafkaSourceRequest.getWatermarkDelayThreshold().getNano())
                            .build())
                    .build())
            .build();

    return this.applyDataSourceRequestBuilder().setDataSource(dataSource).build();
  }

  public RegistryServerOuterClass.ApplyDataSourceRequest buildApplyRequestSourceRequest(
      final RequestSource requestSourceRequest) {
    final DataSourceProto.DataSource.Builder dataSourceProtoBuilder =
        this.dataSourceProtoBuilder(requestSourceRequest);
    final List<FeatureProto.FeatureSpecV2> schemaFields =
        requestSourceRequest.getSchema().stream()
            .map(this.featureSpecBuilder::buildFeatureSpec)
            .toList();
    final DataSourceProto.DataSource dataSource =
        dataSourceProtoBuilder
            .setType(DataSourceProto.DataSource.SourceType.REQUEST_SOURCE)
            .setRequestDataOptions(
                DataSourceProto.DataSource.RequestDataOptions.newBuilder()
                    .addAllSchema(schemaFields)
                    .build())
            .build();
    return this.applyDataSourceRequestBuilder().setDataSource(dataSource).build();
  }

  private DataSourceProto.DataSource.Builder dataSourceProtoBuilder(final DataSource dataSource) {
    DataSourceProto.DataSource.Builder builder =
        DataSourceProto.DataSource.newBuilder()
            .setProject(this.feastProperties.getProject())
            .setName(dataSource.getName());
    if (StringUtils.isNotBlank(dataSource.getDescription())) {
      builder.setDescription(dataSource.getDescription());
    }
    if (StringUtils.isNotBlank(dataSource.getOwner())) {
      builder.setOwner(dataSource.getOwner());
    }
    if (MapUtils.isNotEmpty(dataSource.getTags())) {
      builder.putAllTags(dataSource.getTags());
    }
    return builder;
  }

  private RegistryServerOuterClass.ApplyDataSourceRequest.Builder applyDataSourceRequestBuilder() {
    return RegistryServerOuterClass.ApplyDataSourceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setCommit(true);
  }

  public RegistryServerOuterClass.GetDataSourceRequest buildGetDataSourceRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.GetDataSourceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .setName(name)
        .build();
  }

  public RegistryServerOuterClass.ListDataSourcesRequest buildListDataSourcesRequest() {
    return RegistryServerOuterClass.ListDataSourcesRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .build();
  }

  public RegistryServerOuterClass.DeleteDataSourceRequest buildDeleteDataSourceRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.DeleteDataSourceRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setName(name)
        .setCommit(true)
        .build();
  }
}
