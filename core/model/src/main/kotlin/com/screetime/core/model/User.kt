package com.screetime.core.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.CHILD,
    val familyId: String? = null,
    val deviceId: String? = null,
    val isDeviceOwner: Boolean = false,
    val fcmToken: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    PARENT,
    CHILD
}
