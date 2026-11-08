package com.products.application.price.entity

import com.products.application.common.persistence.AuditableEntity
import com.products.application.product.entity.ProductEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "prices")
class PriceEntity(

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID,

    @Column(name = "value", nullable = false, precision = 11, scale = 6)
    val value: BigDecimal,

    @Column(name = "init_date", nullable = false)
    val initDate: LocalDate,

    @Column(name = "end_date", nullable = true)
    val endDate: LocalDate?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity

) : AuditableEntity()