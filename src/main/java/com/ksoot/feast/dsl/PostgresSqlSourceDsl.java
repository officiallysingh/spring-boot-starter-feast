package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.PostgresSqlSource;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import org.springframework.util.Assert;

public class PostgresSqlSourceDsl {

  public interface QueryOrTableBuilder {

    EventTimestampFieldBuilder query(final String query);

    EventTimestampFieldBuilder table(final String table);
  }

  public interface EventTimestampFieldBuilder
      extends CreatedTimestampFieldBuilder, DescriptionBuilder<PostgresSqlSource> {

    CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField);
  }

  public interface CreatedTimestampFieldBuilder extends DescriptionBuilder<PostgresSqlSource> {

    DescriptionBuilder<PostgresSqlSource> createdTimestampField(final String createdTimestampField);
  }

  public static class Executor extends AbstractRegistryDslExecutor<PostgresSqlSource>
      implements QueryOrTableBuilder, EventTimestampFieldBuilder, CreatedTimestampFieldBuilder {

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    private String query;

    private String table;

    private String eventTimestampField;

    private String createdTimestampField;

    @Override
    public EventTimestampFieldBuilder query(final String query) {
      Assert.hasText(query, "'query' is required");
      this.query = query;
      return this;
    }

    @Override
    public EventTimestampFieldBuilder table(final String table) {
      Assert.hasText(table, "'table' is required");
      this.table = table;
      return this;
    }

    @Override
    public CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField) {
      this.eventTimestampField = eventTimestampField;
      return this;
    }

    @Override
    public DescriptionBuilder<PostgresSqlSource> createdTimestampField(
        final String createdTimestampField) {
      this.createdTimestampField = createdTimestampField;
      return this;
    }

    @Override
    public PostgresSqlSource build() {
      return PostgresSqlSource.of(
          this.name,
          this.query,
          this.table,
          this.eventTimestampField,
          this.createdTimestampField,
          this.description,
          this.owner,
          this.tags);
    }

    @Override
    public void apply() {
      final PostgresSqlSource postgresSqlSourceRequest = this.build();
      this.feastRegistryClient.applyDataSource(postgresSqlSourceRequest);
    }
  }
}
