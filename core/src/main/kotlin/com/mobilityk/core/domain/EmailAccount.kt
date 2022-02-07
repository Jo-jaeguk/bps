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
@Table(name = "tb_email_account")
@NoArgsConstructor
@AllArgsConstructor
data class EmailAccount(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "password")
    var password: String? = null,

) : Comparable<EmailAccount> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: EmailAccount): Int {
        return if(this.id == other.id) 1 else -1
    }
}
