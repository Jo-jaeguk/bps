package com.mobilityk.core.service

import com.mobilityk.core.domain.Branch
import com.mobilityk.core.dto.BranchDTO
import com.mobilityk.core.dto.mapper.BranchMapper
import com.mobilityk.core.exception.CommException
import com.mobilityk.core.repository.BranchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
data class BranchService(
    private val branchRepository: BranchRepository,
    private val branchMapper: BranchMapper
) {

    fun findAll(): List<BranchDTO> {
        return branchRepository.findAll().map { branchMapper.toDto(it) }
    }

    @Transactional(rollbackFor = [CommException::class, RuntimeException::class])
    fun createBranch(branchDTO: BranchDTO): BranchDTO {
        return branchMapper.toDto(
            branchRepository.save(
                Branch(
                    branch = branchDTO.branch
                )
            )
        )
    }


}