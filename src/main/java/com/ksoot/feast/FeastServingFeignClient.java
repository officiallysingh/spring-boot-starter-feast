package com.ksoot.feast;

import com.ksoot.feast.dto.serving.MaterializationRequest;
import com.ksoot.feast.dto.serving.OnlineFeatures;
import com.ksoot.feast.dto.serving.OnlineFeaturesRequest;
import com.ksoot.feast.dto.serving.PushFeaturesRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
public interface FeastServingFeignClient {

  @GetMapping("/health")
  void health();

  @PostMapping("/materialize")
  void materialize(@RequestBody @Valid final MaterializationRequest materializationRequest);

  @PostMapping("/materialize-incremental")
  void materializeIncremental(
      @RequestBody @Valid final MaterializationRequest materializationRequest);

  @PostMapping("/push")
  void pushFeatures(@RequestBody @Valid final PushFeaturesRequest pushFeaturesRequest);

  @PostMapping("/get-online-features")
  OnlineFeatures getOnlineFeatures(
      @RequestBody @Valid
          final OnlineFeaturesRequest.GetOnlineFeaturesRequest getOnlineFeaturesRequest);
}
