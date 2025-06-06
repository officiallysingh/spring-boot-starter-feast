package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.KafkaSource;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import java.time.Duration;

public class KafkaSourceDsl {

  public interface BootstrapServersBuilder {

    TopicNameBuilder bootstrapServers(final String bootstrapServers);
  }

  public interface TopicNameBuilder {

    SchemaJsonBuilder topicName(final String topicName);
  }

  public interface SchemaJsonBuilder {

    WatermarkDelayThresholdBuilder schemaJson(final String schemaJson);
  }

  public interface WatermarkDelayThresholdBuilder {

    BatchSourceBuilder watermarkDelayThreshold(final Duration watermarkDelayThreshold);
  }

  public interface BatchSourceBuilder {

    EventTimestampFieldBuilder batchSource(final String batchSource);
  }

  public interface EventTimestampFieldBuilder {

    DescriptionBuilder<KafkaSource> eventTimestampField(final String eventTimestampField);
  }

  public static class Executor extends AbstractRegistryDslExecutor<KafkaSource>
      implements BootstrapServersBuilder,
          TopicNameBuilder,
          SchemaJsonBuilder,
          WatermarkDelayThresholdBuilder,
          BatchSourceBuilder,
          EventTimestampFieldBuilder {

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    private String bootstrapServers;

    private String topicName;

    private String schemaJson;

    private Duration watermarkDelayThreshold;

    private String batchSource;

    private String eventTimestampField;

    @Override
    public TopicNameBuilder bootstrapServers(final String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
      return this;
    }

    @Override
    public SchemaJsonBuilder topicName(final String topicName) {
      this.topicName = topicName;
      return this;
    }

    @Override
    public WatermarkDelayThresholdBuilder schemaJson(final String schemaJson) {
      this.schemaJson = schemaJson;
      return this;
    }

    @Override
    public BatchSourceBuilder watermarkDelayThreshold(final Duration watermarkDelayThreshold) {
      this.watermarkDelayThreshold = watermarkDelayThreshold;
      return this;
    }

    @Override
    public EventTimestampFieldBuilder batchSource(final String batchSource) {
      this.batchSource = batchSource;
      return this;
    }

    @Override
    public DescriptionBuilder<KafkaSource> eventTimestampField(final String eventTimestampField) {
      this.eventTimestampField = eventTimestampField;
      return this;
    }

    @Override
    public KafkaSource build() {
      return KafkaSource.of(
          this.name,
          this.bootstrapServers,
          this.topicName,
          this.schemaJson,
          this.watermarkDelayThreshold,
          this.batchSource,
          this.eventTimestampField,
          this.description,
          this.owner,
          this.tags);
    }

    @Override
    public void apply() {
      final KafkaSource kafkaSourceRequest = this.build();
      this.feastRegistryClient.applyDataSource(kafkaSourceRequest);
    }
  }
}
