package com.ksoot.feast.dto.registry.builder;

public interface DescriptionBuilder<T> extends OwnerBuilder<T> {

  OwnerBuilder<T> description(final String description);
}
