package com.example.meetingplanner;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("meetingplanner.restrictions")
public class RestrictionsCovidProperties {

  private Integer minuteLibreAvant;
  private Float ratioCapacite;
}
