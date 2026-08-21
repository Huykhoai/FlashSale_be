package org.erp.flashsale_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fs_user")
public class FsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fs_user_id_gen")
    @SequenceGenerator(name = "fs_user_id_gen", sequenceName = "fs_user_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 64)
    @NotNull
    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @ColumnDefault("now()")
    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @NotNull
    @ColumnDefault("0")
    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @NotNull
    @ColumnDefault("false")
    @Builder.Default
    @Column(name = "is_disable", nullable = false)
    private Boolean isDisable = false;

}