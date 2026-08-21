package org.erp.flashsale_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "fs_order")
public class FsOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fs_order_id_gen")
    @SequenceGenerator(name = "fs_order_id_gen", sequenceName = "fs_order_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "goods_id", nullable = false)
    private Long goodsId;

    @Size(max = 200)
    @Column(name = "goods_name", length = 200)
    private String goodsName;

    @Column(name = "goods_price", precision = 10, scale = 2)
    private BigDecimal goodsPrice;

    @ColumnDefault("0")
    @Column(name = "status")
    private Short status;

    @ColumnDefault("now()")
    @Column(name = "create_time")
    private LocalDateTime createTime;

}