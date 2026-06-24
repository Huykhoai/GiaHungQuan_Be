package org.erp.giahungquan_be.mapper;

import org.erp.giahungquan_be.dto.MenuItemDto;
import org.erp.giahungquan_be.entity.MenuItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuItemMapper extends GenericMapper<MenuItem, MenuItemDto>{

}
