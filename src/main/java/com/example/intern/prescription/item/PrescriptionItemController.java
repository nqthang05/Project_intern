package com.example.intern.prescription.item;

import com.example.intern.prescription.item.dto.PrescriptionItemRequest;
import com.example.intern.prescription.item.dto.PrescriptionItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription-items")
public class PrescriptionItemController {

    private final PrescriptionItemService itemService;

    public PrescriptionItemController(
            PrescriptionItemService itemService
    ) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<PrescriptionItemResponse> getAll() {
        return itemService.getAll();
    }

    @GetMapping("/{id}")
    public PrescriptionItemResponse getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping("/prescription/{prescriptionId}")
    public List<PrescriptionItemResponse> getByPrescriptionId(
            @PathVariable Long prescriptionId
    ) {
        return itemService.getByPrescriptionId(prescriptionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionItemResponse create(
            @Valid @RequestBody PrescriptionItemRequest request
    ) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    public PrescriptionItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionItemRequest request
    ) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}