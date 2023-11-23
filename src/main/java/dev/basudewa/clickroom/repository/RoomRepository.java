package dev.basudewa.clickroom.repository;

import dev.basudewa.clickroom.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RoomRepository extends CrudRepository<Room, Long>, PagingAndSortingRepository<Room, Long> {
    public Room findRoomById(Long id);
}
