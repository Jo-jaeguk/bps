package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_upload_mapping")
@NoArgsConstructor
@AllArgsConstructor
data class UploadMapping(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "upload_id")
    var uploadId: Long? = null,

    @Column(name = "mapping_id")
    var mappingId: Long? = null,

    @Column(name = "mapping_type")
    @Enumerated(EnumType.STRING)
    var mappingType: MappingType? = null,

) : Comparable<UploadMapping> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: UploadMapping): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class MappingType {
    NOTICE
}

