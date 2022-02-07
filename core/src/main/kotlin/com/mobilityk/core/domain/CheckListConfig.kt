package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.CheckListConfigDTO
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
@Table(name = "tb_check_list"
)
@NoArgsConstructor
@AllArgsConstructor
data class CheckListConfig(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "question")
    var question: String? = null,

    @Column(name = "price")
    var price: Long? = null,

    @Column(name = "published")
    @NotNull
    var published: Boolean? = null,

    @Column(name = "order_index")
    var orderIndex: Int? = null,

    @Column(name = "writer_login_id")
    var writerLoginId: String? = null,

) : Comparable<CheckListConfig> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: CheckListConfig): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun create(checkListDTO: CheckListConfigDTO) {
        this.question = checkListDTO.question
        this.price = checkListDTO.price
        this.published = false
        this.orderIndex = checkListDTO.orderIndex
        this.writerLoginId = checkListDTO.writerLoginId
    }
}

