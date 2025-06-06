package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import feast.proto.core.DataSourceProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.*;

@Getter
@ToString(callSuper = true)
@Valid
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PostgresSqlSource.class, name = "CUSTOM_SOURCE"),
  @JsonSubTypes.Type(value = PushSource.class, name = "PUSH_SOURCE"),
  @JsonSubTypes.Type(value = KafkaSource.class, name = "STREAM_KAFKA"),
  @JsonSubTypes.Type(value = RequestSource.class, name = "REQUEST_SOURCE"),
  @JsonSubTypes.Type(value = FileSource.class, name = "BATCH_FILE"),
  @JsonSubTypes.Type(value = SparkSource.class, name = "BATCH_SPARK"),
})
public abstract class DataSource extends AbstractFeastRequest {

  @Schema(
      description = "Data source type",
      example = "BATCH_FILE",
      allowableValues = {
        "BATCH_FILE",
        "BATCH_SNOWFLAKE",
        "BATCH_BIGQUERY",
        "BATCH_REDSHIFT",
        "STREAM_KAFKA",
        "STREAM_KINESIS",
        "CUSTOM_SOURCE",
        "REQUEST_SOURCE",
        "PUSH_SOURCE",
        "BATCH_TRINO",
        "BATCH_SPARK",
        "BATCH_ATHENA"
      })
  @NotNull
  protected final DataSourceProto.DataSource.SourceType type;

  protected DataSource(
      final String name,
      final DataSourceProto.DataSource.SourceType type,
      final String description,
      final String owner,
      final Map<String, String> tags) {
    super(name, description, owner, tags);
    this.type = type;
  }
}
