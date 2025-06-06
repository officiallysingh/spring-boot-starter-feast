package com.ksoot.feast.dsl;

import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.dto.registry.SparkSource;
import com.ksoot.feast.dto.registry.builder.DescriptionBuilder;
import org.springframework.util.Assert;

public class SparkSourceDsl {

  public interface QueryOrTableBuilder {

    EventTimestampFieldBuilder query(final String query);

    EventTimestampFieldBuilder table(final String table);

    EventTimestampFieldBuilder file(final String path, final String format);
  }

  public interface EventTimestampFieldBuilder
      extends CreatedTimestampFieldBuilder, DescriptionBuilder<SparkSource> {

    CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField);
  }

  public interface CreatedTimestampFieldBuilder {

    DescriptionBuilder<SparkSource> createdTimestampField(final String createdTimestampField);
  }

  public static class Executor extends AbstractRegistryDslExecutor<SparkSource>
      implements QueryOrTableBuilder, EventTimestampFieldBuilder, CreatedTimestampFieldBuilder {

    public Executor(final FeastRegistryClient feastRegistryClient, final String name) {
      super(feastRegistryClient, name);
    }

    private String query;

    private String table;

    private String filePath;

    private String fileFormat;

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
    public EventTimestampFieldBuilder file(final String path, final String format) {
      Assert.hasText(path, "File 'path' is required");
      Assert.hasText(format, "File 'format' is required");
      this.filePath = path;
      this.fileFormat = format;
      return this;
    }

    @Override
    public CreatedTimestampFieldBuilder eventTimestampField(final String eventTimestampField) {
      this.eventTimestampField = eventTimestampField;
      return this;
    }

    @Override
    public DescriptionBuilder<SparkSource> createdTimestampField(
        final String createdTimestampField) {
      this.createdTimestampField = createdTimestampField;
      return this;
    }

    @Override
    public SparkSource build() {
      return SparkSource.of(
          this.name,
          this.table,
          this.query,
          this.filePath,
          this.fileFormat,
          this.eventTimestampField,
          this.createdTimestampField,
          this.description,
          this.owner,
          this.tags);
    }

    @Override
    public void apply() {
      final SparkSource sparkSourceRequest = this.build();
      this.feastRegistryClient.applyDataSource(sparkSourceRequest);
    }
  }
}
