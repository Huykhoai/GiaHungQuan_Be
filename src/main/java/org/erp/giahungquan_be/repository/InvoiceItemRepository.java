package org.erp.giahungquan_be.repository;

import org.erp.giahungquan_be.entity.InvoiceItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findAllByInvoiceIdOrderByIdAsc(Long invoiceId);

    @EntityGraph(attributePaths = {"invoice", "menuItem"})
    List<InvoiceItem> findAllByInvoiceIdAndStatusOrderByIdAsc(Long invoiceId, String status);

    @EntityGraph(attributePaths = {"invoice", "menuItem"})
    List<InvoiceItem> findAllByStatusOrderByCreatedAtAsc(String status);
}
