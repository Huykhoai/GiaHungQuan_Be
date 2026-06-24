package org.erp.giahungquan_be.repository;

import org.erp.giahungquan_be.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByDeletedAtIsNullOrderByIdAsc();

    Optional<MenuItem> findByIdAndDeletedAtIsNull(Long id);

    List<MenuItem> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    boolean existsByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, Long id);
}
