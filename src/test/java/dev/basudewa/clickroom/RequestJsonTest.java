package dev.basudewa.clickroom;

import dev.basudewa.clickroom.entity.Request;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestJsonTest {
    @Autowired
    private JacksonTester<Request> json;

    @Autowired
    private JacksonTester<Request[]> jsonList;

    private Request[] requests;

    @BeforeEach
    void setUp() {
        requests = Arrays.array(
                new Request(100L, "2023-11-08", "10:30", "12:00", "2022A", "Kuliah Rekayasa Perangkat Lunak", "Pending", 100L),
                new Request(101L, "2023-11-08", "10:30", "12:00", "2022B", "Kuliah Basis Data", "Accepted", 101L),
                new Request(102L, "2023-11-08", "10:30", "12:00", "2022C", "Kuliah Teori Bahasa Dan Otomata", "Declined", 102L)
        );
    }

    @Test
    void requestSerializationTest() throws IOException {
        Request request = new Request(100L, "2023-11-08", "10:30", "12:00", "2022A", "Kuliah Rekayasa Perangkat Lunak", "Pending", 100L);

        assertThat(json.write(request)).isStrictlyEqualToJson("request.json");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(request)).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(json.write(request)).hasJsonPathStringValue("@.borrowDate");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.borrowDate").isEqualTo("2023-11-08");
        assertThat(json.write(request)).hasJsonPathStringValue("@.startTime");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.startTime").isEqualTo("10:30");
        assertThat(json.write(request)).hasJsonPathStringValue("@.endTime");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.endTime").isEqualTo("12:00");
        assertThat(json.write(request)).hasJsonPathStringValue("@.lendee");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.lendee").isEqualTo("2022A");
        assertThat(json.write(request)).hasJsonPathStringValue("@.detail");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.detail").isEqualTo("Kuliah Rekayasa Perangkat Lunak");
        assertThat(json.write(request)).hasJsonPathStringValue("@.status");
        assertThat(json.write(request)).extractingJsonPathStringValue("@.status").isEqualTo("Pending");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.roomId");
        assertThat(json.write(request)).extractingJsonPathNumberValue("@.roomId").isEqualTo(100);
    }

    @Test
    void requestDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 100,
                    "borrowDate": "2023-11-08",
                    "startTime": "10:30",
                    "endTime": "12:00",
                    "lendee": "2022A",
                    "detail": "Kuliah Rekayasa Perangkat Lunak",
                    "status": "Pending",
                    "roomId": 100
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new Request(100L, "2023-11-08", "10:30", "12:00", "2022A", "Kuliah Rekayasa Perangkat Lunak", "Pending", 100L));
        assertThat(json.parseObject(expected).id()).isEqualTo(100L);
        assertThat(json.parseObject(expected).borrowDate()).isEqualTo("2023-11-08");
        assertThat(json.parseObject(expected).startTime()).isEqualTo("10:30");
        assertThat(json.parseObject(expected).endTime()).isEqualTo("12:00");
        assertThat(json.parseObject(expected).lendee()).isEqualTo("2022A");
        assertThat(json.parseObject(expected).detail()).isEqualTo("Kuliah Rekayasa Perangkat Lunak");
        assertThat(json.parseObject(expected).status()).isEqualTo("Pending");
        assertThat(json.parseObject(expected).roomId()).isEqualTo(100);
    }

    @Test
    void requestListSerializationTest() throws IOException {
        assertThat(jsonList.write(requests)).isStrictlyEqualToJson("requestList.json");
    }

    @Test
    void requestListDeserializationTest() throws IOException {
        String expected = """
                [
                    {"id": 100, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022A", "detail": "Kuliah Rekayasa Perangkat Lunak", "status": "Pending", "roomId": 100},
                    {"id": 101, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022B", "detail": "Kuliah Basis Data", "status": "Accepted", "roomId": 101},
                    {"id": 102, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022C", "detail": "Kuliah Teori Bahasa Dan Otomata", "status": "Declined", "roomId": 102}
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(requests);
    }
}
