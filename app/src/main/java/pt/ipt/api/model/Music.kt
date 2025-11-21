package pt.ipt.api.model

import com.google.gson.annotations.SerializedName

data class Music(
    @SerializedName("id") val id: Int?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("artista") val artista: String?,
    @SerializedName("album") val album: Album
)