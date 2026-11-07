package com.products.application.product.entity

import com.products.application.common.persistence.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "products")
class ProductEntity(

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID,

    @Column(name = "name", nullable = false, length = 255)
    val name: String,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String

) : AuditableEntity()