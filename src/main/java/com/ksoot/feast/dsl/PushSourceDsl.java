package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.PushSource;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;

public class PushSourceDsl {

  public interface BatchSourceBuilder {

    DescriptionBuilder<PushSource> batchSource(final String batchSource);
  }

  public static class Executor extends AbstractRegistryDslExecutor<PushSource>
      implements BatchSourceBuilder {

    private String batchSource;

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    @Override
    public DescriptionBuilder<PushSource> batchSource(final String batchSource) {
      this.batchSource = batchSource;
      return this;
    }

    @Override
    public PushSource build() {
      return PushSource.of(this.name, this.batchSource, this.description, this.owner, this.tags);
    }

    @Override
    public void apply() {
      final PushSource pushSourceRequest = this.build();
      this.feastRegistryClient.applyDataSource(pushSourceRequest);
    }
  }
}
