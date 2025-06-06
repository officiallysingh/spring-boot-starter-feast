package com.ksoot.feast;

public class FeatureStoreConstants {

  public static final String DATA_SOURCE_CLASS_TYPE_POSTGRESQL =
      "feast.infra.offline_stores.contrib.postgres_offline_store.postgres_source.PostgreSQLSource";
  public static final String EVENT_TIMESTAMP_FIELD = "event_timestamp";
  public static final String CREATED_TIMESTAMP_FIELD = "created";
}
