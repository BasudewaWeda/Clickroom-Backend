package dev.basudewa.clickroom.facility;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public boolean facilityExists(Long requestedId) {
        return facilityRepository.findFacilityById(requestedId) != null;
    }

    public Facility getFacilityById(Long requestedId) {
        return facilityRepository.findFacilityById(requestedId);
    }

    public URI createFacility(Facility newFacility, UriComponentsBuilder ucb) {
        Facility tempFacility = new Facility(
                null,
                newFacility.getFacilityName(),
                newFacility.getFacilityAmount(),
                newFacility.getRoom()
        );
        Facility savedFacility = facilityRepository.save(tempFacility);

        return ucb
                .path("/facility/{id}")
                .buildAndExpand(savedFacility.getId())
                .toUri();
    }

    public void updateFacility(Long requestedId, Facility facilityUpdate, Facility oldFacility) {
        Facility updatedFacility = new Facility(
                requestedId,
                (facilityUpdate.getFacilityName() != null) ? facilityUpdate.getFacilityName() : oldFacility.getFacilityName(),
                (facilityUpdate.getFacilityAmount() != null) ? facilityUpdate.getFacilityAmount() : oldFacility.getFacilityAmount(),
                (facilityUpdate.getRoom() != null) ? facilityUpdate.getRoom() : oldFacility.getRoom()
        );

        facilityRepository.save(updatedFacility);
    }

    public void deleteFacility(Long requestedId) {
        facilityRepository.deleteById(requestedId);
    }
}
