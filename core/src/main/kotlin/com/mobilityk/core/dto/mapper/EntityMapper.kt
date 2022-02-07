package com.mobilityk.core.dto.mapper

interface EntityMapper <D , E> {
    fun toEntity(dto: D) : E
    fun toDto(entity: E) : D
    fun toDtoList(list: List<E>): List<D>
}