package com.mobilityk.core.repository

import com.mobilityk.core.domain.MemberComment
import org.springframework.data.jpa.repository.JpaRepository

interface MemberCommentRepository: JpaRepository<MemberComment, Long> {
}