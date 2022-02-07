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
import javax.persistence.Table

@Entity
@Table(name = "tb_kakao_account")
@NoArgsConstructor
@AllArgsConstructor
data class KakaoAccount(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "kakao_id")
    var kakaoId: Long? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "age_range")
    var ageRange: String? = null,

    @Column(name = "birthyear")
    var birthYear: String? = null,

    @Column(name = "birthday")
    var birthDay: String? = null,

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "nickname")
    var nickName: String? = null,

    @Column(name = "thumbnail_image_url")
    var thumbnailImageUrl: String? = null,

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,

) : Comparable<KakaoAccount> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: KakaoAccount): Int {
        return if(this.id == other.id) 1 else -1
    }
}
