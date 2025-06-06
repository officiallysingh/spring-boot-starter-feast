package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import feast.proto.core.DataSourceProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@JsonTypeName("REQUEST_SOURCE")
@ToString(callSuper = true)
@Valid
public class RequestSource extends DataSource {

  @Schema(description = "Request fields Schema")
  @NotEmpty
  private final List<@Valid Feature> schema;

  private RequestSource(
      final String name,
      final List<Feature> schema,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, DataSourceProto.DataSource.SourceType.REQUEST_SOURCE, description, owner, tags);
    this.schema = schema;
  }

  @JsonCreator
  public static RequestSource of(
      @Schema(description = "Request Source name", example = "vals_to_add") @JsonProperty("name")
          final String name,
      @JsonProperty("schema") final List<Feature> schema,
      @Schema(description = "Request Source description", example = "Request Source for testing")
          @JsonProperty("description")
          final String description,
      @Schema(description = "Request Source Owner name", example = "Rajveer Singh")
          @JsonProperty("owner")
          final String owner,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new RequestSource(name, schema, description, owner, tags);
  }

  // Asking only for required arguments
  public static RequestSource of(final String name, final List<Feature> schema) {
    return new RequestSource(name, schema, null, null, null);
  }

  public List<Feature> getSchema() {
    return CollectionUtils.isNotEmpty(this.schema)
        ? Collections.unmodifiableList(this.schema)
        : Collections.emptyList();
  }
}
