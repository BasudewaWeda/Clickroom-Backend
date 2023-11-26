package dev.basudewa.clickroom.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestRepository extends JpaRepository<Request, Long>, PagingAndSortingRepository<Request, Long>{
    public Page<Request> findRequestByLendee(String lendee, Pageable pageable);
    public Request findRequestByIdAndLendee(Long id, String lendee);
    public Request findRequestById(Long id);
}
