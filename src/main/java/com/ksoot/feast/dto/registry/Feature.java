package com.ksoot.feast.dto.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import feast.proto.types.ValueProto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.util.Assert;

@Getter
@ToString
@EqualsAndHashCode(of = "name")
@Valid
public class Feature {

  @Schema(description = "Feature name", example = "conv_rate")
  @NotEmpty
  private final String name;

  @Schema(description = "Feature value Type", example = "FLOAT")
  @NotNull
  private final ValueProto.ValueType.Enum valueType;

  @Schema(description = "Feature mandatory or not", example = "true")
  private final boolean required;

  @Schema(description = "Feature description", example = "Feature for testing")
  private final String description;

  @Schema(description = "Tags for easier lookup", example = "{\"team\": \"driver_performance\"}")
  private final Map<@NotEmpty String, @NotEmpty String> tags;

  @JsonIgnore
  public boolean isOptional() {
    return !this.required;
  }

  private Feature(
      final String name,
      final ValueProto.ValueType.Enum valueType,
      final boolean required,
      final String description,
      final Map<String, String> tags) {
    this.name = name;
    this.valueType = valueType;
    this.required = required;
    this.description = description;
    this.tags = tags;
  }

  @JsonCreator
  public static Feature of(
      @JsonProperty("name") final String name,
      @JsonProperty("valueType") final ValueProto.ValueType.Enum valueType,
      @JsonProperty("required") final boolean required,
      @JsonProperty("description") final String description,
      @JsonProperty("tags") final Map<String, String> tags) {
    return new Feature(name, valueType, required, description, tags);
  }

  // Asking only for required arguments
  public static Feature of(final String name, final ValueProto.ValueType.Enum valueType) {
    return Feature.of(name, valueType, false, null, null);
  }

  public Map<String, String> getTags() {
    return MapUtils.isNotEmpty(this.tags)
        ? Collections.unmodifiableMap(this.tags)
        : Collections.emptyMap();
  }

  public static ValueTypeBuilder name(final String name) {
    return new FeatureFieldBuilder(name);
  }

  public interface ValueTypeBuilder {

    RequiredBuilder valueType(final ValueProto.ValueType.Enum valueType);
  }

  public interface RequiredBuilder extends DescriptionBuilder {

    DescriptionBuilder required();

    DescriptionBuilder optional();

    DescriptionBuilder required(final boolean required);
  }

  public interface DescriptionBuilder extends TagsBuilder {

    TagsBuilder description(final String description);
  }

  public interface TagsBuilder extends Builder<Feature> {

    TagsBuilder tags(final Map<String, String> tags);

    TagsBuilder tag(final String key, final String value);
  }

  public static class FeatureFieldBuilder
      implements ValueTypeBuilder, RequiredBuilder, DescriptionBuilder {

    private final String name;

    private ValueProto.ValueType.Enum valueType;

    private boolean required = false;

    private String description;

    private final Map<String, String> tags = new LinkedHashMap<>();

    FeatureFieldBuilder(final String name) {
      this.name = name;
    }

    @Override
    public RequiredBuilder valueType(final ValueProto.ValueType.Enum valueType) {
      this.valueType = valueType;
      return this;
    }

    @Override
    public DescriptionBuilder required() {
      this.required = true;
      return this;
    }

    @Override
    public DescriptionBuilder optional() {
      this.required = false;
      return this;
    }

    @Override
    public DescriptionBuilder required(boolean required) {
      this.required = required;
      return this;
    }

    @Override
    public TagsBuilder description(@Nullable final String description) {
      this.description = description;
      return this;
    }

    @Override
    public TagsBuilder tags(@Nullable final Map<String, String> tags) {
      if (MapUtils.isNotEmpty(tags)) {
        tags.forEach(this::tag);
      }
      return this;
    }

    @Override
    public TagsBuilder tag(final String key, final String value) {
      Assert.hasText(key, "'key' must not be null or empty");
      Assert.hasText(value, "'value' must not be null or empty");
      this.tags.put(key, value);
      return this;
    }

    @Override
    public Feature build() {
      return Feature.of(this.name, this.valueType, this.required, this.description, this.tags);
    }
  }
}
