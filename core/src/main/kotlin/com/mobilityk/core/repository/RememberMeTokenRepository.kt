package com.mobilityk.core.repository

import com.mobilityk.core.domain.RememberMe
import mu.KotlinLogging
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Repository
class RememberMeTokenRepository(
    private val rememberMeRepository: RememberMeRepository
): PersistentTokenRepository {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun createNewToken(token: PersistentRememberMeToken?) {
        logger.info { "remember-me token 생성" }
        rememberMeRepository.save(
            RememberMe(
                series = token?.series,
                token = token?.tokenValue,
                username = token?.username,
                lastUsed = token?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            )
        )
    }

    @Transactional
    override fun updateToken(series: String?, tokenValue: String?, lastUsed: Date?) {
        logger.info { "remember-me token 업데이트 [$series] [$tokenValue]" }
        val rememberMeOpt = rememberMeRepository.findBySeries(series!!)
        if(rememberMeOpt.isPresent) {
            val rememberMe = rememberMeOpt.get()
            rememberMe.lastUsed = LocalDateTime.now()
            rememberMe.token = tokenValue
        }
    }

    @Transactional
    override fun getTokenForSeries(seriesId: String?): PersistentRememberMeToken? {
        logger.info { "remember-me token 획득" }
        val rememberMeOpt = rememberMeRepository.findBySeries(seriesId!!)
        return if(rememberMeOpt.isPresent) {
            logger.info { "remember-me token 획득 tokne ${rememberMeOpt.get().token}" }
            PersistentRememberMeToken(
                rememberMeOpt.get().username,
                rememberMeOpt.get().series,
                rememberMeOpt.get().token,
                Date.from(rememberMeOpt.get().lastUsed?.atZone(ZoneId.systemDefault())?.toInstant())
            )
        } else null
    }

    @Transactional
    override fun removeUserTokens(username: String?) {
        logger.info { "remember-me token 삭제 $username" }
        rememberMeRepository.deleteByUsername(username!!)
    }



}