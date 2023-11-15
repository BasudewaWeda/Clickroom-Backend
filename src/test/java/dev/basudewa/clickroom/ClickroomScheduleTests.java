package dev.basudewa.clickroom;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.basudewa.clickroom.entity.Schedule;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.mockito.internal.exceptions.util.ScenarioPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClickroomScheduleTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllScheduleOfARoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/schedule/room/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int scheduleCount = documentContext.read("$.length()");
        assertThat(scheduleCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(101, 100);
    }

    @Test
    void shouldReturnSortedScheduleOfARoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/schedule/room/100?sort=start_time,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int scheduleCount = documentContext.read("$.length()");
        assertThat(scheduleCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactly(101, 100);
    }

    @Test
    void shouldReturnAllScheduleMadeByUser() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022B", "2022B")
                .getForEntity("/schedule", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int scheduleCount = documentContext.read("$.length()");
        assertThat(scheduleCount).isEqualTo(1);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(102);
    }

    @Test
    void shouldNotReturnScheduleOfANonExistingRoom() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/schedule/room/100000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotReturnScheduleToUserWithNoSchedule() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("noschedule", "noschedule")
                .getForEntity("/schedule", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAddASchedule1() {
        Schedule schedule = new Schedule(null, "2023-11-09", "08:00", "10:30", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewSchedule = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewSchedule, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        Number roomId = documentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(101);

        String lendee = documentContext.read("$.lendee");
        assertThat(lendee).isEqualTo("admin1");

        String lender = documentContext.read("$.lender");
        assertThat(lender).isEqualTo("admin1");
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAddASchedule2() {
        Schedule schedule = new Schedule(null, "2023-11-09", "08:00", "10:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewSchedule = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewSchedule, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAddASchedule3() {
        Schedule schedule = new Schedule(null, "2023-11-09", "12:00", "14:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewSchedule = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewSchedule, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAddASchedule4() {
        Schedule schedule = new Schedule(null, "2023-11-09", "13:00", "14:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewSchedule = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewSchedule, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotAllowAdminToAddScheduleThatCollidesWithAnotherSchedule1() {
        Schedule schedule = new Schedule(null, "2023-11-09", "10:30", "12:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotAllowAdminToAddScheduleThatCollidesWithAnotherSchedule2() {
        Schedule schedule = new Schedule(null, "2023-11-09", "09:00", "11:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotAllowAdminToAddScheduleThatCollidesWithAnotherSchedule3() {
        Schedule schedule = new Schedule(null, "2023-11-09", "10:45", "11:45", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotAllowAdminToAddScheduleThatCollidesWithAnotherSchedule4() {
        Schedule schedule = new Schedule(null, "2023-11-09", "11:00", "13:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotAllowNonAdminToMakeNewSchedule() {
        Schedule schedule = new Schedule(null, "2023-11-08", "08:00", "10:00", null, null, "Kuliah Rekayasa Perangkat Lunak", 101L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .postForEntity("/schedule/admin", schedule, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void shouldAllowUserToDeleteScheduleOwnedByThem() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/schedule/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/schedule/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowUserToDeleteScheduleTheyDontOwn() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/schedule/101", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToDeleteAllSchedule() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/schedule/admin/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/schedule/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowAdminToDeleteNonExistingSchedule() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/schedule/admin/100000", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowNonAdminToDeleteAllSchedule() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/schedule/admin/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToModifyAScheduleToAValidTime() {
        Schedule scheduleUpdate = new Schedule(null, "2023-11-10", "10:00", "12:30", null, null, "Kuliah DAA", 101L);
        HttpEntity<Schedule> request = new HttpEntity<>(scheduleUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/schedule/admin/101", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/schedule/101", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(101);

        String borrowDate = documentContext.read("$.borrowDate");
        assertThat(borrowDate).isEqualTo("2023-11-10");

        String startTime = documentContext.read("$.startTime");
        assertThat(startTime).isEqualTo("10:00:00");

        String endTime = documentContext.read("$.endTime");
        assertThat(endTime).isEqualTo("12:30:00");

        String lendee = documentContext.read("$.lendee");
        assertThat(lendee).isEqualTo("2022D");

        String lender = documentContext.read("$.lender");
        assertThat(lender).isEqualTo("admin1");

        String detail = documentContext.read("$.detail");
        assertThat(detail).isEqualTo("Kuliah DAA");

        Number roomId = documentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(101);
    }
}