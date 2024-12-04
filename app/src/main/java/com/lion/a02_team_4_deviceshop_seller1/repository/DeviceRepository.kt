package com.lion.a02_team_4_deviceshop_seller1.repository

import android.content.Context
import com.lion.a02_team_4_deviceshop_seller1.dao.DeviceDatabase
import com.lion.a02_team_4_deviceshop_seller1.util.DeviceType
import com.lion.a02_team_4_deviceshop_seller1.util.numberToDeviceType
import com.lion.a02_team_4_deviceshop_seller1.viewmodel.DeviceViewModel
import com.lion.a02_team_4_deviceshop_seller1.vo.DeviceVO


class DeviceRepository {

    companion object {

        // 제품 정보를 저장하는 메서드
        fun insertDeviceInfo(context: Context, deviceViewModel: DeviceViewModel) {
            // Database 객체를 가져온다.
            val deviceDatabase = DeviceDatabase.getInstance(context)
            // ViewModel의 데이터를 VO에 담아준다.
            val deviceType = deviceViewModel.deviceType.number
            val deviceName = deviceViewModel.deviceName
            val devicePrice = deviceViewModel.devicePrice
            val deviceImage = deviceViewModel.deviceImage

            val deviceVO = DeviceVO(deviceType = deviceType, deviceName = deviceName, devicePrice = devicePrice, deviceImage = deviceImage)

            deviceDatabase?.deviceDAO()?.insertDeviceData(deviceVO)
        }

        // 제품 정보 전체를 가져오는 메서드
        fun selectDeviceInfoAll(context: Context): MutableList<DeviceViewModel> {
            // 데이터 베이스 객체 생성
            val deviceDatabase = DeviceDatabase.getInstance(context)
            // 제품 데이터 전체를 가져온다
            val deviceVoList = deviceDatabase?.deviceDAO()?.selectDeviceDataAll()
            // 제품 데이터 전체를 담을 리스트 생성
            val deviceViewModelList = mutableListOf<DeviceViewModel>()
            // 제품 수 만큼 반복
            deviceVoList?.forEach {
                // 제품 데이터를 추출
                val deviceType = when(it.deviceType) {
                    DeviceType.DEVICE_TYPE_SAMSUNG.number -> DeviceType.DEVICE_TYPE_SAMSUNG
                    DeviceType.DEVICE_TYPE_APPLE.number -> DeviceType.DEVICE_TYPE_APPLE
                    else -> DeviceType.DEVICE_TYPE_HUAWEI
                }
                val deviceName = it.deviceName
                val devicePrice = it.devicePrice
                val deviceImage = it.deviceImage
                val deviceIdx = it.deviceIdx
                // 객체에 담는다.
                val deviceViewModel = deviceImage?.let { it1 ->
                    DeviceViewModel(deviceIdx, deviceType, deviceName, devicePrice,
                        it1
                    )
                }
                // 리스트에 담는다.
                if (deviceViewModel != null) {
                    deviceViewModelList.add(deviceViewModel)
                }
            }
            return deviceViewModelList
        }

        // 제품 하나의 정보를 가져오는 메서드
        fun selectDeviceInfoByDeviceIdx(context: Context, deviceIdx:Int) : DeviceViewModel {
            val deviceDatabase = DeviceDatabase.getInstance(context)
            // 제품 하나의 정보를 가져온다.
            val deviceVO = deviceDatabase?.deviceDAO()?.selectDeviceDataByDeviceIdx(deviceIdx)
            // 객체에 담는다.
            val deviceType = when(deviceVO?.deviceType) {
                DeviceType.DEVICE_TYPE_SAMSUNG.number -> DeviceType.DEVICE_TYPE_SAMSUNG
                DeviceType.DEVICE_TYPE_APPLE.number -> DeviceType.DEVICE_TYPE_APPLE
                else -> DeviceType.DEVICE_TYPE_HUAWEI
            }
            val deviceName = deviceVO?.deviceName
            val devicePrice = deviceVO?.devicePrice
            val deviceImage = deviceVO?.deviceImage

            val deviceViewModel = DeviceViewModel(deviceIdx, deviceType, deviceName!!, devicePrice!!, deviceImage!!)

            return deviceViewModel
        }

        // 제품 이름으로 검색하여 제품 데이터 전체를 가져오는 메서드
        fun selectDeviceDataAllByDeviceName(context: Context, deviceName:String): MutableList<DeviceViewModel> {
            // 데이터를 가져온다.
            val deviceDatabase = DeviceDatabase.getInstance(context)
            val deviceList = deviceDatabase?.deviceDAO()?.selectDeviceDataAllByDeviceName(deviceName)

            // 제품 데이터를 담을 리스트
            val tempList = mutableListOf<DeviceViewModel>()

            // 제품 수만큼 반복
            deviceList?.forEach {
                val deviceModel = DeviceViewModel(
                    it.deviceIdx, numberToDeviceType(it.deviceType), it.deviceName, it.devicePrice, it.deviceImage!!
                )
                // 리스트에 담는다.
                tempList.add(deviceModel)
            }
            return tempList
        }

        // 제품 정보를 삭제하는 메서드
        fun deleteDeviceInfoByDeviceIdx(context: Context, deviceIdx: Int) {
            val deviceDatabase = DeviceDatabase.getInstance(context)
            // 삭제한 제품 번호를 담고 있는 객체를 생성
            val deviceVO = DeviceVO(deviceIdx = deviceIdx)
            // 삭제
            deviceDatabase?.deviceDAO()?.deleteDeviceData(deviceVO)
        }

        // 제품 정보를 수정하는 메서드
        fun updateDeviceInfo(context: Context, deviceViewModel: DeviceViewModel) {
            val deviceDatabase = DeviceDatabase.getInstance(context)
            // VO 객체에 담는다.
            val deviceIdx = deviceViewModel.deviceIdx
            val deviceType = deviceViewModel.deviceType.number
            val deviceName = deviceViewModel.deviceName
            val devicePrice = deviceViewModel.devicePrice
            val deviceImage = deviceViewModel.deviceImage

            val deviceVO = DeviceVO(deviceIdx, deviceType, deviceName, devicePrice, deviceImage)
            // 수정
            deviceDatabase?.deviceDAO()?.updateDeviceData(deviceVO)
        }

    }
}