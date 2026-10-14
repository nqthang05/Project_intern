package com.example.intern.invoice.item;

import com.example.intern.invoice.item.dto.InvoiceItemRequest;
import com.example.intern.invoice.item.dto.InvoiceItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
public class InvoiceItemController {

    private final InvoiceItemService itemService;

    public InvoiceItemController(InvoiceItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<InvoiceItemResponse> getAll() {
        return itemService.getAll();
    }

    @GetMapping("/{id}")
    public InvoiceItemResponse getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<InvoiceItemResponse> getByInvoiceId(
            @PathVariable Long invoiceId
    ) {
        return itemService.getByInvoiceId(invoiceId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceItemResponse create(
            @Valid @RequestBody InvoiceItemRequest request
    ) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    public InvoiceItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceItemRequest request
    ) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}