package dev.basudewa.clickroom;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.basudewa.clickroom.entity.Facility;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClickroomFacilityTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllFacilitiesOfARoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/facility/room/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int facilityCount = documentContext.read("$.length()");
        assertThat(facilityCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102);
    }

    @Test
    void shouldReturnSortedFacilitiesOfARoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/facility/room/100?sort=facility_name,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int facilityCount = documentContext.read("$.length()");
        assertThat(facilityCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(102, 101, 100);
    }

    @Test
    void shouldNotReturnFacilityOfANonExistingRoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/facility/room/100000?sort=facility_name,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAddNewFacilityToARoom() {
        Facility newFacility = new Facility(null, "Projector", 1, 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/facility/admin", newFacility, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewFacility = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewFacility, String.class);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        String facilityName = documentContext.read("$.facilityName");
        assertThat(facilityName).isEqualTo("Projector");

        Number amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(1);

        Number roomId = documentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(101);
    }

    @Test
    void shouldNotAllowNotAdminToCreateNewFacility() {
        Facility newFacility = new Facility(null, "Projector", 1, 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .postForEntity("/facility/admin", newFacility, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
