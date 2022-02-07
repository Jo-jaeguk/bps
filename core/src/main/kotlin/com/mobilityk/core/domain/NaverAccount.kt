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

@Entity
@Table(name = "tb_naver_account")
@NoArgsConstructor
@AllArgsConstructor
data class NaverAccount(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "naver_id")
    var naverId: String? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "nick_name")
    var nickname: String? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "age")
    var age: String? = null,

    @Column(name = "birth_day")
    var birthday: String? = null,

    @Column(name = "profile_image")
    var profileImage: String? = null,

    @Column(name = "birth_year")
    var birthyear: String? = null,

    @Column(name = "mobile")
    var mobile: String? = null,

) : Comparable<NaverAccount> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: NaverAccount): Int {
        return if(this.id == other.id) 1 else -1
    }
}
