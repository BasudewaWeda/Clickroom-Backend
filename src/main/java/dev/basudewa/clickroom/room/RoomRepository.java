package dev.basudewa.clickroom.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RoomRepository extends JpaRepository<Room, Long>, PagingAndSortingRepository<Room, Long> {
    public Room findRoomById(Long id);
}
