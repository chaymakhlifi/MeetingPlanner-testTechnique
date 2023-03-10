package com.example.meetingplanner.service;

import com.example.meetingplanner.RestrictionsCovidProperties;
import com.example.meetingplanner.model.Materiel;
import com.example.meetingplanner.service.db.MaterielMobileDbService;
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

/** Unit test for {@link FindAvailableMaterielService} */
@ExtendWith(MockitoExtension.class)
public class FindAvailableMaterielServiceTest {

  @Mock private RestrictionsCovidProperties restrictionsCovidProperties;
  @Mock private MaterielMobileDbService materielMobileDbService;
  @InjectMocks private FindAvailableMaterielService service;

  @BeforeEach
  void mockConfig() {
    lenient().doReturn(60).when(restrictionsCovidProperties).getMinuteLibreAvant();
    lenient().doReturn(0.7f).when(restrictionsCovidProperties).getRatioCapacite();
  }

  /** Unit test for method {@link FindAvailableMaterielService#find} */
  @Nested
  class Find {

    @Test
    void findExpectedResults() {
      // [GIVEN] Searching parameters
      Instant debut = Instant.EPOCH;
      Instant fin = Instant.EPOCH.plus(1, DAYS);
      Set<Integer> idTypeMateriel = Set.of(1, 2);
      // [GIVEN] MaterielMobileDbService working when searching for 1h earlier
      Set<Materiel> foundMateriel = Set.of(Materiel.builder().id(3).build());
      lenient()
          .doReturn(foundMateriel)
          .when(materielMobileDbService)
          .searchAllAvailable(debut.minus(1, HOURS), fin, idTypeMateriel);

      // [WHEN] Calling method
      Set<Materiel> result = service.find(idTypeMateriel, debut, fin);

      // [THEN] Return found result
      assertThat(result).isEqualTo(foundMateriel);
    }
  }
}
