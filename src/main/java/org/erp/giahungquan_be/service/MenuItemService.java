package org.erp.giahungquan_be.service;

import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.dto.MenuItemDto;
import org.erp.giahungquan_be.mapper.MenuItemMapper;
import org.erp.giahungquan_be.entity.MenuItem;
import org.erp.giahungquan_be.exception.DuplicateRecordException;
import org.erp.giahungquan_be.repository.MenuItemRepository;
import org.erp.giahungquan_be.request.MenuItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository repository;
    private final MenuItemMapper mapper;

    public List<MenuItemDto> getAll() {
        return mapper.toDtoList(repository.findAllByDeletedAtIsNullOrderByIdAsc());
    }

    @Transactional
    public MenuItemDto create(MenuItemRequest request) {
        if (repository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new DuplicateRecordException("Tên món đã tồn tại");
        }
        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        return mapper.toDto(repository.save(item));
    }

    @Transactional
    public MenuItemDto update(Long id, MenuItemRequest request) {
        MenuItem item = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));

        if (repository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
            throw new DuplicateRecordException("Tên món đã tồn tại");
        }

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        return mapper.toDto(repository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        MenuItem item = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
        item.setDeletedAt(LocalDateTime.now());
        repository.save(item);
    }
}
