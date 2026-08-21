package org.erp.flashsale_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "fs_goods")
public class FsGood {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fs_goods_id_gen")
    @SequenceGenerator(name = "fs_goods_id_gen", sequenceName = "fs_goods_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 200)
    @NotNull
    @Column(name = "goods_name", nullable = false, length = 200)
    private String goodsName;

    @Size(max = 500)
    @Column(name = "goods_img", length = 500)
    private String goodsImg;

    @NotNull
    @Column(name = "goods_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal goodsPrice;

}