package org.erp.giahungquan_be.service;

import lombok.RequiredArgsConstructor;
import org.erp.giahungquan_be.dto.DiningTableDto;
import org.erp.giahungquan_be.mapper.DiningTableMapper;
import org.erp.giahungquan_be.entity.DiningTable;
import org.erp.giahungquan_be.exception.DuplicateRecordException;
import org.erp.giahungquan_be.repository.DiningTableRepository;
import org.erp.giahungquan_be.request.DiningTableRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiningTableService {

    private final DiningTableRepository repository;
    private final DiningTableMapper mapper;

    public List<DiningTableDto> getAll() {
        return mapper.toDtoList(repository.findAllByDeletedAtIsNullOrderByIdAsc());
    }

    @Transactional
    public DiningTableDto create(DiningTableRequest request) {
        if (repository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new DuplicateRecordException("Tên bàn đã tồn tại");
        }
        DiningTable table = DiningTable.builder()
                .name(request.getName())
                .status("EMPTY")
                .build();
        return mapper.toDto(repository.save(table));
    }

    @Transactional
    public DiningTableDto update(Long id, DiningTableRequest request) {
        DiningTable table = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        if (repository.existsByNameAndIdNotAndDeletedAtIsNull(request.getName(), id)) {
            throw new DuplicateRecordException("Tên bàn đã tồn tại");
        }

        table.setName(request.getName());
        return mapper.toDto(repository.save(table));
    }

    @Transactional
    public void delete(Long id) {
        DiningTable table = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
        table.setDeletedAt(LocalDateTime.now());
        repository.save(table);
    }
}
