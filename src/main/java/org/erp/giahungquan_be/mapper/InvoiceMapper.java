package org.erp.giahungquan_be.mapper;

import org.erp.giahungquan_be.dto.InvoiceDto;
import org.erp.giahungquan_be.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface InvoiceMapper extends GenericMapper<Invoice, InvoiceDto>{
    @Override
    @Mapping(source = "table.id", target = "tableId")
    InvoiceDto toDto(Invoice invoice);

    @Override
    @Mapping(source = "table.id", target = "tableId")
    List<InvoiceDto> toDtoList(List<Invoice> entityList);

}
