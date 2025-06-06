package com.ksoot.feast;

import org.springframework.core.NestedRuntimeException;

public class FeastClientException extends NestedRuntimeException {

  public FeastClientException(final String message) {
    super(message);
  }

  public FeastClientException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
