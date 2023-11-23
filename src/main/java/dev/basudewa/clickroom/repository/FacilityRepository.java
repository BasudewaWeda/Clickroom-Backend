package dev.basudewa.clickroom.repository;

import dev.basudewa.clickroom.entity.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FacilityRepository extends CrudRepository<Facility, Long>, PagingAndSortingRepository<Facility, Long>{
    public Page<Facility> findFacilityByRoomId(Long roomId, Pageable pageable);

    public Facility findFacilityById(Long id);
}
