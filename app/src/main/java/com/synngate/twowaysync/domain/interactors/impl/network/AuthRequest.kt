package com.synngate.twowaysync.domain.interactors.impl.network

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("deviceId") val deviceId: String //  deviceId будет сериализоваться в JSON как "deviceId"
)