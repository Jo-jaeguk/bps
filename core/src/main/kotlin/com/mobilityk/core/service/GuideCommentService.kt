package com.mobilityk.core.service

import com.mobilityk.core.domain.CommentType
import com.mobilityk.core.domain.GuideComment
import com.mobilityk.core.domain.GuideCommentSearchOption
import com.mobilityk.core.dto.GuideCommentDTO
import com.mobilityk.core.dto.api.guide.GuideCommentForAdminDTO
import com.mobilityk.core.dto.mapper.GuideCommentMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.GuideCommentRepository
import com.mobilityk.core.repository.GuideRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuideCommentService(
    private val guideCommentRepository: GuideCommentRepository,
    private val guideRepository: GuideRepository,
    private val guideCommentMapper: GuideCommentMapper
) {
    @Transactional
    fun findAllByGuideId(guideId: Long, commentType: CommentType): List<GuideCommentForAdminDTO> {
        val guide = guideRepository.findById(guideId).orElseThrow { CommException("not found guide") }
        val searchOption = GuideCommentSearchOption(
            guide = guide,
            commentType = commentType
        )
        return guideCommentRepository.findAllByGuideForAdmin(searchOption)
    }
}