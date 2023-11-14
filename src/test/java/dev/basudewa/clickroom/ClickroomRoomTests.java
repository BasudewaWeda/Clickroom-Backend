package dev.basudewa.clickroom;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.basudewa.clickroom.entity.Room;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
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
class ClickroomRoomTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllRoomsToUser() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/room", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int roomCount = documentContext.read("$.length()");
        assertThat(roomCount).isEqualTo(4);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102, 103);

        JSONArray capacities = documentContext.read("$..capacity");
        assertThat(capacities).containsExactlyInAnyOrder(50, 40, 30, 40);

        JSONArray locations = documentContext.read("$..location");
        assertThat(locations).containsExactlyInAnyOrder("fmipa", "fk", "fh", "ft");
    }

    @Test
    void shouldReturnSortedRoomsToUser() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/room?sort=location,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int roomCount = documentContext.read("$.length()");
        assertThat(roomCount).isEqualTo(4);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactly(102, 101, 100, 103);

        JSONArray capacities = documentContext.read("$..capacity");
        assertThat(capacities).containsExactly(30, 40, 50, 40);

        JSONArray locations = documentContext.read("$..location");
        assertThat(locations).containsExactly("fh", "fk", "fmipa", "ft");
    }

    @Test
    void shouldReturnAllRoomsToAdmin() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/room", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int roomCount = documentContext.read("$.length()");
        assertThat(roomCount).isEqualTo(4);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102, 103);

        JSONArray capacities = documentContext.read("$..capacity");
        assertThat(capacities).containsExactlyInAnyOrder(50, 40, 30, 40);

        JSONArray locations = documentContext.read("$..location");
        assertThat(locations).containsExactlyInAnyOrder("fmipa", "fk", "fh", "ft");
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToMakeANewRoom() {
        Room room = new Room(null, 50, "fmipa");
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .postForEntity("/room/admin", room, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewRoom = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewRoom, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        Number capacity = documentContext.read("$.capacity");
        assertThat(capacity).isEqualTo(room.capacity());

        String location = documentContext.read("$.location");
        assertThat(location).isEqualTo(room.location());
    }

    @Test
    void shouldNotAllowNonAdminToMakeANewRoom() {
        Room room = new Room(null, 50, "fmipa");
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .postForEntity("/room/admin", room, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToUpdateAnExsitingRoom() {
        Room roomUpdate = new Room(null, 40, "fmipa");
        HttpEntity<Room> request = new HttpEntity<>(roomUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/room/admin/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/room/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(100);

        Number capacity = documentContext.read("$.capacity");
        assertThat(capacity).isEqualTo(40);

        String location = documentContext.read("$.location");
        assertThat(location).isEqualTo("fmipa");
    }

    @Test
    void shouldNotAllowAdminToUpdateNonExisitingRoom() {
        Room roomUpdate = new Room(null, 40, "fmipa");
        HttpEntity<Room> request = new HttpEntity<>(roomUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/room/admin/100000", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowNonAdminToUpdateRoom() {
        Room roomUpdate = new Room(null, 40, "fmipa");
        HttpEntity<Room> request = new HttpEntity<>(roomUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/room/admin/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToDeleteExsistingRoom() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/room/admin/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/room/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowAdminToDeleteNonExistingRoom() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/room/admin/10000000", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowNonAdminToDeleteRoom() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/room/admin/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
