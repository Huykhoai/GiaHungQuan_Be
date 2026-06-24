package org.erp.giahungquan_be.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 100, message = "Tên món tối đa 100 ký tự")
    private String name;

    @NotNull(message = "Giá món không được để trống")
    @Min(value = 0, message = "Giá món phải >= 0")
    private BigDecimal price;

    @Size(max = 50, message = "Danh mục tối đa 50 ký tự")
    private String category;
}
