package pt.ipt.api.model

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("foto") val foto: String?,
    @SerializedName("artista") val artista: String?,
    @SerializedName("musicas") val musicas: List<Music>// URL or path to the image
)
