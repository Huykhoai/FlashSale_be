package org.erp.flashsale_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "fs_flash_event")
public class FsFlashEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fs_flash_event_id_gen")
    @SequenceGenerator(name = "fs_flash_event_id_gen", sequenceName = "fs_flash_event_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goods_id", nullable = false)
    private FsGood goods;

    @NotNull
    @Column(name = "flash_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal flashPrice;

    @NotNull
    @Column(name = "stock_count", nullable = false)
    private Integer stockCount;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

}