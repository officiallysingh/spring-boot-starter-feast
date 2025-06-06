package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.Entity;
import com.ksoot.feast.dto.registry.JoinKey;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import feast.proto.types.ValueProto;

public class EntityDsl {

  public interface JoinKeyBuilder {

    DescriptionBuilder<Entity> joinKey(final JoinKey joinKey);

    DescriptionBuilder<Entity> joinKey(
        final String joinKeyName, final ValueProto.ValueType.Enum joinKeyValueType);
  }

  public static class Executor extends AbstractRegistryDslExecutor<Entity>
      implements JoinKeyBuilder {

    private JoinKey joinKey;

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    @Override
    public DescriptionBuilder<Entity> joinKey(final JoinKey joinKey) {
      this.joinKey = joinKey;
      return this;
    }

    @Override
    public DescriptionBuilder<Entity> joinKey(
        final String joinKeyName, final ValueProto.ValueType.Enum joinKeyValueType) {
      this.joinKey = JoinKey.of(joinKeyName, joinKeyValueType);
      return this;
    }

    @Override
    public Entity build() {
      return Entity.of(this.name, this.joinKey, this.description, this.owner, this.tags);
    }

    @Override
    public void apply() {
      final Entity entity = this.build();
      this.feastRegistryClient.applyEntity(entity);
    }
  }
}
