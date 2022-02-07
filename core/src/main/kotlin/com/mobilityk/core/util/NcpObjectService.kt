package com.mobilityk.core.util

import mu.KotlinLogging
import org.apache.commons.codec.binary.Hex
import org.apache.http.Header
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.FileEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.TreeMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
data class NcpObjectService(
    @Value("\${s3.url}")
    private val url: String,
    @Value("\${s3.endpoint}")
    private val endPoint: String,
    @Value("\${s3.region-name}")
    private val regionName: String,
    @Value("\${s3.secret-key}")
    private val secretKey: String,
    @Value("\${s3.access-key}")
    private val accessKey: String,
    @Value("\${s3.bucket.name}")
    private val bucketName: String,
    private val resourceLoader: ResourceLoader
) {
    private val logger = KotlinLogging.logger {}

    private val CHARSET_NAME = "UTF-8"
    private val HMAC_ALGORITHM = "HmacSHA256"
    private val HASH_ALGORITHM = "SHA-256"
    private val AWS_ALGORITHM = "AWS4-HMAC-SHA256"

    private val SERVICE_NAME = "s3"
    private val REQUEST_TYPE = "aws4_request"

    private val UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD"

    private val DATE_FORMATTER = SimpleDateFormat("yyyyMMdd")
    private val TIME_FORMATTER = SimpleDateFormat("yyyyMMdd\'T\'HHmmss\'Z\'")


    fun putObjectAclMultiPart(objectName: String, imgFile: MultipartFile?): Int {
        return putObject(bucketName, "image/$objectName", imgFile!!, "public-read")
    }

    fun putObjectAcl(objectName: String, localFilePath: String): Int {
        return putObject(objectName, localFilePath, "public-read")
    }

    @Throws(Exception::class)
    fun putObject(bucketName: String, objectName: String, imgFile: MultipartFile, aclHeader: String?): Int {
        val httpClient: HttpClient = HttpClientBuilder.create().build()
        val request = HttpPut("$endPoint/$bucketName/$objectName")
        request.addHeader("Host", request.uri.host)
        if (aclHeader != null && aclHeader.isNotEmpty()) {
            request.addHeader("X-Amz-Acl", aclHeader)
        }
        val file = File(imgFile.originalFilename)
        imgFile.transferTo(file)
        request.entity = FileEntity(file)
        authorization(request, regionName, accessKey, secretKey)
        val response = httpClient.execute(request)
        logger.info { "Response : " + response.statusLine }
        return response.statusLine.statusCode
    }

    @Throws(java.lang.Exception::class)
    fun putObject(objectName: String, localFilePath: String?, aclHeader: String?): Int {
        val httpClient: HttpClient = HttpClientBuilder.create().build()
        val request = HttpPut("$endPoint/$bucketName/$objectName")
        request.addHeader("Host", request.uri.host)
        if (aclHeader != null && aclHeader.isNotEmpty()) {
            request.addHeader("X-Amz-Acl", aclHeader)
        }
        request.entity = FileEntity(File(localFilePath))
        authorization(request, regionName, accessKey, secretKey)
        val response = httpClient.execute(request)
        println("Response : " + response.statusLine)
        return response.statusLine.statusCode
    }

    fun fileUpload(imgFile: MultipartFile, filePath: String, fileName: String): String {
        var path: String
        var out: OutputStream? = null
        val printWriter: PrintWriter? = null
        try {
            var originFileName: String = imgFile.originalFilename.toString()
            originFileName.substring(originFileName.indexOf("."))
            //            fileName = fileName+originFileName;
            val bytes: ByteArray = imgFile.bytes
            path = resourceLoader.getResource("classpath:").uri.path + "/image/"
            /*
            if (path.startsWith("/D") || path.startsWith("/C")) {
                path = path.substring(1)
                path = windowPath
            } else if (path.startsWith("/Users")) {
                path = macPath
            } else {
                path = linuxPath
            }
             */
            path += filePath
            var file = File(path)

//          파일명이 중복으로 존재할 경우
            if (fileName != "") {
                file = if (file.exists()) {
                    File(path + fileName)
                } else {
                    val mkdirs = file.mkdirs()
                    if (mkdirs) {
                    }
                    File(path + fileName)
                }
            }
            logger.info { "filePath : $filePath $fileName" }
            out = FileOutputStream(file)
            out.write(bytes)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //fileName = "error"
            return "error"
        } finally {
            try {
                out?.close()
                printWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return path + fileName
    }

    fun fileUpload(bytes: ByteArray, filePath: String, fileName: String): String {
        var path: String
        var out: OutputStream? = null
        val printWriter: PrintWriter? = null
        try {
            //            fileName = fileName+originFileName;
            path = resourceLoader.getResource("classpath:").uri.path + "/image/"
            path += filePath
            var file = File(path)

//          파일명이 중복으로 존재할 경우
            if (fileName != "") {
                file = if (file.exists()) {
                    File(path + fileName)
                } else {
                    val mkdirs = file.mkdirs()
                    if (mkdirs) {
                    }
                    File(path + fileName)
                }
            }
            logger.info { "filePath : $filePath $fileName" }
            out = FileOutputStream(file)
            out.write(bytes)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //fileName = "error"
            return "error"
        } finally {
            try {
                out?.close()
                printWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return path + fileName
    }

    private fun authorization(request: HttpUriRequest, regionName: String, accessKey: String, secretKey: String) {

        val now = Date()
        DATE_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        TIME_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        val datestamp = DATE_FORMATTER.format(now)
        val timestamp = TIME_FORMATTER.format(now)
        request.addHeader("X-Amz-Date", timestamp)
        request.addHeader("X-Amz-Content-Sha256", UNSIGNED_PAYLOAD)
        val standardizedQueryParameters: String = getStandardizedQueryParameters(request.uri.query)
        val sortedHeaders: TreeMap<String, String> = getSortedHeaders(request.allHeaders)
        val signedHeaders: String = getSignedHeaders(sortedHeaders)
        val standardizedHeaders: String = getStandardizedHeaders(sortedHeaders)
        val canonicalRequest: String =
            getCanonicalRequest(request, standardizedQueryParameters, standardizedHeaders, signedHeaders)
        println("> canonicalRequest :")
        println(canonicalRequest)
        val scope: String = getScope(datestamp, regionName)
        val stringToSign: String = getStringToSign(timestamp, scope, canonicalRequest)
        println("> stringToSign :")
        println(stringToSign)
        val signature: String = getSignature(secretKey, datestamp, regionName, stringToSign)
        val authorization: String = getAuthorization(accessKey, scope, signedHeaders, signature)
        request.addHeader("Authorization", authorization)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getStandardizedQueryParameters(queryString: String?): String {
        val sortedQueryParameters = TreeMap<String, String?>()
        // sort by key name
        if (queryString != null && queryString.isNotEmpty()) {
            val queryStringTokens = queryString.split("&").toTypedArray()
            for (field in queryStringTokens) {
                val fieldTokens = field.split("=").toTypedArray()
                if (fieldTokens.isNotEmpty()) {
                    if (fieldTokens.size > 1) {
                        sortedQueryParameters[fieldTokens[0]] = fieldTokens[1]
                    } else {
                        sortedQueryParameters[fieldTokens[0]] = ""
                    }
                }
            }
        }
        val standardizedQueryParametersBuilder = StringBuilder()
        var count = 0
        for (key in sortedQueryParameters.keys) {
            if (count > 0) {
                standardizedQueryParametersBuilder.append("&")
            }
            standardizedQueryParametersBuilder.append(key).append("=")
            if (sortedQueryParameters[key] != null && !sortedQueryParameters[key]!!.isEmpty()) {
                standardizedQueryParametersBuilder.append(
                    URLEncoder.encode(
                        sortedQueryParameters[key],
                        "UTF-8"
                    )
                )
            }
            count++
        }
        return standardizedQueryParametersBuilder.toString()
    }

    private fun getSortedHeaders(headers: Array<Header>): TreeMap<String, String> {
        val sortedHeaders = TreeMap<String, String>()
        // sort by header name
        for (header in headers) {
            sortedHeaders[header.name] = header.value
        }
        return sortedHeaders
    }

    private fun getSignedHeaders(sortedHeaders: TreeMap<String, String>): String {
        val signedHeadersBuilder = StringBuilder()
        for (headerName in sortedHeaders.keys) {
            signedHeadersBuilder.append(headerName.lowercase()).append(";")
        }
        return signedHeadersBuilder.toString()
    }

    private fun getStandardizedHeaders(sortedHeaders: TreeMap<String, String>): String {
        val standardizedHeadersBuilder = StringBuilder()
        for (headerName in sortedHeaders.keys) {
            standardizedHeadersBuilder.append(headerName.lowercase()).append(":").append(sortedHeaders[headerName])
                .append("\n")
        }
        return standardizedHeadersBuilder.toString()
    }

    private  fun getCanonicalRequest(
        request: HttpUriRequest,
        standardizedQueryParameters: String,
        standardizedHeaders: String,
        signedHeaders: String
    ): String {
        val canonicalRequestBuilder = StringBuilder().append(request.method).append("\n")
            .append(request.uri.path).append("\n")
            .append(standardizedQueryParameters).append("\n")
            .append(standardizedHeaders).append("\n")
            .append(signedHeaders).append("\n")
            .append(UNSIGNED_PAYLOAD)
        return canonicalRequestBuilder.toString()
    }

    private  fun getScope(datestamp: String, regionName: String): String {
        val scopeBuilder = StringBuilder().append(datestamp).append("/")
            .append(regionName).append("/")
            .append(SERVICE_NAME).append("/")
            .append(REQUEST_TYPE)
        return scopeBuilder.toString()
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private  fun getStringToSign(timestamp: String, scope: String, canonicalRequest: String): String {
        val stringToSignBuilder: StringBuilder = StringBuilder(AWS_ALGORITHM)
            .append("\n")
            .append(timestamp).append("\n")
            .append(scope).append("\n")
            .append(hash(canonicalRequest))
        return stringToSignBuilder.toString()
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class, InvalidKeyException::class)
    private  fun getSignature(
        secretKey: String,
        datestamp: String,
        regionName: String,
        stringToSign: String
    ): String {
        val kSecret = "AWS4$secretKey".toByteArray(charset(CHARSET_NAME))
        val kDate: ByteArray = sign(datestamp, kSecret)
        val kRegion: ByteArray = sign(regionName, kDate)
        val kService: ByteArray = sign(SERVICE_NAME, kRegion)
        val signingKey: ByteArray = sign(REQUEST_TYPE, kService)
        return Hex.encodeHexString(sign(stringToSign, signingKey))
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class, InvalidKeyException::class)
    private  fun sign(stringData: String, key: ByteArray): ByteArray {
        val data = stringData.toByteArray(charset(CHARSET_NAME))
        val e = Mac.getInstance(HMAC_ALGORITHM)
        e.init(SecretKeySpec(key, HMAC_ALGORITHM))
        return e.doFinal(data)
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private fun hash(text: String): String {
        val e = MessageDigest.getInstance(HASH_ALGORITHM)
        e.update(text.toByteArray(charset(CHARSET_NAME)))
        return Hex.encodeHexString(e.digest())
    }

    private fun getAuthorization(
        accessKey: String,
        scope: String,
        signedHeaders: String,
        signature: String
    ): String {
        val signingCredentials = "$accessKey/$scope"
        val credential = "Credential=$signingCredentials"
        val signerHeaders = "SignedHeaders=$signedHeaders"
        val signatureHeader = "Signature=$signature"
        val authHeaderBuilder = StringBuilder().append(AWS_ALGORITHM).append(" ")
            .append(credential).append(", ")
            .append(signerHeaders).append(", ")
            .append(signatureHeader)
        return authHeaderBuilder.toString()
    }
}