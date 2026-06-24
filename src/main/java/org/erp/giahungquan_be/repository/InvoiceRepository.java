package org.erp.giahungquan_be.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.erp.giahungquan_be.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
        Optional<Invoice> findByTableIdAndStatus(Long tableId, String status);

        List<Invoice> findAllByStatusAndCreatedAtGreaterThanEqualOrderByIdDesc(@Size(max = 100) @NotNull String status,
                        @NotNull LocalDateTime createdAtIsGreaterThan);

        @Query("SELECT i FROM Invoice i WHERE i.status = :status AND " +
                        "i.createdAt >= :startDate AND " +
                        "i.createdAt < :endDate AND " +
                        "(:tableName = '' OR LOWER(i.tableName) LIKE LOWER(CONCAT('%', :tableName, '%'))) " +
                        "ORDER BY i.createdAt DESC")
        List<Invoice> findInvoicesWithFilters(
                        @Param("status") String status,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("tableName") String tableName);
}
