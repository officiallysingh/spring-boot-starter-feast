package com.ksoot.feast;

import ch.qos.logback.classic.Level;
import com.ksoot.feast.config.FeastClientConfiguration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Disabled
@SpringJUnitConfig(classes = {FeastClientConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class FeastClientPushFeaturesTest {

  @Autowired private FeastClient feastClient;

  @BeforeAll
  public static void setUp() {
    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FeastServingFeignClient.class);
    root.setLevel(Level.DEBUG);
  }

  @Test
  @DisplayName("Test Push Features")
  void testPushFeatures() {
    this.feastClient
        .pushTo("driver_stats_push_source")
        .toOnline()
        .feature("driver_id", 1001, 1002)
        .and("event_timestamp", "2024-11-10T12:30:30+00:00", "2024-11-10T12:31:31+00:00")
        .and("created", "2024-11-10T12:30:30Z", "2024-11-10T12:31:31Z")
        .and("conv_rate", 7.7, 7.7)
        .and("acc_rate", 8.8, 8.8)
        .and("avg_daily_trips", 2222, 3333)
        .apply();
  }

  //  +---+-------------+---+-----------+-------------------+-----+-----------+--------+------+
  //  |id |sex          |age|cholesterol|dob                |label|is_diabitic|salary  |pin   |
  //  +---+-------------+---+-----------+-------------------+-----+-----------+--------+------+
  //  |1  |MALE         |25 |260.9      |2016-12-31 03:22:34|3    |true       |34900.0 |238492|
  //  |2  |FEMALE       |45 |251.5      |2016-12-31 03:22:34|2    |true       |56000.0 |122002|
  //  |3  |NULL         |46 |290.4      |2016-12-31 03:21:21|3    |false      |78000.0 |122003|
  //  |4  |MALE         |15 |146.9      |2015-04-21 14:32:21|2    |true       |134000.0|122004|
  //  |5  |NOT_SPECIFIED|34 |NULL       |2015-04-21 19:23:20|1    |false      |123000.0|122005|
  //  +---+-------------+---+-----------+-------------------+-----+-----------+--------+------+

  //  @Test
  @DisplayName("Test Push Features")
  void testPushFeatures1() {
    Map<String, List<Object>> features = new LinkedHashMap<>();
    features.put("id", List.of("1", "2", "3", "4", "5"));
    features.put("sex", List.of("MALE", "FEMALE", "NULL", "MALE", "NOT_SPECIFIED"));
    features.put("age", List.of(25, 45, 46, 15, 34));
    features.put("cholesterol", List.of(260.9, 251.5, 290.4, 146.9, null));
    features.put(
        "dob",
        List.of(
            "2016-12-31 03:22:34",
            "2016-12-31 03:22:34",
            "2016-12-31 03:21:21",
            "2015-04-21 14:32:21",
            "2015-04-21 19:23:20"));
    features.put("label", List.of(3, 2, 3, 2, 1));
    features.put("is_diabitic", List.of(true, true, false, true, false));
    features.put("salary", List.of(34900.0, 56000.0, 78000.0, 134000.0, 123000.0));
    features.put("pin", List.of(238492, 122002, 122003, 122004, 122005));
    this.feastClient.pushTo("driver_stats_push_source").toOnline().features(features).apply();
  }
}
