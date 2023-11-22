package dev.basudewa.clickroom;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.basudewa.clickroom.entity.Request;
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
public class ClickroomRequestTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAllRequestToOwner() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/request", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int responseCount = documentContext.read("$.length()");
        assertThat(responseCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 103);
    }

    @Test
    void shouldReturnSortedRequestToOwner() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/request?sort=borrow_date,desc&sort=start_time,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int responseCount = documentContext.read("$.length()");
        assertThat(responseCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactly(103, 100);
    }

    @Test
    void shouldNotReturnAnyRequestToUserWithNoRequest() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("noschedule", "noschedule")
                .getForEntity("/request?sort=borrow_date,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowUserToMakeARequest() {
        Request request = new Request(null, "2023-11-30", "08:00", "12:00", null, "Kuliah Rekayasa Perangkat Lunak", null, 100L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .postForEntity("/request", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewRequest = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity(locationOfNewRequest, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        String borrowDate = documentContext.read("$.borrowDate");
        assertThat(borrowDate).isEqualTo("2023-11-30");

        String startTime = documentContext.read("$.startTime");
        assertThat(startTime).isEqualTo("08:00:00");

        String endTime = documentContext.read("$.endTime");
        assertThat(endTime).isEqualTo("12:00:00");

        String lendee = documentContext.read("$.lendee");
        assertThat(lendee).isEqualTo("2022A");

        String detail = documentContext.read("$.detail");
        assertThat(detail).isEqualTo("Kuliah Rekayasa Perangkat Lunak");

        String status = documentContext.read("$.status");
        assertThat(status).isEqualTo("Pending");

        Number roomId = documentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(100);
    }

    @Test
    void shouldNotAllowUserToMakeRequestThatCollidesWithExistingSchedule() {
        Request request = new Request(null, "2023-11-08", "09:00", "11:00", null, "Kuliah Rekayasa Perangkat Lunak", null, 100L);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .postForEntity("/request", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void shouldAllowUserToUpdateRequestTheyOwn() {
        Request requestUpdate = new Request(null, "2023-11-08", "13:00", "15:00", null, "Kuliah Interaksi Manusia Dan Komputer", null, 100L);
        HttpEntity<Request> request = new HttpEntity<>(requestUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/request/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/request/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();

        String borrowDate = documentContext.read("$.borrowDate");
        assertThat(borrowDate).isEqualTo("2023-11-08");

        String startTime = documentContext.read("$.startTime");
        assertThat(startTime).isEqualTo("13:00:00");

        String endTime = documentContext.read("$.endTime");
        assertThat(endTime).isEqualTo("15:00:00");

        String lendee = documentContext.read("$.lendee");
        assertThat(lendee).isEqualTo("2022A");

        String detail = documentContext.read("$.detail");
        assertThat(detail).isEqualTo("Kuliah Interaksi Manusia Dan Komputer");

        String status = documentContext.read("$.status");
        assertThat(status).isEqualTo("Pending");

        Number roomId = documentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(100);
    }

    @Test
    void shouldNotAllowUserToUpdateRequestToATimeWithCollidingSchedule() {
        Request requestUpdate = new Request(null, "2023-11-08", "11:00", "13:00", null, "Kuliah Interaksi Manusia Dan Komputer", null, 100L);
        HttpEntity<Request> request = new HttpEntity<>(requestUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/request/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotAllowUserToUpdateRequestTheyDontOwn() {
        Request requestUpdate = new Request(null, "2023-11-08", "13:00", "15:00", null, "Kuliah Interaksi Manusia Dan Komputer", null, 100L);
        HttpEntity<Request> request = new HttpEntity<>(requestUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022B", "2022B")
                .exchange("/request/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowUserToDeleteRequestTheyOwn() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/request/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("2022A", "2022A")
                .getForEntity("/request/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowUserToDeleteRequestTheyDontOwn() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022B", "2022B")
                .exchange("/request/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToAcceptARequest() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/request/admin/100?status=accept", HttpMethod.PUT, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> getResponseRequest = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/request/100", String.class);
        assertThat(getResponseRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext requestDocumentContext = JsonPath.parse(getResponseRequest.getBody());
        Number requestId = requestDocumentContext.read("$.id");
        assertThat(requestId).isEqualTo(100);

        String requestStatus = requestDocumentContext.read("$.status");
        assertThat(requestStatus).isEqualTo("Accepted");

        URI locationOfNewSchedule = response.getHeaders().getLocation();
        ResponseEntity<String> getResponseSchedule = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity(locationOfNewSchedule, String.class);
        assertThat(getResponseSchedule.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext scheduleDocumentContext = JsonPath.parse(getResponseSchedule.getBody());
        Number scheduleId = scheduleDocumentContext.read("$.id");
        assertThat(scheduleId).isNotNull();

        String borrowDate = scheduleDocumentContext.read("$.borrowDate");
        assertThat(borrowDate).isEqualTo("2023-11-20");

        String startTime = scheduleDocumentContext.read("$.startTime");
        assertThat(startTime).isEqualTo("10:30:00");

        String endTime = scheduleDocumentContext.read("$.endTime");
        assertThat(endTime).isEqualTo("12:00:00");

        String lendee = scheduleDocumentContext.read("$.lendee");
        assertThat(lendee).isEqualTo("2022A");

        String lender = scheduleDocumentContext.read("$.lender");
        assertThat(lender).isEqualTo("admin1");

        String detail = scheduleDocumentContext.read("$.detail");
        assertThat(detail).isEqualTo("Kuliah Rekayasa Perangkat Lunak");

        Number roomId = scheduleDocumentContext.read("$.roomId");
        assertThat(roomId).isEqualTo(100);
    }

    @Test
    @DirtiesContext
    void shouldAllowAdminToDeclineRequest() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/request/admin/100?status=decline", HttpMethod.PUT, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponseRequest = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/request/100", String.class);
        assertThat(getResponseRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext requestDocumentContext = JsonPath.parse(getResponseRequest.getBody());
        Number requestId = requestDocumentContext.read("$.id");
        assertThat(requestId).isEqualTo(100);

        String requestStatus = requestDocumentContext.read("$.status");
        assertThat(requestStatus).isEqualTo("Declined");
    }

    @Test
    void shouldNotAllowNonAdminToAcceptOrDeclineRequest() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("2022A", "2022A")
                .exchange("/request/admin/100?status=decline", HttpMethod.PUT, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAdminToModifyRequestWithoutRequestParameters() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin1", "admin1")
                .exchange("/request/admin/100", HttpMethod.PUT, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<String> getResponseRequest = restTemplate
                .withBasicAuth("admin1", "admin1")
                .getForEntity("/request/100", String.class);
        assertThat(getResponseRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext requestDocumentContext = JsonPath.parse(getResponseRequest.getBody());
        Number requestId = requestDocumentContext.read("$.id");
        assertThat(requestId).isEqualTo(100);

        String requestStatus = requestDocumentContext.read("$.status");
        assertThat(requestStatus).isEqualTo("Pending");
    }
}
