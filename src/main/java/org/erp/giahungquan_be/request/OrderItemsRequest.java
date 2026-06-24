package org.erp.giahungquan_be.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemsRequest {
    @NotEmpty(message = "Danh sách món gọi không được bỏ trống")
    @Valid
    private List<OrderItemRequest> items;
}
