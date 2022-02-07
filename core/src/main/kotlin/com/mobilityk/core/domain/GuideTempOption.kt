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
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_temp_option"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideTempOption(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "member_id")
    @NotNull
    var memberId: Long? = null,

    @Column(name = "guide_temp_id")
    @NotNull
    var guideTempId: Long? = null,

    @Column(name = "option")
    var option: String? = null,


) : Comparable<GuideTempOption> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideTempOption): Int {
        return if(this.id == other.id) 1 else -1
    }

}

