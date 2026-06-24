package org.erp.giahungquan_be.mapper;

import org.erp.giahungquan_be.dto.InvoiceItemDto;
import org.erp.giahungquan_be.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper extends GenericMapper<InvoiceItem, InvoiceItemDto> {
    @Override
    @Mapping(source = "menuItem.id", target = "menuItemId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    InvoiceItemDto toDto(InvoiceItem entity);

    @Override
    @Mapping(source = "menuItem.id", target = "menuItemId")
    @Mapping(source = "invoice.id", target = "invoiceId")
    List<InvoiceItemDto> toDtoList(List<InvoiceItem> entity);
}
