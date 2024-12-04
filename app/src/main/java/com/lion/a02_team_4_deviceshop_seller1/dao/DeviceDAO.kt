package com.lion.a02_team_4_deviceshop_seller1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lion.a02_team_4_deviceshop_seller1.vo.DeviceVO

@Dao
interface DeviceDAO {

    // 제품 정보 저장
    @Insert
    fun insertDeviceData(deviceVO: DeviceVO)

    // 제품 정보를 가져오기
    @Query("""
        select * from DeviceTable
        order by deviceIdx desc
    """)
    fun selectDeviceDataAll() : List<DeviceVO>

    // 제품 하나의 정보를 가져오는 메서드
    @Query("""
        select * from DeviceTable
        where deviceIdx = :deviceIdx
    """)
    fun selectDeviceDataByDeviceIdx(deviceIdx:Int) :DeviceVO

    // deviceName 컬럼의 값이 지정된 값과 같은 행만 가져오는 메서드
    @Query("""
        select * from DeviceTable
        where deviceName = :deviceName
        order by deviceIdx desc
    """)
    fun selectDeviceDataAllByDeviceName(deviceName: String) : List<DeviceVO>

    // 제품 하나의 정보를 삭제하는 메서드
    @Delete
    fun deleteDeviceData(deviceVO: DeviceVO)

    // 제품 정보를 수정하는 메서드
    @Update
    fun updateDeviceData(deviceVO:DeviceVO)
}