package com.mobilityk.core.service

import com.mobilityk.core.dto.GuidePriceConfigCommentDTO
import com.mobilityk.core.dto.mapper.GuidePriceConfigCommentMapper
import com.mobilityk.core.repository.GuidePriceConfigCommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class GuidePriceConfigCommentService(
    private val guidePriceConfigCommentRepository: GuidePriceConfigCommentRepository,
    private val guidePriceConfigCommentMapper: GuidePriceConfigCommentMapper
) {
    @Transactional
    fun findAllWithMember(): List<GuidePriceConfigCommentDTO> {
        return guidePriceConfigCommentRepository.findAllWithMember()
    }


}