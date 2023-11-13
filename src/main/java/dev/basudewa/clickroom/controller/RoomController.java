package dev.basudewa.clickroom.controller;

import dev.basudewa.clickroom.entity.Room;
import dev.basudewa.clickroom.repository.RoomRepository;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    private RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public ResponseEntity<List<Room>> findAll(Pageable pageable) {
        Page<Room> page = roomRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "location"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Room> findById(@PathVariable Long requestedId) {
        Room requestedRoom = roomRepository.findRoomById(requestedId);
        if(requestedRoom != null) {
            return ResponseEntity.ok(requestedRoom);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> createRoom(@RequestBody Room newRoom, UriComponentsBuilder ucb) {
        Room savedRoom = roomRepository.save(newRoom);
        URI locationOfNewRoom = ucb
                .path("room/{id}")
                .buildAndExpand(savedRoom.id())
                .toUri();
        return ResponseEntity.created(locationOfNewRoom).build();
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> updateRoom(@PathVariable Long requestedId, @RequestBody Room roomUpdate) {
        Room room = roomRepository.findRoomById(requestedId);
        if(room != null) {
            Room updatedRoom = new Room(requestedId, roomUpdate.capacity(), roomUpdate.location());
            roomRepository.save(updatedRoom);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/{requestedId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long requestedId) {
        if(roomRepository.findRoomById(requestedId) != null) {
            roomRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
