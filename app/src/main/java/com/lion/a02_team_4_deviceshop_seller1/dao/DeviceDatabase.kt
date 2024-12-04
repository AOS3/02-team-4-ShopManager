package com.lion.a02_team_4_deviceshop_seller1.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lion.a02_team_4_deviceshop_seller1.vo.DeviceVO

@Database(entities = [DeviceVO::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class DeviceDatabase : RoomDatabase() {
    abstract fun deviceDAO() : DeviceDAO

    companion object {
        var deviceDatabase:DeviceDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : DeviceDatabase? {
            synchronized(DeviceDatabase::class) {
                deviceDatabase = Room.databaseBuilder(
                    context.applicationContext, DeviceDatabase::class.java,
                    "Device.db"
                ).build()
            }
            return deviceDatabase
        }

        fun destroyInstance() {
            deviceDatabase = null
        }
    }
}