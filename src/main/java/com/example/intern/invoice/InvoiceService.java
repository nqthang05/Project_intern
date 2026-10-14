package com.example.intern.invoice;

import com.example.intern.appointment.AppointmentRepository;
import com.example.intern.invoice.dto.InvoiceRequest;
import com.example.intern.invoice.dto.InvoiceResponse;
import com.example.intern.invoice.item.InvoiceItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class InvoiceService {

    private static final Set<String> STATUSES =
            Set.of("UNPAID", "PAID", "CANCELLED");

    private static final Set<String> PAYMENT_METHODS =
            Set.of("CASH", "BANK_TRANSFER", "CARD");

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceItemRepository itemRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            AppointmentRepository appointmentRepository,
            InvoiceItemRepository itemRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getByAppointmentId(Long appointmentId) {
        validateId(appointmentId, "ID lịch hẹn");

        Invoice invoice = invoiceRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hóa đơn của lịch hẹn: "
                                + appointmentId
                ));

        return toResponse(invoice);
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        validateRequest(request);
        checkAppointment(request.appointmentId());

        if (invoiceRepository.existsByAppointmentId(
                request.appointmentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lịch hẹn này đã có hóa đơn"
            );
        }

        Invoice invoice = new Invoice();
        invoice.setAppointmentId(request.appointmentId());
        invoice.setTotalAmount(BigDecimal.ZERO);
        applyRequest(invoice, request);

        Invoice saved = invoiceRepository.saveAndFlush(invoice);
        return toResponse(findById(saved.getId()));
    }

    @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest request) {
        validateRequest(request);

        Invoice invoice = findById(id);
        checkAppointment(request.appointmentId());

        invoiceRepository.findByAppointmentId(request.appointmentId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Lịch hẹn này đã có hóa đơn khác"
                    );
                });

        invoice.setAppointmentId(request.appointmentId());
        applyRequest(invoice, request);

        return toResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public void delete(Long id) {
        Invoice invoice = findById(id);

        if (itemRepository.existsByInvoiceId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Không thể xóa hóa đơn còn chi tiết"
            );
        }

        invoiceRepository.delete(invoice);
    }

    private Invoice findById(Long id) {
        validateId(id, "ID hóa đơn");

        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hóa đơn có ID: " + id
                ));
    }

    private void checkAppointment(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy lịch hẹn có ID: " + appointmentId
            );
        }
    }

    private void validateRequest(InvoiceRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dữ liệu hóa đơn không được để trống"
            );
        }

        validateId(request.appointmentId(), "ID lịch hẹn");

        String status = normalize(request.status());
        if (status != null && !STATUSES.contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trạng thái phải là UNPAID, PAID hoặc CANCELLED"
            );
        }

        String method = normalize(request.paymentMethod());
        if (method != null && !PAYMENT_METHODS.contains(method)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phương thức thanh toán phải là CASH, BANK_TRANSFER hoặc CARD"
            );
        }
    }

    private void applyRequest(
            Invoice invoice,
            InvoiceRequest request
    ) {
        String status = normalize(request.status());
        String method = normalize(request.paymentMethod());

        invoice.setStatus(status == null ? "UNPAID" : status);
        invoice.setPaymentMethod(method);
        invoice.setNotes(trimToNull(request.notes()));
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

    private String trimToNull(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getAppointmentId(),
                invoice.getInvoiceDate(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                invoice.getPaymentMethod(),
                invoice.getNotes(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}