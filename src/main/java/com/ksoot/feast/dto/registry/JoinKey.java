package com.ksoot.feast.dto.registry;

import feast.proto.types.ValueProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record JoinKey(
    @Schema(description = "Entity Id name", example = "driver_id") @NotEmpty String name,
    @Schema(description = "Entity Id value Type", example = "INT64") @NotNull
        ValueProto.ValueType.Enum valueType) {

  // A Static factory method, alternative to constructor
  public static JoinKey of(final String name, final ValueProto.ValueType.Enum valueType) {
    return new JoinKey(name, valueType);
  }
}
