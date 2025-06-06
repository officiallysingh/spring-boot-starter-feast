package com.ksoot.feast.dto.registry.builder;

import com.ksoot.feast.config.FeastProperties;
import com.ksoot.feast.dto.registry.Entity;
import feast.proto.core.EntityProto;
import feast.registry.RegistryServerOuterClass;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class EntityRequestBuilder {

  private final FeastProperties feastProperties;

  public RegistryServerOuterClass.ApplyEntityRequest buildApplyEntityRequest(final Entity entity) {
    final EntityProto.EntitySpecV2.Builder entitySpecBuilder =
        EntityProto.EntitySpecV2.newBuilder()
            .setName(entity.getName())
            .setJoinKey(entity.getJoinKey().name())
            .setValueType(entity.getJoinKey().valueType());
    if (StringUtils.isNotBlank(entity.getDescription())) {
      entitySpecBuilder.setDescription(entity.getDescription());
    }
    if (StringUtils.isNotBlank(entity.getOwner())) {
      entitySpecBuilder.setOwner(entity.getOwner());
    }
    if (MapUtils.isNotEmpty(entity.getTags())) {
      entitySpecBuilder.putAllTags(entity.getTags());
    }
    return RegistryServerOuterClass.ApplyEntityRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setCommit(true)
        .setEntity(EntityProto.Entity.newBuilder().setSpec(entitySpecBuilder.build()).build())
        .build();
  }

  public RegistryServerOuterClass.GetEntityRequest buildGetEntityRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.GetEntityRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setName(name)
        .setAllowCache(true)
        .build();
  }

  public RegistryServerOuterClass.ListEntitiesRequest buildListEntitiesRequest() {
    return RegistryServerOuterClass.ListEntitiesRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setAllowCache(true)
        .build();
  }

  public RegistryServerOuterClass.DeleteEntityRequest buildDeleteEntityRequest(
      @NotEmpty final String name) {
    return RegistryServerOuterClass.DeleteEntityRequest.newBuilder()
        .setProject(this.feastProperties.getProject())
        .setName(name)
        .setCommit(true)
        .build();
  }
}
