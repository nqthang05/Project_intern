package com.example.intern.invoice.item;

import com.example.intern.invoice.Invoice;
import com.example.intern.invoice.InvoiceRepository;
import com.example.intern.invoice.item.dto.InvoiceItemRequest;
import com.example.intern.invoice.item.dto.InvoiceItemResponse;
import com.example.intern.medicine.MedicineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class InvoiceItemService {

    private static final Set<String> ITEM_TYPES =
            Set.of("SERVICE", "MEDICINE", "OTHER");

    private final InvoiceItemRepository itemRepository;
    private final InvoiceRepository invoiceRepository;
    private final MedicineRepository medicineRepository;

    public InvoiceItemService(
            InvoiceItemRepository itemRepository,
            InvoiceRepository invoiceRepository,
            MedicineRepository medicineRepository
    ) {
        this.itemRepository = itemRepository;
        this.invoiceRepository = invoiceRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<InvoiceItemResponse> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvoiceItemResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<InvoiceItemResponse> getByInvoiceId(Long invoiceId) {
        validateId(invoiceId, "ID hóa đơn");
        findInvoice(invoiceId);

        return itemRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public InvoiceItemResponse create(InvoiceItemRequest request) {
        validateRequest(request);
        checkReferences(request);

        Invoice invoice = findInvoice(request.invoiceId());

        if ("PAID".equals(invoice.getStatus())
                || "CANCELLED".equals(invoice.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể thêm chi tiết vào hóa đơn đã thanh toán hoặc đã hủy"
            );
        }

        InvoiceItem item = new InvoiceItem();
        applyRequest(item, request);

        InvoiceItem saved = itemRepository.saveAndFlush(item);
        recalculateInvoice(saved.getInvoiceId());

        return toResponse(itemRepository.findById(saved.getId())
                .orElseThrow());
    }

    @Transactional
    public InvoiceItemResponse update(
            Long id,
            InvoiceItemRequest request
    ) {
        validateRequest(request);
        checkReferences(request);

        InvoiceItem item = findById(id);
        Long oldInvoiceId = item.getInvoiceId();

        Invoice invoice = findInvoice(oldInvoiceId);
        if ("PAID".equals(invoice.getStatus())
                || "CANCELLED".equals(invoice.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể sửa chi tiết hóa đơn đã thanh toán hoặc đã hủy"
            );
        }

        Invoice targetInvoice = findInvoice(request.invoiceId());
        if ("PAID".equals(targetInvoice.getStatus())
                || "CANCELLED".equals(targetInvoice.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể thêm chi tiết vào hóa đơn đã thanh toán hoặc đã hủy"
            );
        }

        applyRequest(item, request);
        itemRepository.saveAndFlush(item);

        recalculateInvoice(oldInvoiceId);

        if (!oldInvoiceId.equals(request.invoiceId())) {
            recalculateInvoice(request.invoiceId());
        }

        return toResponse(itemRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void delete(Long id) {
        InvoiceItem item = findById(id);
        Long invoiceId = item.getInvoiceId();

        Invoice invoice = findInvoice(invoiceId);
        if ("PAID".equals(invoice.getStatus())
                || "CANCELLED".equals(invoice.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể xóa chi tiết của hóa đơn đã thanh toán hoặc đã hủy"
            );
        }

        itemRepository.delete(item);
        itemRepository.flush();

        recalculateInvoice(invoiceId);
    }

    private void recalculateInvoice(Long invoiceId) {
        Invoice invoice = findInvoice(invoiceId);

        BigDecimal total = itemRepository
                .calculateTotalByInvoiceId(invoiceId);

        invoice.setTotalAmount(
                total == null ? BigDecimal.ZERO : total
        );

        invoiceRepository.save(invoice);
    }

    private Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hóa đơn có ID: " + id
                ));
    }

    private InvoiceItem findById(Long id) {
        validateId(id, "ID chi tiết hóa đơn");

        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chi tiết hóa đơn có ID: " + id
                ));
    }

    private void checkReferences(InvoiceItemRequest request) {
        findInvoice(request.invoiceId());

        if (request.medicineId() != null
                && !medicineRepository.existsById(request.medicineId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy thuốc có ID: " + request.medicineId()
            );
        }
    }

    private void validateRequest(InvoiceItemRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dữ liệu chi tiết hóa đơn không được để trống"
            );
        }

        validateId(request.invoiceId(), "ID hóa đơn");

        if (request.itemName() == null
                || request.itemName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên khoản thu không được để trống"
            );
        }

        String type = normalize(request.itemType());
        if (type == null || !ITEM_TYPES.contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loại khoản thu phải là SERVICE, MEDICINE hoặc OTHER"
            );
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng phải lớn hơn 0"
            );
        }

        if (request.unitPrice() == null
                || request.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn giá không được âm"
            );
        }

        if ("MEDICINE".equals(type) && request.medicineId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Khoản thu loại MEDICINE phải có ID thuốc"
            );
        }
    }

    private void applyRequest(
            InvoiceItem item,
            InvoiceItemRequest request
    ) {
        item.setInvoiceId(request.invoiceId());
        item.setMedicineId(request.medicineId());
        item.setItemName(request.itemName().trim());
        item.setItemType(normalize(request.itemType()));
        item.setQuantity(request.quantity());
        item.setUnitPrice(request.unitPrice());
    }

    private void validateId(Long id, String field) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    field + " phải lớn hơn 0"
            );
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim().toUpperCase();
    }

    private InvoiceItemResponse toResponse(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getInvoiceId(),
                item.getMedicineId(),
                item.getItemName(),
                item.getItemType(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal(),
                item.getCreatedAt()
        );
    }
}