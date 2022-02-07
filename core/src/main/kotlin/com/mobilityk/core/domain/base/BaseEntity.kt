package com.mobilityk.core.domain.base


import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

@MappedSuperclass
open class BaseEntity (

    @Transient
    protected  var result: Int= 0,

    @Transient
    protected var description: String? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = LocalDateTime.now(),

    @CreatedDate
    var createdAt: LocalDateTime? = LocalDateTime.now()

    )  : Serializable {

}