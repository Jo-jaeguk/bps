package com.mobilityk.core.service

import com.mobilityk.core.domain.MappingType
import com.mobilityk.core.domain.Notice
import com.mobilityk.core.domain.NoticeSearchOption
import com.mobilityk.core.domain.UploadMapping
import com.mobilityk.core.dto.NoticeDTO
import com.mobilityk.core.dto.mapper.NoticeMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.NoticeRepository
import com.mobilityk.core.repository.UploadMappingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val noticeMapper: NoticeMapper,
    private val uploadMappingRepository: UploadMappingRepository,
    private val kakaoTalkService: KakaoTalkService
) {

    @Transactional
    fun findAllBySearch(searchOption: NoticeSearchOption, pageable: Pageable): Page<NoticeDTO>{
        return noticeRepository.findAllBySearch(searchOption, pageable)
            .map {
                noticeMapper.toDto(it)
            }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createNotice(noticeDTO: NoticeDTO, adminId: Long): NoticeDTO{
        val stored = noticeRepository.save(
            Notice(
                title = noticeDTO.title,
                body = noticeDTO.body,
                writerId = adminId
            )
        )
        noticeDTO.attatchMents?.let { attachMents ->
            attachMents.forEach { attach ->
                uploadMappingRepository.save(
                    UploadMapping(
                        uploadId = attach.uploadId,
                        mappingId = stored.id,
                        mappingType = MappingType.NOTICE
                    )
                )
            }
        }
        return noticeMapper.toDto(stored)
    }

    @Transactional
    fun deleteNotice(id: Long): Unit {
        return noticeRepository.deleteById(id)
    }

    @Transactional
    fun findById(id: Long): NoticeDTO{

        val notice = noticeRepository.findById(id).orElseThrow { CommException("not found notice") }
        val noticeDTO = noticeMapper.toDto(notice)
        val uploadMappings = uploadMappingRepository.findAllByMappingIdAndMappingType(notice.id!!, MappingType.NOTICE)
        if(!uploadMappings.isNullOrEmpty()) {

        }
        return noticeDTO
    }




}