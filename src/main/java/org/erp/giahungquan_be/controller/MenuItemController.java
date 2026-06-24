package org.erp.giahungquan_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.common.ApiResponse;
import org.erp.giahungquan_be.request.MenuItemRequest;
import org.erp.giahungquan_be.service.MenuItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService service;

    @GetMapping
    public ApiResponse<?> getAll() {
        return ApiResponse.success("Lấy danh sách món ăn thành công", service.getAll());
    }

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody MenuItemRequest request) {
        return ApiResponse.success("Thêm món ăn thành công", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return ApiResponse.success("Cập nhật món ăn thành công", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Xóa món ăn thành công", null);
    }
}
