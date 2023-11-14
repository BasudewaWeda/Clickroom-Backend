package dev.basudewa.clickroom;

import dev.basudewa.clickroom.entity.Facility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class FacilityJsonTest {

    @Autowired
    private JacksonTester<Facility> json;

    @Autowired
    private JacksonTester<Facility[]> jsonList;

    private Facility[] facilities;

    @BeforeEach
    void setUp() {
        facilities = Arrays.array(
                new Facility(100L, "Chair", 40, 100L),
                new Facility(101L, "Table", 20, 100L),
                new Facility(102L, "AC", 2, 100L)
        );
    }

    @Test
    void facilitySerializationTest() throws IOException {
        Facility facility = new Facility(100L, "Chair", 40, 100L);
        assertThat(json.write(facility)).isStrictlyEqualToJson("facility.json");
        assertThat(json.write(facility)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(facility)).extractingJsonPathNumberValue("@.id").isEqualTo(100);
        assertThat(json.write(facility)).hasJsonPathStringValue("@.facilityName");
        assertThat(json.write(facility)).extractingJsonPathStringValue("@.facilityName").isEqualTo("Chair");
        assertThat(json.write(facility)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(facility)).extractingJsonPathNumberValue("@.amount").isEqualTo(40);
        assertThat(json.write(facility)).hasJsonPathNumberValue("@.roomId");
        assertThat(json.write(facility)).extractingJsonPathNumberValue("@.roomId").isEqualTo(100);
    }

    @Test
    void facilityDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 100,
                    "facilityName": "Chair",
                    "amount": 40,
                    "roomId": 100
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new Facility(100L, "Chair", 40, 100L));
        assertThat(json.parseObject(expected).id()).isEqualTo(100);
        assertThat(json.parseObject(expected).facilityName()).isEqualTo("Chair");
        assertThat(json.parseObject(expected).amount()).isEqualTo(40);
        assertThat(json.parseObject(expected).roomId()).isEqualTo(100);
    }

    @Test
    void facilityListSerializationTest() throws IOException {
        assertThat(jsonList.write(facilities)).isStrictlyEqualToJson("facilityList.json");
    }

    @Test
    void facilityListDeserializationTest() throws IOException {
        String expected = """
                [
                    {"id": 100, "facilityName": "Chair", "amount": 40, "roomId": 100},
                    {"id": 101, "facilityName": "Table", "amount": 20, "roomId": 100},
                    {"id": 102, "facilityName": "AC", "amount": 2, "roomId": 100}
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(facilities);
    }
}
