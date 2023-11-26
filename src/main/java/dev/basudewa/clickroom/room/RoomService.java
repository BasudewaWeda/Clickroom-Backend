package dev.basudewa.clickroom.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public boolean roomExists(Long requestedId) {
        return roomRepository.findRoomById(requestedId) != null;
    }

    public Page<Room> getRooms(Pageable pageable) {
        return roomRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "roomLocation", "roomCapacity"))
                )
        );
    }

    public Room getRoomById(Long requestedId) {
        return roomRepository.findRoomById(requestedId);
    }

    public URI createRoom(Room newRoom, UriComponentsBuilder ucb) {
        Room tempRoom = new Room(
                null,
                newRoom.getRoomName(),
                newRoom.getRoomCapacity(),
                newRoom.getRoomLocation(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        Room savedRoom = roomRepository.save(tempRoom);
        return ucb
                .path("room/{id}")
                .buildAndExpand(savedRoom.getId())
                .toUri();
    }

    public void updateRoom(Long requestedId, Room roomUpdate, Room oldRoom) {
        Room updatedRoom = new Room(
                requestedId,
                (roomUpdate.getRoomName() != null) ? roomUpdate.getRoomName() : oldRoom.getRoomName(),
                (roomUpdate.getRoomCapacity() != null) ? roomUpdate.getRoomCapacity() : oldRoom.getRoomCapacity(),
                (roomUpdate.getRoomLocation() != null) ? roomUpdate.getRoomLocation() : oldRoom.getRoomLocation(),
                oldRoom.getFacilities(),
                oldRoom.getSchedules(),
                oldRoom.getRequests()
        );

        roomRepository.save(updatedRoom);
    }

    public void deleteRoom(Long requestedId) {
        roomRepository.deleteById(requestedId);
    }
}
