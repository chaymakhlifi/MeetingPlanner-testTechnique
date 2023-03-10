package com.example.meetingplanner.service;

import com.example.meetingplanner.RestrictionsCovidProperties;
import com.example.meetingplanner.model.Salle;
import com.example.meetingplanner.service.db.SalleDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

/** Unit test for {@link FindAvailableSalleService} */
@ExtendWith(MockitoExtension.class)
public class FindAvailableSalleServiceTest {

  @Mock private RestrictionsCovidProperties restrictionsCovidProperties;
  @Mock private SalleDbService salleDbService;
  @InjectMocks private FindAvailableSalleService service;

  @BeforeEach
  void mockConfig() {
    lenient().doReturn(60).when(restrictionsCovidProperties).getMinuteLibreAvant();
    lenient().doReturn(0.7f).when(restrictionsCovidProperties).getRatioCapacite();
  }

  /** Unit test for method {@link FindAvailableSalleService#find} */
  @Nested
  class Find {

    @Test
    void findExpectedResults() {
      // [GIVEN] Searching parameters
      Instant debut = Instant.EPOCH;
      Instant fin = Instant.EPOCH.plus(1, DAYS);
      Integer nombrePersonne = 10;
      // [GIVEN] SalleDbService working when searching for 1h earlier and 70% capacity (15)
      Set<Salle> foundSalle = Set.of(Salle.builder().id(1).build());
      lenient()
          .doReturn(foundSalle)
          .when(salleDbService)
          .searchAllAvailable(debut.minus(1, HOURS), fin, 15);

      // [WHEN] Calling method
      Set<Salle> result = service.find(nombrePersonne, debut, fin);

      // [THEN] Return found result
      assertThat(result).isEqualTo(foundSalle);
    }
  }
}
