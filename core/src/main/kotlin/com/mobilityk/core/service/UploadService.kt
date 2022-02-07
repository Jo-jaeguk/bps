package com.mobilityk.core.service

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest
import com.amazonaws.services.s3.model.PartETag
import com.amazonaws.services.s3.model.UploadPartRequest
import com.mobilityk.core.domain.Upload
import com.mobilityk.core.dto.UploadDTO
import com.mobilityk.core.dto.mapper.UploadMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.MemberRepository
import com.mobilityk.core.repository.UploadRepository
import com.mobilityk.core.util.CommonUtil
import com.mobilityk.core.util.NcpObjectService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@Service
data class UploadService(
    private val uploadRepository: UploadRepository,
    private val uploadMapper: UploadMapper,
    private val memberRepository: MemberRepository,
    private val resourceLoader: ResourceLoader,
    private val ncpObjectService: NcpObjectService,
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
) {

    private val logger = KotlinLogging.logger {}

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createUpload(memberId: Long, imgFile: MultipartFile?, httpServletRequest: HttpServletRequest): UploadDTO {

        val member = memberRepository.findById(memberId).orElseThrow { CommException("존재하지 않는 ID 입니다.") }

        //val realFolder = httpServletRequest.session.servletContext.getRealPath("image")

        val now = LocalDateTime.now()
        val todayDate: String = CommonUtil.getTodayDate(now)
        val filePath = todayDate + "/" + member.emailAddress + "/"
        val fileName: String = member.emailAddress + "_" + now.second + "_" + now.nano + ".png"


        val localFilePath = ncpObjectService.fileUpload(imgFile!!, filePath, fileName)
        if(localFilePath == "error") {
            throw CommException("fail save iamge")
        } else {
            val objectName = "image/$filePath$fileName"
            val result = ncpObjectService.putObjectAcl(objectName, localFilePath)
            logger.info { "localFilePath : $localFilePath" }
            logger.info { "result : $result" }
            val file = File(localFilePath)
            file.delete()
            if(result == HttpStatus.OK.value()) {
                return uploadMapper.toDto(
                    uploadRepository.save(
                        Upload(
                            url = "$url/$objectName",
                            path = filePath,
                            fileName = fileName,
                            writerId = memberId
                        )
                    )
                )
            } else {
                throw CommException("Object Storage 업로드 실패")
            }
        }
    }

    private fun uploadS3(imgFile: MultipartFile?,  objectName: String): Boolean {
        var uploadSuccess = true
        /*
        val endPoint = endPoint
        val regionName = regionName
        val accessKey = "ACCESS_KEY"
        val secretKey = "SECRET_KEY"
         */
        // S3 client


        logger.info { "bucketName $bucketName" }
        logger.info { "endPoint $endPoint" }
        logger.info { "regionName $regionName" }
        logger.info { "accessKey $accessKey" }
        logger.info { "secretKey $secretKey" }
        logger.info { "objectName $objectName" }

        logger.info { "originFileName ${imgFile?.originalFilename}" }
        // S3 client
        val s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(EndpointConfiguration(endPoint, regionName))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .build()

        //val bucketName = "sample-bucket"
        //val objectName = "sample-large-object"

        val file = File(imgFile?.originalFilename)
        val contentLength = file.length()
        var partSize = (10 * 1024 * 1024).toLong()

        try {
            // initialize and get upload ID
            val initiateMultipartUploadResult =
                s3.initiateMultipartUpload(InitiateMultipartUploadRequest(bucketName, objectName))
            val uploadId = initiateMultipartUploadResult.uploadId

            // upload parts
            val partETagList: MutableList<PartETag> = ArrayList()
            var fileOffset: Long = 0
            var i = 1
            while (fileOffset < contentLength) {

                partSize = Math.min(partSize, contentLength - fileOffset)
                val uploadPartRequest = UploadPartRequest()
                    .withBucketName(bucketName)
                    .withKey(objectName)
                    .withUploadId(uploadId)
                    .withPartNumber(i)
                    .withFile(file)
                    .withFileOffset(fileOffset)
                    .withPartSize(partSize)

                logger.info { "uploadId $uploadId" }
                logger.info { "file ${file}" }

                val uploadPartResult = s3.uploadPart(uploadPartRequest)
                partETagList.add(uploadPartResult.partETag)
                fileOffset += partSize
                i++
            }

            // abort
            // s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectName, uploadId));

            // complete
            s3.completeMultipartUpload(
                CompleteMultipartUploadRequest(
                    bucketName,
                    objectName,
                    uploadId,
                    partETagList
                )
            )
        } catch (e: AmazonS3Exception) {
            e.printStackTrace()
            logger.info { "AmazonS3Exception ${e.localizedMessage}" }
            uploadSuccess = false
        } catch (e: SdkClientException) {
            e.printStackTrace()
            logger.info { "SdkClientException ${e.localizedMessage}" }
            uploadSuccess = false
        }
        return uploadSuccess
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createUpload(memberId: Long, imgFile: ByteArray, httpServletRequest: HttpServletRequest): UploadDTO {

        val member = memberRepository.findById(memberId).orElseThrow { CommException("존재하지 않는 ID 입니다.") }

        //val realFolder = httpServletRequest.session.servletContext.getRealPath("image")

        val now = LocalDateTime.now()
        val todayDate: String = CommonUtil.getTodayDate(now)
        val filePath = todayDate + "/" + member.emailAddress + "/"
        val fileName: String = member.emailAddress + "_" + now.second + "_" + now.nano + ".png"

        val localFilePath = ncpObjectService.fileUpload(imgFile, filePath, fileName)
        if(localFilePath == "error") {
            throw CommException("fail save iamge")
        } else {
            val objectName = "image/$filePath$fileName"
            val result = ncpObjectService.putObjectAcl(objectName, localFilePath)
            logger.info { "localFilePath : $localFilePath" }
            logger.info { "result : $result" }
            val file = File(localFilePath)
            file.delete()
            if(result == HttpStatus.OK.value()) {
                return uploadMapper.toDto(
                    uploadRepository.save(
                        Upload(
                            url = "$url/$objectName",
                            path = filePath,
                            fileName = fileName,
                            writerId = memberId
                        )
                    )
                )
            } else {
                throw CommException("Object Storage 업로드 실패")
            }
        }
    }

}