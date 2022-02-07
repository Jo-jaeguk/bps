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
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_check_list"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideCheckList(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_guide")
    @NotNull
    var guide: Guide? = null,

    @Column(name = "question")
    var question: String? = null,

    @Column(name = "price")
    var price: Long? = null,

) : Comparable<GuideCheckList> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideCheckList): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(
        guide: Guide,
        question: String,
        price: Long
    ) {
        this.guide = guide
        this.question = question
        this.price = price
    }
}

