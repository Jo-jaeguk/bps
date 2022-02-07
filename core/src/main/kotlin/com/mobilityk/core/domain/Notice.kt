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
import javax.persistence.Index
import javax.persistence.Lob
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_notice")
@NoArgsConstructor
@AllArgsConstructor
data class Notice(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "title")
    @NotNull
    var title: String? = null,

    @Column(name = "body")
    @NotNull
    @Lob
    var body: String? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,


) : Comparable<Notice> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Notice): Int {
        return if(this.id == other.id) 1 else -1
    }
}

data class NoticeSearchOption(
    var search: String? = null,
    var startedAt: LocalDateTime? = null,
    var endedAt: LocalDateTime? = null,
)



