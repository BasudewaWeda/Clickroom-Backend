package dev.basudewa.clickroom.facility;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    public Facility findFacilityById(Long id);
}
