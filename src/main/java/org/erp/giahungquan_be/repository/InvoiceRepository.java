package org.erp.giahungquan_be.repository;

import org.erp.giahungquan_be.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByTableIdAndStatus(Long tableId, String status);

    List<Invoice> findAllByStatusOrderByIdDesc(String status);
}
