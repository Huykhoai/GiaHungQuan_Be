package org.erp.giahungquan_be.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.erp.giahungquan_be.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    @Query("""
                SELECT a FROM Account a
                WHERE a.username=:username
            """)
    Optional<Account> findByUsername(String username);

    boolean existsAccountByUsername(@Size(max = 50) @NotNull String username);
}
