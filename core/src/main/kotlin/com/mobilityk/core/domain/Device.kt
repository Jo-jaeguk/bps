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
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_device"
)
@NoArgsConstructor
@AllArgsConstructor
data class Device(

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_member")
    @NotNull
    var member: Member? = null,

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    var deviceType: DeviceType? = null,

    @Column(name = "token")
    @Lob
    var token: String? = null,

) : Comparable<Device> , Serializable, BaseEntity( 0, null, LocalDateTime.now() , LocalDateTime.now()) {
    override fun compareTo(other: Device): Int {
        return if(this.id == other.id) 1 else -1
    }
}

enum class DeviceType {
    ANDROID, IOS
}