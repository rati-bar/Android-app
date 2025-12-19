package com.screetime.core.model

data class Family(
    val id: String = "",
    val name: String = "",
    val parentId: String = "",
    val childIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
