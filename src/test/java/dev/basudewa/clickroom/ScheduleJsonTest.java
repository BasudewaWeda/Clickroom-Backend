package dev.basudewa.clickroom;

import dev.basudewa.clickroom.entity.Schedule;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ScheduleJsonTest {

    @Autowired
    private JacksonTester<Schedule> json;

    @Autowired
    private JacksonTester<Schedule[]> jsonList;

    private Schedule[] schedules;

    @BeforeEach
    void setUp() {
        schedules = Arrays.array(
                new Schedule(100L, "2023-11-08", "10:30", "12:00", "2022A", "admin1", 100L),
                new Schedule(101L, "2023-11-08", "10:30", "12:00", "2022B", "admin1", 101L),
                new Schedule(102L, "2023-11-08", "10:30", "12:00", "2022C", "admin1", 102L)
        );
    }

    @Test
    void scheduleSerializationTest() throws IOException{
        Schedule schedule = new Schedule(100L, "2023-11-08", "10:30", "12:00", "2022A", "admin1", 100L);

        assertThat(json.write(schedule)).isStrictlyEqualToJson("schedule.json");
        assertThat(json.write(schedule)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(schedule)).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(json.write(schedule)).hasJsonPathStringValue("@.lendee");
        assertThat(json.write(schedule)).extractingJsonPathStringValue("@.lendee").isEqualTo("2022A");
        assertThat(json.write(schedule)).hasJsonPathStringValue("@.lender");
        assertThat(json.write(schedule)).extractingJsonPathStringValue("@.lender").isEqualTo("admin1");
        assertThat(json.write(schedule)).hasJsonPathNumberValue("@.roomId");
        assertThat(json.write(schedule)).extractingJsonPathNumberValue("@.roomId").isEqualTo(100);
    }

    @Test
    void scheduleDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 100,
                    "borrowDate": "2023-11-08",
                    "startTime": "10:30",
                    "endTime": "12:00",
                    "lendee": "2022A",
                    "lender": "admin1",
                    "roomId": 100
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new Schedule(100L, "2023-11-08", "10:30", "12:00", "2022A", "admin1", 100L));
        assertThat(json.parseObject(expected).id()).isEqualTo(100L);
        assertThat(json.parseObject(expected).borrowDate()).isEqualTo("2023-11-08");
        assertThat(json.parseObject(expected).startTime()).isEqualTo("10:30");
        assertThat(json.parseObject(expected).endTime()).isEqualTo("12:00");
        assertThat(json.parseObject(expected).lendee()).isEqualTo("2022A");
        assertThat(json.parseObject(expected).lender()).isEqualTo("admin1");
        assertThat(json.parseObject(expected).roomId()).isEqualTo(100);
    }

    @Test
    void scheduleListSerializationTest() throws IOException {
        assertThat(jsonList.write(schedules)).isStrictlyEqualToJson("scheduleList.json");
    }

    @Test
    void scheduleListDeserializationTest() throws IOException {
        String expected = """
                [
                    {"id": 100, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022A", "lender": "admin1", "roomId": 100},
                    {"id": 101, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022B", "lender": "admin1", "roomId": 101},
                    {"id": 102, "borrowDate": "2023-11-08", "startTime": "10:30", "endTime": "12:00", "lendee": "2022C", "lender": "admin1", "roomId": 102}
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(schedules);
    }
}
