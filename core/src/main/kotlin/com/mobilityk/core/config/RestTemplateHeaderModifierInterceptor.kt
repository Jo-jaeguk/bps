package com.mobilityk.core.config

import com.google.gson.GsonBuilder
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RestTemplateHeaderModifierInterceptor : ClientHttpRequestInterceptor {

    private val logger = KotlinLogging.logger {}

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {

        var response : ClientHttpResponse = execution.execute(request , body)

        logger.info { " ** Method = ${request.methodValue} ** " }
        logger.info { " ** URL = ${request.uri.toString()}" }

        if(request.method != HttpMethod.GET ) {

            logger.info { " ** Body ** " }

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonOutput = gson.toJson(String(body))
            /*
            val gson: Gson = GsonBuilder().setPrettyPrinting().create()
            val jp = JsonParser()
            val je: JsonElement = jp.parse(String(body))
            val prettyJsonString: String = gson.toJson(je)
             */
            logger.info { "${jsonOutput.toString()}" }
        } else {

        }

        return response;
    }
}