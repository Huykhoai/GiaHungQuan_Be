package org.erp.giahungquan_be.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInvoiceRequest {
    @NotNull(message = "ID Bàn không được để trống")
    private Long tableId;
}
