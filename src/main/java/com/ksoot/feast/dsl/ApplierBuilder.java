package com.ksoot.feast.dsl;

import org.apache.commons.lang3.builder.Builder;

public interface ApplierBuilder<T> extends Builder<T> {

  void apply();
}
