package com.ksoot.feast;

import com.ksoot.feast.dto.serving.MaterializationRequest;
import com.ksoot.feast.dto.serving.OnlineFeatures;
import com.ksoot.feast.dto.serving.OnlineFeaturesRequest;
import com.ksoot.feast.dto.serving.PushFeaturesRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
@RequiredArgsConstructor
public class FeastServingClient {

  private final FeastServingFeignClient feastServingFeignClient;

  public boolean isServing() {
    this.feastServingFeignClient.health();
    return true;
  }

  public void materialize(final MaterializationRequest materializationRequest) {
    if (materializationRequest.isIncremental()) {
      this.feastServingFeignClient.materializeIncremental(materializationRequest);
    } else {
      this.feastServingFeignClient.materialize(materializationRequest);
    }
    log.info("Materialized successfully: {}", materializationRequest);
  }

  public OnlineFeatures getOnlineFeatures(
      @Valid final OnlineFeaturesRequest onlineFeaturesRequest) {
    return this.feastServingFeignClient.getOnlineFeatures(onlineFeaturesRequest.features());
  }

  public void pushFeatures(@Valid final PushFeaturesRequest pushFeaturesRequest) {
    this.feastServingFeignClient.pushFeatures(pushFeaturesRequest);
    log.info("Pushed successfully: {}", pushFeaturesRequest);
  }
}
