package com.mobilityk.core.service

import com.mobilityk.core.dto.MemberCommentDTO
import com.mobilityk.core.dto.mapper.MemberCommentMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.MemberCommentRepository
import com.mobilityk.core.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class MemberCommentService(
    private val memberCommentRepository: MemberCommentRepository,
    private val memberRepository: MemberRepository,
    private val memberCommentMapper: MemberCommentMapper
) {

    @Transactional
    fun createComment(memberId: Long, memberCommentDTO: MemberCommentDTO, writerId: Long): MemberCommentDTO {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found member")

        val storedMember = member.get()

        val memberComment = memberCommentMapper.toEntity(memberCommentDTO)
        memberComment.writerId = writerId
        memberComment.member = storedMember

        return memberCommentMapper.toDto(memberCommentRepository.save(memberComment))
    }

    @Transactional
    fun deleteComment(memberId: Long, commentId: Long): Unit {
        val member = memberRepository.findById(memberId)
        if(member.isEmpty) throw CommException("not found member")
        return memberCommentRepository.deleteById(commentId)
    }
}