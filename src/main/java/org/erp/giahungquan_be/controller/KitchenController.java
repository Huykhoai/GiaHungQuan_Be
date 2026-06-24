package org.erp.giahungquan_be.controller;

import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.common.ApiResponse;
import org.erp.giahungquan_be.repository.InvoiceItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final InvoiceItemRepository invoiceItemRepository;

    @GetMapping("/pending")
    public ApiResponse<?> getPendingItems() {
        return ApiResponse.success(
                invoiceItemRepository.findAllByStatusOrderByCreatedAtAsc("PENDING"));
    }
}
