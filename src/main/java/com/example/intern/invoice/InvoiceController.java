package com.example.intern.invoice;

import com.example.intern.invoice.dto.InvoiceRequest;
import com.example.intern.invoice.dto.InvoiceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<InvoiceResponse> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @GetMapping("/appointment/{appointmentId}")
    public InvoiceResponse getByAppointmentId(
            @PathVariable Long appointmentId
    ) {
        return invoiceService.getByAppointmentId(appointmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse create(
            @Valid @RequestBody InvoiceRequest request
    ) {
        return invoiceService.create(request);
    }

    @PutMapping("/{id}")
    public InvoiceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest request
    ) {
        return invoiceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        invoiceService.delete(id);
    }
}