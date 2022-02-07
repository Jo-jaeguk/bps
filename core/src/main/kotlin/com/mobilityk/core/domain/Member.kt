package com.mobilityk.core.domain


import com.mobilityk.core.domain.base.BaseEntity
import com.mobilityk.core.dto.api.member.MemberJoinVM
import com.mobilityk.core.dto.api.member.MemberVM
import com.mobilityk.core.enumuration.ROLE
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_member",
    indexes = [
        Index(columnList = "email_address"),
        Index(columnList = "phone"),
        Index(columnList = "name")
    ]
)
@NoArgsConstructor
@AllArgsConstructor
data class Member(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    var accountType: AccountType? = null,

    @Column(name = "email_address")
    var emailAddress: String? = null,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "birth_date")
    var birthDate: LocalDateTime? = null,

    @Column(name = "phone", unique = true)
    var phone: String? = null,

    @Column(name = "position")
    var position: String? = null,

    @Column(name = "group_name")
    var groupName: String? = null,

    @Column(name = "branch")
    var branch: String? = null,

    @Column(name = "manage_branch")
    var manageBranch: String? = null,

    @Column(name = "manage_admin_id")
    var manageAdminId: Long? = null,

    @Column(name = "manage_admin_name")
    var manageAdminName: String? = null,

    @Column(name = "manage_admin_email_address")
    var manageAdminEmailAddress: String? = null,

    @Column(name = "manage_admin_phone")
    var manageAdminPhone: String? = null,

    @Column(name = "activated")
    @NotNull
    var activated: Boolean? = null,

    @Column(name = "point")
    @NotNull
    var point: Long? = null,

    @Column(name = "access_yn")
    var accessYn: Boolean? = null,

    @Column(name = "kakao_talk_yn")
    var kakaoTalkYn: Boolean? = null,

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(name = "guide_count")
    @NotNull
    var guideCount: Long? = null,

    @Column(name = "comment")
    @Lob
    var comment: String? = null,

    @Column(name = "access_token")
    @Lob
    var accessToken: String? = null,

    @Column(name = "refresh_token")
    @Lob
    var refreshToken: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<ROLE>? = mutableSetOf(),

    @OneToMany(mappedBy = "member" , cascade = [CascadeType.ALL])
    var commentList: MutableList<MemberComment>? = mutableListOf(),

    @OneToMany(mappedBy = "member" , cascade = [CascadeType.ALL])
    var deviceList: MutableList<Device>? = mutableListOf(),

    @Column(name = "last_accessed_at")
    var lastAccessedAt: LocalDateTime? = null

) : Comparable<Member> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Member): Int {
        return if(this.id == other.id) 1 else -1
    }

    fun updateLastAccessdedAt() {
        this.lastAccessedAt = LocalDateTime.now()
    }
    fun createMember(memberJoinVM: MemberJoinVM) {
        this.emailAddress = memberJoinVM.emailAddress
        this.password = memberJoinVM.password
        this.roles = memberJoinVM.roles ?: mutableSetOf(ROLE.NEW)
        this.point = 0L
        this.accessYn = false
        this.kakaoTalkYn = true
        this.accountType = memberJoinVM.accountType
        this.branch = ""
        this.groupName = ""
        this.name = ""
        this.phone = ""
        this.position = ""
        this.guideCount = 0
    }

    fun updateMyInfo(memberVM: MemberVM) {
        memberVM.birthDate?.let {
            this.birthDate = it
        }
        memberVM.branch?.let {
            this.branch = it
        }
        memberVM.comment?.let {
            this.comment = it
        }
        memberVM.groupName?.let {
            this.groupName = it
        }
        memberVM.name?.let {
            this.name = it
        }
        memberVM.phone?.let {
            this.phone = it
        }
        memberVM.point?.let {
            this.point = it
        }
        memberVM.position?.let {
            this.position = it
        }
        memberVM.roles?.let {
            if(!it.isNullOrEmpty()) {
                this.roles = it
            }
        }
        memberVM.accessYn?.let {
            this.accessYn = it
        }
        memberVM.kakaoTalkYn?.let {
            this.kakaoTalkYn = it
        }
    }

    fun updateManageBranch(admin: Member) {
        admin.branch?.let {
            this.manageBranch = it
        }
        admin.name?.let {
            this.manageAdminName = it
        }
        admin.emailAddress?.let {
            this.manageAdminEmailAddress = it
        }
        admin.phone?.let {
            this.manageAdminPhone = it
        }
    }

    fun updateManageBranch(manageBranch: String, admin: Member) {
        this.manageBranch = manageBranch
        admin.name?.let {
            this.manageAdminName = it
        }
        admin.emailAddress?.let {
            this.manageAdminEmailAddress = it
        }
        admin.phone?.let {
            this.manageAdminPhone = it
        }
    }

    fun increasePoint(point: Long) {
        val beforePoint = this.point ?: 0L
        this.point = beforePoint.plus(point)
    }

    fun incrementGuideCount() {
        val count = this.guideCount ?: 0L
        this.guideCount = count.plus(1)
    }

}

enum class AccountType(val description: String) {
    KAKAO("카카오"), NAVER("네이버"), EMAIL("이메일"), APPLE("애플")
}

enum class Gender{
    MALE, FEMALE
}