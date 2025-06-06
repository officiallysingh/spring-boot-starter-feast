package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import com.ksoot.feast.dto.registry.builder.OwnerBuilder;
import com.ksoot.feast.dto.registry.builder.TagsBuilder;
import jakarta.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractRegistryDslExecutor<T> implements DescriptionBuilder<T> {

  protected final FeastRegistryClient feastRegistryClient;

  protected final String name;

  protected String description;

  protected String owner;

  protected Map<String, String> tags = new LinkedHashMap<>();

  @Override
  public OwnerBuilder<T> description(@Nullable final String description) {
    this.description = description;
    return this;
  }

  @Override
  public TagsBuilder<T> owner(@Nullable final String owner) {
    this.owner = owner;
    return this;
  }

  @Override
  public TagsBuilder<T> tags(@Nullable final Map<String, String> tags) {
    if (MapUtils.isNotEmpty(tags)) {
      tags.forEach(this::tag);
    }
    return this;
  }

  @Override
  public TagsBuilder<T> tag(final String key, final String value) {
    Assert.hasText(key, "'key' must not be null or empty");
    Assert.hasText(value, "'value' must not be null or empty");
    this.tags.put(key, value);
    return this;
  }
}
