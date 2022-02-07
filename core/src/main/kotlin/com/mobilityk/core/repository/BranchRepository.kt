package com.mobilityk.core.repository

import com.mobilityk.core.domain.Branch
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BranchRepository: JpaRepository<Branch, Long> {
    fun findByBranch(branch: String): Optional<Branch>
}