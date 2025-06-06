package com.ksoot.feast.dto.registry.builder;

public interface OwnerBuilder<T> extends TagsBuilder<T> {

  TagsBuilder<T> owner(final String owner);
}
