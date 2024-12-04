package com.lion.a02_team_4_deviceshop_seller1.vo

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DeviceTable")
data class DeviceVO(
    @PrimaryKey(autoGenerate = true)
    var deviceIdx:Int = 0,
    var deviceType:Int = 0,
    var deviceName:String = "",
    var devicePrice:Int = 0,
    var deviceImage:Bitmap? = null
)