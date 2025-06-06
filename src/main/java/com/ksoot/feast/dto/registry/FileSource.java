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
@JsonTypeName("BATCH_FILE")
@ToString(callSuper = true)
@Valid
public class FileSource extends DataSource {

  @Schema(description = "Batch file path", example = "driver_stats.parquet")
  @NotEmpty
  private final String path;

  // TODO: Add remaining properties

  private FileSource(
      final String name,
      final String path,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.BATCH_FILE, description, owner, tags);
    this.path = path;
  }

  @JsonCreator
  public static FileSource of(
      @Schema(description = "File Source name", example = "driver_stats_file_batch_source")
          @JsonProperty("name")
          final String name,
      @JsonProperty("path") final String path,
      @JsonProperty("description") final String description,
      @Schema(description = "File Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new FileSource(name, path, description, owner, tags);
  }

  // Asking only for required arguments
  public static FileSource of(final String name, final String path) {
    return new FileSource(name, path, null, null, null);
  }
}
