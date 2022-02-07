package com.mobilityk.core.repository

import com.mobilityk.core.domain.Guide
import com.mobilityk.core.domain.GuideComment
import com.mobilityk.core.domain.GuideCommentSearchOption
import com.mobilityk.core.domain.QGuideComment.guideComment
import com.mobilityk.core.domain.QMember
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.api.guide.GuideCommentForAdminDTO
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.jpa.repository.JpaRepository

interface GuideCommentRepository: JpaRepository<GuideComment, Long>, GuideCommentRepositoryCustom {

    fun findAllByGuide(guide: Guide): List<GuideComment>
}

interface GuideCommentRepositoryCustom {
    fun findAllByGuideForAdmin(guideCommentSearchOption: GuideCommentSearchOption): List<GuideCommentForAdminDTO>
}

class GuideCommentRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
): GuideCommentRepositoryCustom {

    private val logger = KotlinLogging.logger {}
    override fun findAllByGuideForAdmin(guideCommentSearchOption: GuideCommentSearchOption): List<GuideCommentForAdminDTO> {
        return jpaQueryFactory.select(
            Projections.bean(
                GuideCommentForAdminDTO::class.java,
                guideComment.id,
                guideComment.commentType,
                guideComment.body,
                guideComment.writerId,
                guideComment.updatedAt,
                guideComment.createdAt,

                Projections.bean(MemberDTO::class.java,
                    QMember.member.id,
                    QMember.member.name,
                    QMember.member.point,
                    QMember.member.position,
                    QMember.member.groupName,
                    QMember.member.phone,
                    QMember.member.branch
                ).`as`("member")

            )
        )
            .from(guideComment)
            .where(search(searchOption = guideCommentSearchOption))
            .leftJoin(QMember.member).on(QMember.member.id.eq(guideComment.writerId))
            .fetch()
    }


    private fun search(searchOption: GuideCommentSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.guide?.let { guide ->
            builder.and(guideComment.guide.eq(guide))
        }
        searchOption.body?.let { body ->
            builder.and(guideComment.body.contains(body))
        }
        searchOption.commentType?.let { commentType ->
            builder.and(guideComment.commentType.eq(commentType))
        }
        searchOption.writerId?.let { memberId ->
            builder.and(guideComment.writerId.eq(memberId))
        }
        return builder
    }

}