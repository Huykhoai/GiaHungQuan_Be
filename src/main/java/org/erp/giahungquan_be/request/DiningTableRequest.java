package org.erp.giahungquan_be.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiningTableRequest {
    @NotBlank(message = "Tên bàn không được để trống")
    @Size(max = 50, message = "Tên bàn tối đa 50 ký tự")
    private String name;
}
