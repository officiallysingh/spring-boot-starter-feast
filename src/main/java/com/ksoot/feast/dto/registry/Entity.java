package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import feast.proto.types.ValueProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Valid
public class Entity extends AbstractFeastRequest {

  @Schema(description = "Join key")
  @NotNull
  private final JoinKey joinKey;

  private Entity(
      final String name,
      final JoinKey joinKey,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, description, owner, tags);
    this.joinKey = joinKey;
  }

  @JsonCreator
  public static Entity of(
      @Schema(description = "Entity name", example = "driver") @JsonProperty("name")
          final String name,
      @JsonProperty("joinKey") final JoinKey joinKey,
      @Schema(description = "Entity description", example = "Example entity for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Entity Owner name", example = "Rajveer Singh") @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new Entity(name, joinKey, description, owner, tags);
  }

  // Asking only for required arguments
  public static Entity of(final String name, final JoinKey joinKey) {
    return Entity.of(name, joinKey, null, null, null);
  }

  // Asking only for required arguments
  public static Entity of(
      final String name,
      final String joinKeyName,
      final ValueProto.ValueType.Enum joinKeyValueType) {
    return Entity.of(name, JoinKey.of(joinKeyName, joinKeyValueType), null, null, null);
  }
}
