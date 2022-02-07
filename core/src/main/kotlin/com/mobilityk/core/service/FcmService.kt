package com.mobilityk.core.service

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidNotification
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
data class FcmService(
    private val memberService: MemberService,
    @Value("\${fcm.base-url}")
    private val baseUrl: String,
    @Value("\${fcm.key}")
    private val key: String,
) {

    private val logger = KotlinLogging.logger {}

    fun sendPush (
        title: String,
        body: String,
        tokens: MutableList<String>
    ) {
        val message = MulticastMessage.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()
            )
            .setAndroidConfig(AndroidConfig.builder()
                .setTtl(3600 * 1000)
                .setNotification(AndroidNotification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setClickAction("")
                    .build()
                )
                .setPriority(AndroidConfig.Priority.HIGH)
                .build()
            )
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .build()
                )
                .putHeader("apns-priority", "5")
                .build()
            )
            .addAllTokens(tokens)
            .build()

        val response = FirebaseMessaging.getInstance().sendMulticast(message)
        logger.info { "FCM PUSH" }
        logger.info { "FCM PUSH SEND $message" }
        logger.info { "FCM PUSH Response count ${response.successCount}" }
        logger.info { "FCM PUSH Response $response" }
    }



}