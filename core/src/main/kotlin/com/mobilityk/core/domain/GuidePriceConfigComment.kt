package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table

@Entity
@Table(name = "tb_guide_price_config_comment"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuidePriceConfigComment(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "body")
    @Lob
    var body: String? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,


) : Comparable<GuidePriceConfigComment> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuidePriceConfigComment): Int {
        return if(this.id == other.id) 1 else -1
    }
}

