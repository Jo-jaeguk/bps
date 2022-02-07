package com.mobilityk.core.dto

import java.time.LocalDateTime

data class BranchDTO(
    var id: Long? = null,
    var branch: String? = null,
    var branchNumber: String? = null,
    var updatedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime? = null
) {
}