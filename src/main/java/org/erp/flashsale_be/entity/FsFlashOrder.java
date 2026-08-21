package org.erp.flashsale_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fs_flash_order")
public class FsFlashOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fs_flash_order_id_gen")
    @SequenceGenerator(name = "fs_flash_order_id_gen", sequenceName = "fs_flash_order_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private FsOrder order;

    @NotNull
    @Column(name = "goods_id", nullable = false)
    private Long goodsId;

}