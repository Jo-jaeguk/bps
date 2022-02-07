package com.mobilityk.core.util

import com.mobilityk.core.repository.MemberRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
data class FileService(
    private val memberRepository: MemberRepository
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun compressFiles(
        files: List<File>,
        compressFileName: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        if (request.getHeader("User-Agent").indexOf("MSIE 5.5") > -1) {
            response.setHeader("Content-Disposition", "filename=$compressFileName.zip;");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=$compressFileName.zip;");
        }
        response.setHeader("Content-Transfer-Encoding", "binary");

        //val bufferSize = 1024 * 2
        val os = response.outputStream
        val zos = ZipOutputStream(os)
        zos.setLevel(8)

        files.forEach { file ->
            var bis = BufferedInputStream(FileInputStream(file))
            val zentry = ZipEntry(file.name)
            zentry.time = file.lastModified()
            zos.putNextEntry(zentry)
            zos.write(bis.readAllBytes())
            bis.close()
        }
        zos.closeEntry()
        zos.close()
    }
}