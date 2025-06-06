package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.Feature;
import com.ksoot.feast.dto.registry.RequestSource;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

public class RequestSourceDsl {

  public interface SchemaBuilder {

    DescriptionBuilder<RequestSource> schema(final List<Feature> schema);

    DescriptionBuilder<RequestSource> schema(final Feature... schema);
  }

  public static class Executor extends AbstractRegistryDslExecutor<RequestSource>
      implements SchemaBuilder {

    private final List<Feature> schema = new LinkedList<>();

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    @Override
    public DescriptionBuilder<RequestSource> schema(final List<Feature> schema) {
      this.schema.addAll(schema);
      return this;
    }

    @Override
    public DescriptionBuilder<RequestSource> schema(final Feature... schema) {
      Assert.state(ArrayUtils.isNotEmpty(schema), "'schema' must not be null or empty array");
      this.schema.addAll(List.of(schema));
      return this;
    }

    @Override
    public RequestSource build() {
      return RequestSource.of(this.name, this.schema, this.description, this.owner, this.tags);
    }

    @Override
    public void apply() {
      final RequestSource requestSourceRequest = this.build();
      this.feastRegistryClient.applyDataSource(requestSourceRequest);
    }
  }
}
