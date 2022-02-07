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
import javax.persistence.Index
import javax.persistence.Lob
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_notification",
    indexes = [
        Index(columnList = "member_id")
    ]
)
@NoArgsConstructor
@AllArgsConstructor
data class Notification(

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

    @Column(name = "read_yn")
    var readYn: Boolean? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    var targetType: TargetType? = null,

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    var notificationType: NotificationType? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,

) : Comparable<Notification> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Notification): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class TargetType {
    SPECIPIC_USER, ALL_USER
}

enum class NotificationType {
    TEXT, HTML
}