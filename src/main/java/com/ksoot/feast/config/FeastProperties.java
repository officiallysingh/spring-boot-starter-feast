package com.ksoot.feast.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Validated
@ConditionalOnProperty(
    prefix = "feast",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class FeastProperties {

  private boolean enabled = true;

  /** Feature store project name. */
  @NotEmpty private String project;

  @NotNull private Registry registry = new Registry();

  @NotNull private Serving serving = new Serving();

  @Getter
  @Setter
  @NoArgsConstructor
  @ToString
  @Validated
  public static class Registry {

    /** Feast gRpc Registry server host address. */
    @NotEmpty private String host = "localhost";

    /** Feast gRpc Registry server port. */
    @NotNull private Integer port = 50051;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @ToString
  @Validated
  public static class Serving {

    /** Feast HTTP Serving server URL. */
    @NotEmpty private String url = "http://localhost:6566";
  }
}
