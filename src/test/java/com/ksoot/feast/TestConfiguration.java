package com.ksoot.feast;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

  //  TestConfiguration(final FeastProperties feastProperties) throws IOException {
  //    Resource resource = new ClassPathResource("application-test.properties");
  //    Map<String, Object> feastProps = new ResourcePropertySource(resource).getSource();
  //    feastProperties.setProject(feastProps.get("mlhub.feast.project").toString());
  //
  // feastProperties.getRegistry().setHost(feastProps.get("mlhub.feast.registry.host").toString());
  //    feastProperties
  //        .getRegistry()
  //        .setPort(Integer.valueOf(feastProps.get("mlhub.feast.registry.port").toString()));
  //    feastProperties.getServing().setUrl(feastProps.get("mlhub.feast.serving.url").toString());
  //  }

  @Bean
  ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    //    objectMapper.registerModule(new JavaTimeModule()); // Add support for Java 8 Date/Time
    // types
    return objectMapper;
  }

  @Bean
  public HttpMessageConverters converters() {
    return new HttpMessageConverters();
  }

  //  @DynamicPropertySource
  //  static void dynamicProperties(final DynamicPropertyRegistry registry) {
  //    registry.add("mlhub.feast.project", () -> "feast_demo");
  //    registry.add("mlhub.feast.registry.host", () -> "localhost");
  //    registry.add("mlhub.feast.registry.port", () -> 50051);
  //    registry.add("mlhub.feast.serving.url", () -> "http://localhost:6567");
  //    registry.add("logging.level.ai.mlhub.platform.feast.client.FeastServingFeignClient", () ->
  // "debug");
  //  }

  // Define other beans or component scans as needed
}
