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
@Table(name = "tb_apple_account")
@NoArgsConstructor
@AllArgsConstructor
data class AppleAccount(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "apple_uid")
    var appleUid: String? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "name")
    var name: String? = null,

) : Comparable<AppleAccount> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: AppleAccount): Int {
        return if(this.id == other.id) 1 else -1
    }
}
