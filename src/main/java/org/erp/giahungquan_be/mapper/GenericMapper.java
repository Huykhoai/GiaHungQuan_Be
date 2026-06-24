package org.erp.giahungquan_be.mapper;

import org.erp.giahungquan_be.dto.MenuItemDto;
import org.erp.giahungquan_be.entity.MenuItem;
import org.mapstruct.Named;

import java.util.List;

public interface GenericMapper<E, D> {
    @Named("toDto")
    D toDto(E entity);
    @Named("toEntity")
    E toEntity(D dto);

    @Named("toListDto")
    List<D> toDtoList(List<E> entityList);

}
