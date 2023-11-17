package dev.basudewa.clickroom;

import dev.basudewa.clickroom.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RoomJsonTest {

    @Autowired
    private JacksonTester<Room> json;

    @Autowired
    private JacksonTester<Room[]> jsonList;

    private Room[] rooms;

    @BeforeEach
    void setUp() {
        rooms = Arrays.array(
                new Room(100L, "Ruang 2.2", 50, "fmipa"),
                new Room(101L, "Ruang 2.1", 40, "fk"),
                new Room(102L, "Ruang 2.3", 30, "fh")
        );
    }

    @Test
    void roomSerializationTest() throws IOException {
        Room room = new Room(100L, "Ruang 2.2", 50, "fmipa");
        assertThat(json.write(room)).isStrictlyEqualToJson("room.json");
        assertThat(json.write(room)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(room)).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(json.write(room)).hasJsonPathStringValue("@.name");
        assertThat(json.write(room)).extractingJsonPathStringValue("@.name").isEqualTo("Ruang 2.2");
        assertThat(json.write(room)).hasJsonPathNumberValue("@.capacity");
        assertThat(json.write(room)).extractingJsonPathNumberValue("@.capacity").isEqualTo(50);
        assertThat(json.write(room)).hasJsonPathStringValue("@.location");
        assertThat(json.write(room)).extractingJsonPathStringValue("@.location").isEqualTo("fmipa");
    }

    @Test
    void roomDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 100,
                    "name": "Ruang 2.2",
                    "capacity": 50,
                    "location": "fmipa"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new Room(100L, "Ruang 2.2", 50, "fmipa"));
        assertThat(json.parseObject(expected).id()).isEqualTo(100);
        assertThat(json.parseObject(expected).name()).isEqualTo("Ruang 2.2");
        assertThat(json.parseObject(expected).capacity()).isEqualTo(50);
        assertThat(json.parseObject(expected).location()).isEqualTo("fmipa");
    }

    @Test
    void roomListSerializationTest() throws IOException {
        assertThat(jsonList.write(rooms)).isStrictlyEqualToJson("roomList.json");
    }

    @Test
    void roomListDeserializationTest() throws IOException {
        String expected = """
                [
                    {"id": 100, "name": "Ruang 2.2", "capacity": 50, "location": "fmipa"},
                    {"id": 101, "name": "Ruang 2.1", "capacity": 40, "location": "fk"},
                    {"id": 102, "name": "Ruang 2.3", "capacity": 30, "location": "fh"}
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(rooms);
    }
}
