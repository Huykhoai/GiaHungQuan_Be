package org.erp.giahungquan_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningTableDto {
    private Long id;
    private String name;
    private String status;
}
