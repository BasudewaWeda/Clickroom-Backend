package dev.basudewa.clickroom.repository;

import dev.basudewa.clickroom.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestRepository extends CrudRepository<Request, Long>, PagingAndSortingRepository<Request, Long> {
    public Page<Request> findRequestByLendee(String lendee, Pageable pageable);
    public Request findRequestByIdAndLendee(Long id, String lendee);
    public Request findRequestById(Long id);
}
