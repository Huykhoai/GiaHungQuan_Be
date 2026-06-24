package org.erp.giahungquan_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.common.ApiResponse;
import org.erp.giahungquan_be.request.CreateInvoiceRequest;
import org.erp.giahungquan_be.request.OrderItemsRequest;
import org.erp.giahungquan_be.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @GetMapping("/active")
    public ApiResponse<?> getActiveInvoices() {
        return ApiResponse.success(service.getActiveInvoices());
    }

    @GetMapping("/history")
    public ApiResponse<?> getInvoiceHistory(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String tableName) {
        return ApiResponse.success(service.getInvoiceHistory(date, tableName));
    }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody CreateInvoiceRequest request) {
        return ApiResponse.success("Mở bàn thành công", service.createInvoice(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getInvoice(@PathVariable Long id) {
        return ApiResponse.success(service.getInvoice(id));
    }

    @GetMapping("/{id}/items")
    public ApiResponse<?> getInvoiceItems(@PathVariable Long id, @RequestParam(required = false) String status) {
        return ApiResponse.success(service.getInvoiceItems(id, status));
    }

    @GetMapping("/items/pending")
    public ApiResponse<?> getAllPendingItems() {
        return ApiResponse.success(service.getAllPendingItems());
    }

    @PostMapping("/{id}/order")
    public ApiResponse<?> orderItems(@PathVariable Long id, @Valid @RequestBody OrderItemsRequest request) {
        service.orderItems(id, request);
        return ApiResponse.success("Gọi món thành công", null);
    }

    @PutMapping("/items/{itemId}/serve")
    public ApiResponse<?> markItemAsServed(@PathVariable Long itemId) {
        return ApiResponse.success("Đã bê ra", service.markItemAsServed(itemId));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<?> payInvoice(@PathVariable Long id) {
        service.payInvoice(id);
        return ApiResponse.success("Thanh toán thành công", null);
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<?> cancelPendingItem(@PathVariable Long itemId) {
        service.cancelPendingItem(itemId);
        return ApiResponse.success("Hủy món thành công", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteEmptyInvoice(@PathVariable Long id) {
        return ApiResponse.success("Đã xóa hóa đơn trống và giải phóng bàn",service.deleteEmptyInvoice(id));
    }
}
