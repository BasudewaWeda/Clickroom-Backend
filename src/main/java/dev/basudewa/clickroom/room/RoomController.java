package dev.basudewa.clickroom.room;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> findAll(Pageable pageable) {
        Page<Room> page = roomService.getRooms(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Room> findById(@PathVariable Long requestedId) {
        Room requestedRoom = roomService.getRoomById(requestedId);
        if(requestedRoom != null) {
            return ResponseEntity.ok(requestedRoom);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<URI> createRoom(@RequestBody Room newRoom, UriComponentsBuilder ucb) {
        URI locationOfNewRoom = roomService.createRoom(newRoom, ucb);
        return ResponseEntity.created(locationOfNewRoom).body(locationOfNewRoom);
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> updateRoom(@PathVariable Long requestedId, @RequestBody Room roomUpdate) {
        Room room = roomService.getRoomById(requestedId);
        if(room != null) {
            roomService.updateRoom(requestedId, roomUpdate, room);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/{requestedId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long requestedId) {
        if(roomService.roomExists(requestedId)) {
            roomService.deleteRoom(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
