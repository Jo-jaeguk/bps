package com.mobilityk.core.domain


import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "persistent_logins"
)
@NoArgsConstructor
@AllArgsConstructor
data class RememberMe(

    @Id
    @Column(length = 64)
    var series: String? = null,

    @Column(name = "username")
    @NotNull
    var username: String? = null,

    @Column(name = "token")
    @NotNull
    var token: String? = null,

    @Column(name = "last_used")
    var lastUsed: LocalDateTime? = null,

)
