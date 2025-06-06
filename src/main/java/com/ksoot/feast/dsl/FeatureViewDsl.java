package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.Feature;
import com.ksoot.feast.dto.registry.FeatureView;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import java.time.Duration;
import java.util.*;
import org.apache.commons.lang3.ArrayUtils;

public class FeatureViewDsl {

  public interface EntitiesBuilder {

    FeaturesBuilder entities(final Set<String> entities);

    FeaturesBuilder entities(final String entity, final String... entities);
  }

  public interface FeaturesBuilder {

    DataSourceBuilder features(final Feature feature, final Feature... features);

    DataSourceBuilder features(final Set<Feature> features);
  }

  public interface DataSourceBuilder {

    TtlBuilder dataSource(final String dataSource);
  }

  public interface TtlBuilder {

    OnlineBuilder ttl(final Duration ttl);
  }

  public interface OnlineBuilder {

    EventTimestampFieldBuilder online(final boolean isOnline);

    EventTimestampFieldBuilder online();

    EventTimestampFieldBuilder offline();
  }

  public interface EventTimestampFieldBuilder
      extends CreatedTimestampFieldBuilder, DescriptionBuilder<FeatureView> {

    CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField);
  }

  public interface CreatedTimestampFieldBuilder extends DescriptionBuilder<FeatureView> {

    DescriptionBuilder<FeatureView> createdTimestampField(final String createdTimestampField);
  }

  public static class Executor extends AbstractRegistryDslExecutor<FeatureView>
      implements EntitiesBuilder,
          FeaturesBuilder,
          DataSourceBuilder,
          TtlBuilder,
          OnlineBuilder,
          EventTimestampFieldBuilder {

    private Set<String> entities = new LinkedHashSet<>();

    private final Set<Feature> features = new LinkedHashSet<>();

    private String dataSource;

    private Duration ttl;

    private boolean online;

    private String eventTimestampField;

    private String createdTimestampField;

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    @Override
    public FeaturesBuilder entities(final Set<String> entities) {
      this.entities.addAll(entities);
      return this;
    }

    @Override
    public FeaturesBuilder entities(final String entity, final String... entities) {
      this.entities.add(entity);
      if (ArrayUtils.isNotEmpty(entities)) {
        this.entities.addAll(List.of(entities));
      }
      return this;
    }

    @Override
    public DataSourceBuilder features(final Set<Feature> features) {
      this.features.addAll(features);
      return this;
    }

    @Override
    public DataSourceBuilder features(final Feature feature, final Feature... features) {
      this.features.add(feature);
      if (ArrayUtils.isNotEmpty(features)) {
        this.features.addAll(List.of(features));
      }
      return this;
    }

    @Override
    public TtlBuilder dataSource(final String dataSource) {
      this.dataSource = dataSource;
      return this;
    }

    @Override
    public OnlineBuilder ttl(final Duration ttl) {
      this.ttl = ttl;
      return this;
    }

    //    @Override
    //    public OnlineBuilder timeWindowType(final TimeWindowType timeWindowType) {
    //      this.ttl = this.timeWindowTypeToTtlConverter.convert(timeWindowType);
    //      return this;
    //    }

    @Override
    public EventTimestampFieldBuilder online(final boolean isOnline) {
      this.online = isOnline;
      return this;
    }

    @Override
    public EventTimestampFieldBuilder online() {
      this.online = true;
      return this;
    }

    @Override
    public EventTimestampFieldBuilder offline() {
      this.online = false;
      return this;
    }

    @Override
    public CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField) {
      this.eventTimestampField = eventTimestampField;
      return this;
    }

    @Override
    public DescriptionBuilder<FeatureView> createdTimestampField(
        final String createdTimestampField) {
      this.createdTimestampField = createdTimestampField;
      return this;
    }

    @Override
    public FeatureView build() {
      return FeatureView.of(
          this.name,
          this.entities,
          this.features,
          this.dataSource,
          this.ttl,
          this.online,
          this.eventTimestampField,
          this.createdTimestampField,
          this.description,
          this.owner,
          this.tags);
    }

    @Override
    public void apply() {
      final FeatureView featureView = this.build();
      this.feastRegistryClient.applyFeatureView(featureView);
    }
  }
}
