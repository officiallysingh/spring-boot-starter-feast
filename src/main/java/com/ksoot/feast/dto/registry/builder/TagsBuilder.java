package com.ksoot.feast.dto.registry.builder;

import com.ksoot.feast.dsl.ApplierBuilder;
import java.util.Map;

public interface TagsBuilder<T> extends ApplierBuilder<T> {
  TagsBuilder<T> tags(final Map<String, String> tags);

  TagsBuilder<T> tag(final String key, final String value);
}
