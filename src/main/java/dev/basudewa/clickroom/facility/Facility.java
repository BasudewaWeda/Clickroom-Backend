package dev.basudewa.clickroom.facility;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.basudewa.clickroom.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facility")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String facilityName;
    private Integer facilityAmount;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
