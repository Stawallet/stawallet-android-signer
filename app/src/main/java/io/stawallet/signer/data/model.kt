package io.stawallet.signer.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class User(
    val id: Int,
    @PrimaryKey val email: String,
    val type: String,
    val isEmailVerified: Boolean,
    val isEvidenceVerified: Boolean,
    val hasSecondFactor: Boolean,
    val isActive: Boolean,
    val createdAt: Date?,
    val modifiedAt: Date?,
    val tierName: String?,
    val referralCode: String?,
    @Embedded(prefix = "country_") val country: Country?
)

data class Country(val id: Int, val name: String, val phonePrefix: String, val code: String) {
    override fun toString(): String = name
}

data class TokenResponse(
    val token: String
)

