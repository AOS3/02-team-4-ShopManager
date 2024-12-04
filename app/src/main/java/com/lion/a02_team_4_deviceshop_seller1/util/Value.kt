package com.lion.a02_team_4_deviceshop_seller1.util

import java.text.DecimalFormat


// 브랜드 타입을 나타내는 값
enum class DeviceType(var number:Int, var str:String){
    // 삼성
    DEVICE_TYPE_SAMSUNG(1, "SAMSUNG"),
    // 애플
    DEVICE_TYPE_APPLE(2, "APPLE"),
    // 화웨이
    DEVICE_TYPE_HUAWEI(3, "HUAWEI"),
}

// 브랜드 타입을 숫자로 받는 값
fun numberToDeviceType(deviceType: Int) = when(deviceType) {
    1 -> DeviceType.DEVICE_TYPE_SAMSUNG
    2 -> DeviceType.DEVICE_TYPE_APPLE
    else -> DeviceType.DEVICE_TYPE_HUAWEI
}

fun Int.formatToComma(): String {
    val formatter = DecimalFormat("#,###원")
    return formatter.format(this)
}