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

@Entity
@Table(name = "tb_activity_log",
    indexes = [
        Index(columnList = "log_type")
    ]
)
@NoArgsConstructor
@AllArgsConstructor
data class ActivityLog(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "writer_id")
    var writerId: Long? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "member_name")
    var memberName: String? = null,

    @Column(name = "member_email")
    var memberEmail: String? = null,

    @Column(name = "member_phone")
    var memberPhone: String? = null,

    @Column(name = "member_position")
    var memberPosition: String? = null,

    @Column(name = "member_group")
    var memberGroup: String? = null,

    @Column(name = "member_branch")
    var memberBranch: String? = null,

    @Column(name = "body")
    @Lob
    var body: String? = null,

    @Column(name = "log_type")
    @Enumerated(EnumType.STRING)
    var logType: LogType? = null,


) : Comparable<ActivityLog> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: ActivityLog): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class LogType {
    MEMBER_LOG, GUIDE_LOG, GUIDE_CONFIG_LOG
}