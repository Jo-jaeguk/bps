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
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_guide_comment"
)
@NoArgsConstructor
@AllArgsConstructor
data class GuideComment(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_guide")
    @NotNull
    var guide: Guide? = null,

    @Column(name = "comment_type")
    @Enumerated(EnumType.STRING)
    var commentType: CommentType? = null,

    @Column(name = "body")
    @Lob
    var body: String? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,


) : Comparable<GuideComment> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: GuideComment): Int {
        return if(this.id == other.id) 1 else -1
    }
}


enum class CommentType {
    PRICE , GUIDE, INSPECT
}

data class GuideCommentSearchOption(
    var commentType: CommentType? = null,
    var writerId: Long? = null,
    var body: String? = null,
    var guide: Guide? = null
)