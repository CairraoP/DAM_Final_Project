package pt.ipt.spitifi.model

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String?,
    @SerializedName("foto") val foto: String?,
    @SerializedName("Albuns") val albuns: List<Album>, // Array of Albuns which belongs to this artist
)