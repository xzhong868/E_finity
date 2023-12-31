package com.example.e_finity

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.color.utilities.Score
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uniqueID: String,
    val full_name: String,
    val phone_num: String,
    val role: String,
    var avatar: Boolean,
    val group: String,
    val score: Int
)

@Serializable
data class UserRead(
    val id: Int,
    val uniqueID: String,
    val full_name: String,
    val phone_num: String,
    val role: String,
    var avatar: Boolean,
    val group: String,
    val score: Int
)

@Serializable
data class GroupRead(
    val id: Int,
    val name: String,
    val color: String,
    val timemodi: Int
)

@Serializable
data class Stats(
    val uniqueID: String,
    val Attack: Int,
    val HP: Int,
    val Defence: Int,
    val Accuracy: Int
)

@Serializable
data class GroupAdd(
    val name: String,
    val color: String
)

@Serializable
data class uRead(
    val uniqueID: List<userStats>,
    val full_name: String,
    val phone_num: String,
    val role: String,
    val score: Int,
    val group: orient
)

@Serializable
data class orient(
    val name: String,
    val color: String
)

@Serializable
data class userStats(
    val Attack: Int,
    val HP: Int,
    val Defence: Int,
    val Accuracy: Int
)

@Serializable
data class treasureClass(
    val id: Int,
    val UID: String,
    val points: Int,
    val content: String,
    val completed: String
)

@Serializable
data class treasureClassAdd(
    val UID: String,
    val points: Int,
    val content: String,
    val completed: String
)

@Serializable
data class treasureClassComplete(
    val UID: String,
    val points: Int,
    val completed: String
)

@Serializable
data class userScore(
    val uniqueID: String,
    val score: Int
)

@Serializable
data class scatterClass(
    val id: Int,
    val UID: String,
    val points: Int,
    val completed: String
)

@Serializable
data class scatterClassAdd(
    val UID: String,
    val points: Int
)

@Serializable
data class scatterClassScan(
    val UID: String,
    val points: Int,
    val completed: String
)

@Serializable
data class uReadShort(
    val full_name: String,
    val group: orient
)

@Serializable
data class memberEdit(
    val uniqueID: String,
    val full_name: String,
    val phone_num: String
)

@Serializable
data class bossesClass(
    val id: Int,
    val bossName: String,
    val bossPower: Int,
    val bossDesc: String,
    val lat: Double,
    val log: Double,
    val defeated: String
)

@Serializable
data class bossesAdd(
    val bossName: String,
    val bossPower: Int,
    val bossDesc: String,
    val lat: Double,
    val log: Double
)

@Serializable
data class userStatsRead(
    val id: Int,
    val uniqueID: String,
    val Attack: Int,
    val HP: Int,
    val Defence: Int,
    val Accuracy: Int
)

@Serializable
data class leaderBoard(
    val uniqueID: String,
    val full_name: String,
    val role: String,
    val group: leaderScore,
    val score: Int
)

@Serializable
data class leaderScore(
    val name: String,
    val color: String,
    val timemodi: Int
)
