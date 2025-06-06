package com.ksoot.feast.dto.registry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Map;
import lombok.*;
import org.apache.commons.collections4.MapUtils;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "name")
@ToString
public class AbstractFeastRequest {

  @NotEmpty protected final String name;

  protected final String description;

  protected final String owner;

  @Schema(description = "Tags for easier lookup", example = "{\"team\": \"driver_performance\"}")
  protected final Map<@NotEmpty String, @NotEmpty String> tags;

  public Map<String, String> getTags() {
    return MapUtils.isNotEmpty(this.tags)
        ? Collections.unmodifiableMap(this.tags)
        : Collections.emptyMap();
  }
}
