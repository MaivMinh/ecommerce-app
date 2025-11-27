package com.minh.event_service.repository;

import com.minh.event_service.entity.Voucher;
import com.minh.event_service.payload.request.SearchVouchersRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query(value = """
              select v from Voucher v
              where (coalesce(:#{#request.campaignId}, null) is null or v.campaignId = :#{#request.campaignId})
                and (coalesce(:#{#request.code}, null) is null or v.code like %:#{#request.code}%)
                and (coalesce(:#{#request.fromExpirationDate}, null) is null or v.expirationDate >= :#{#request.fromExpirationDate})
                and (coalesce(:#{#request.toExpirationDate} , null) is null or v.expirationDate <= :#{#request.toExpirationDate})
            """)
    Page<Voucher> searchVouchers(SearchVouchersRequest request, Pageable pageable);
}
