package com.ksoot.feast.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksoot.feast.FeastClient;
import com.ksoot.feast.FeastRegistryClient;
import com.ksoot.feast.FeastServingClient;
import com.ksoot.feast.FeastServingFeignClient;
import feast.registry.RegistryServerGrpc;
import feign.Feign;
import feign.Logger;
import feign.slf4j.Slf4jLogger;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients
@Configuration
@ConditionalOnProperty(
    prefix = "feast",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class FeastClientConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "feast")
  FeastProperties feastProperties() {
    return new FeastProperties();
  }

  @Bean(destroyMethod = "shutdown")
  ManagedChannel managedChannel(final FeastProperties feastProperties) {
    return ManagedChannelBuilder.forAddress(
            feastProperties.getRegistry().getHost(), feastProperties.getRegistry().getPort())
        .usePlaintext() // Use plaintext communication (no SSL)
        .build();
  }

  @Bean
  RegistryServerGrpc.RegistryServerBlockingStub registryServerBlockingStub(
      final ManagedChannel managedChannel) {
    return RegistryServerGrpc.newBlockingStub(managedChannel);
  }

  @Bean
  FeastRegistryClient feastRegistryClient(
      final RegistryServerGrpc.RegistryServerBlockingStub registryServerBlockingStub,
      final FeastProperties feastProperties,
      final ObjectMapper objectMapper) {
    return new FeastRegistryClient(registryServerBlockingStub, feastProperties, objectMapper);
  }

  @Bean
  FeastServingFeignClient feastServingFeignClient(
      final FeastProperties feastProperties,
      final ObjectFactory<HttpMessageConverters> messageConverters) {
    return Feign.builder()
        .contract(new SpringMvcContract())
        .decodeVoid()
        .decoder(new SpringDecoder(messageConverters))
        .encoder(new SpringEncoder(messageConverters))
        .logLevel(Logger.Level.FULL)
        .logger(new Slf4jLogger(FeastServingFeignClient.class))
        .target(FeastServingFeignClient.class, feastProperties.getServing().getUrl());
  }

  @Bean
  FeastServingClient feastServingClient(final FeastServingFeignClient feastServingFeignClient) {
    return new FeastServingClient(feastServingFeignClient);
  }

  @Bean
  FeastClient feastClient(
      final FeastRegistryClient feastRegistryClient, final FeastServingClient feastServingClient) {
    return new FeastClient(feastRegistryClient, feastServingClient);
  }
}
