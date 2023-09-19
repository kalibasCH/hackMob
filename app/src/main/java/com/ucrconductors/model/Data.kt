package com.ucrconductors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int? = null,
    @ColumnInfo(name = "first_name") val id: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)

data class Conductor(
    val login: String? = null,
    val password: String? = null,
    val name: String? = null,
    val surname: String? = null
)

data class Transport(
    val id: String,
    val transport_num: String
)

data class SomeClass(val userInfo: String = "", val login: String = "", val password: String = "")
