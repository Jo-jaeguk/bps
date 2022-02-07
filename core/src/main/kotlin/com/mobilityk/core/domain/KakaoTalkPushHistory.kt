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
import javax.persistence.Lob
import javax.persistence.Table

@Entity
@Table(name = "tb_kakao_talk_push_history"
)
@NoArgsConstructor
@AllArgsConstructor
data class KakaoTalkPushHistory(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "call_back")
    var callBack: String? = null,

    @Column(name = "send_message")
    @Lob
    var sendMessage: String? = null,

    @Column(name = "template_code")
    var templateCode: String? = null,

    @Column(name = "result_code")
    var resultCode: String? = null,

    @Column(name = "result_message")
    var resultMessage: String? = null,

    @Column(name = "cmid")
    var cmid: String? = null,


) : Comparable<KakaoTalkPushHistory> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: KakaoTalkPushHistory): Int {
        return if(this.id == other.id) 1 else -1
    }
}
