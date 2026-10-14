package com.example.intern.medicine;

import com.example.intern.medicine.dto.MedicineRequest;
import com.example.intern.medicine.dto.MedicineResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // GET /api/medicines
    @GetMapping
    public List<MedicineResponse> getAllMedicines() {
        return medicineService.getAllMedicines();
    }

    // GET /api/medicines/{id}
    @GetMapping("/{id}")
    public MedicineResponse getMedicineById(
            @PathVariable Long id
    ) {
        return medicineService.getMedicineById(id);
    }

    // GET /api/medicines/search?name=paracetamol
    @GetMapping("/search")
    public List<MedicineResponse> searchByName(
            @RequestParam String name
    ) {
        return medicineService.searchByName(name);
    }

    // GET /api/medicines/search-ingredient?name=paracetamol
    @GetMapping("/search-ingredient")
    public List<MedicineResponse> searchByActiveIngredient(
            @RequestParam String name
    ) {
        return medicineService.searchByActiveIngredient(name);
    }

    // GET /api/medicines/status/ACTIVE
    @GetMapping("/status/{status}")
    public List<MedicineResponse> getByStatus(
            @PathVariable String status
    ) {
        return medicineService.getByStatus(status);
    }

    // POST /api/medicines
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineResponse createMedicine(
            @Valid @RequestBody MedicineRequest request
    ) {
        return medicineService.createMedicine(request);
    }

    // PUT /api/medicines/{id}
    @PutMapping("/{id}")
    public MedicineResponse updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request
    ) {
        return medicineService.updateMedicine(id, request);
    }

    // DELETE /api/medicines/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
    }
}