package pt.ipt.spitifi.model

import com.google.gson.annotations.SerializedName

data class Music(
    @SerializedName("id") val id: Int?,
    @SerializedName("nome") val nome: String?,
    @SerializedName("album") val album: Int,
    @SerializedName("filePath") val filePath: String?,
    @SerializedName("donoFK") val donoFK: Int?
)