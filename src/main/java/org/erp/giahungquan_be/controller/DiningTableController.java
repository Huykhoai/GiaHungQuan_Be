package org.erp.giahungquan_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.common.ApiResponse;
import org.erp.giahungquan_be.request.DiningTableRequest;
import org.erp.giahungquan_be.service.DiningTableService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dining-tables")
@RequiredArgsConstructor
public class DiningTableController {

    private final DiningTableService service;

    @GetMapping
    public ApiResponse<?> getAll() {
        return ApiResponse.success("Lấy danh sách bàn thành công", service.getAll());
    }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody DiningTableRequest request) {
        return ApiResponse.success("Thêm bàn thành công", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody DiningTableRequest request) {
        return ApiResponse.success("Cập nhật bàn thành công", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Xóa bàn thành công", null);
    }
}
