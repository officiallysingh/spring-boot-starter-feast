package com.ksoot.feast;

import com.ksoot.feast.config.FeastClientConfiguration;
import feast.proto.types.ValueProto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class FeastClientEntityTest {

  @Autowired private FeastClient feastClient;

  @Test
  @DisplayName("Test Apply Entity successfully")
  void testEntityApply() {
    ValueProto.ValueType.Enum valueType = ValueProto.ValueType.Enum.INT64;
    this.feastClient
        .entity("driver")
        .joinKey("driver_id", valueType)
        .description("Example entity for testing")
        .owner("Rajveer Singh")
        .tag("team", "driver_performance")
        .apply();
  }
}
