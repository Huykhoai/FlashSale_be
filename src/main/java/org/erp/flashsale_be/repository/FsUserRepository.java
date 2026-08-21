package org.erp.flashsale_be.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.erp.flashsale_be.entity.FsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FsUserRepository extends JpaRepository<FsUser, Long> {
    @Query("""
                SELECT a FROM FsUser a
                WHERE (a.username=:username)
            """)
    Optional<FsUser> findByUsername(String username);

    boolean existsFsUserByUsernameAndIsDisable(@Size(max = 50) @NotNull String username, @NotNull Boolean isDisable);

    boolean existsFsUserByEmailAndIsDisable(@Size(max = 100) String email, @NotNull Boolean isDisable);
}
