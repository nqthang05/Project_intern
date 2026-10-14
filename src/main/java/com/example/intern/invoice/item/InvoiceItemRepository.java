package com.example.intern.invoice.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceItemRepository
        extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceId(Long invoiceId);

    boolean existsByInvoiceId(Long invoiceId);

    boolean existsByMedicineId(Long medicineId);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COALESCE(SUM(i.quantity * i.unitPrice), 0)
        FROM InvoiceItem i
        WHERE i.invoiceId = :invoiceId
        """)
    BigDecimal calculateTotalByInvoiceId(
            @org.springframework.data.repository.query.Param("invoiceId")
            Long invoiceId
    );
}