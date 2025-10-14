package com.fighting.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(val fcmToken: String)