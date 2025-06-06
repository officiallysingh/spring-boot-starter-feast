package com.ksoot.feast.dto.serving;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Valid
@Getter
@ToString
public class PushFeaturesRequest {

  @Schema(description = "Push Source name", example = "driver_stats_push_source")
  @NotEmpty
  @JsonProperty("push_source_name")
  private final String pushSourceName;

  @Schema(
      description =
          "Push Mode. 'offline', 'online' and 'online_and_offline' to push features to Offline Store, Online Store or Both respectively.</b>"
              + "Check Feast Documentation to see what mode is supported for Configured Offline and Online Stores",
      example = "online")
  @NotNull
  @JsonProperty("to")
  private PushMode mode = PushMode.online;

  @Schema(
      description = "Data to push.",
      example =
          """
              {
                  "driver_id": [1001, 1002],
                  "event_timestamp": ["2024-11-10T12:30:30+00:00", "2024-11-10T12:31:31+00:00"],
                  "created": ["2024-11-10T12:30:30Z", "2024-11-10T12:31:31Z"],
                  "conv_rate": [7.7, 7.7],
                  "acc_rate": [7.7, 7.7],
                  "avg_daily_trips": [2222, 3333]
              }
          """)
  @NotEmpty
  @JsonProperty("df")
  private Map<@NotEmpty String, @NotEmpty List<@NotNull Object>> features;

  public PushFeaturesRequest(
      final String pushSourceName, final PushMode mode, final Map<String, List<Object>> features) {
    this.pushSourceName = pushSourceName;
    this.mode = mode;
    this.features = features;
  }

  @JsonCreator
  public static PushFeaturesRequest of(
      @JsonProperty("push_source_name") final String pushSourceName,
      @JsonProperty("to") final PushMode mode,
      @JsonProperty("df") final Map<String, List<Object>> features) {
    return new PushFeaturesRequest(pushSourceName, mode, features);
  }

  public enum PushMode {
    offline,
    online,
    online_and_offline
  }
}
