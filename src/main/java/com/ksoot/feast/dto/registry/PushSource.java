package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import feast.proto.core.DataSourceProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.*;

@Getter
@JsonTypeName("PUSH_SOURCE")
@ToString(callSuper = true)
@Valid
public class PushSource extends DataSource {

  @Schema(description = "Backing Batch Source name", example = "driver_stats_source")
  @NotEmpty
  private final String batchSource;

  private PushSource(
      final String name,
      final String batchSource,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.PUSH_SOURCE, description, owner, tags);
    this.batchSource = batchSource;
  }

  @JsonCreator
  public static PushSource of(
      @Schema(description = "Push Source name", example = "driver_stats_push_source")
          @JsonProperty("name")
          final String name,
      @JsonProperty("batchSource") final String batchSource,
      @Schema(description = "Push Source description", example = "Push Source for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Push Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new PushSource(name, batchSource, description, owner, tags);
  }

  // Asking only for required arguments
  public static PushSource of(final String name, final String batchSource) {
    return new PushSource(name, batchSource, null, null, null);
  }
}
