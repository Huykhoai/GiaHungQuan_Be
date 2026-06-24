package org.erp.giahungquan_be.mapper;

import org.erp.giahungquan_be.dto.DiningTableDto;
import org.erp.giahungquan_be.entity.DiningTable;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiningTableMapper extends GenericMapper<DiningTable,  DiningTableDto>{

}
