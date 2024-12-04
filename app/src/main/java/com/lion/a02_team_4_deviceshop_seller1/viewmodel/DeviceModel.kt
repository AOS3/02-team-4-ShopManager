package com.lion.a02_team_4_deviceshop_seller1.viewmodel

import android.graphics.Bitmap
import com.lion.a02_team_4_deviceshop_seller1.util.DeviceType

// 제품 정보를 담을 ViewModel 클래스
data class DeviceViewModel(
    var deviceIdx:Int,
    // 브랜드 타입
    var deviceType:DeviceType,
    // 제품 이름
    var deviceName:String,
    // 제품 가격
    var devicePrice:Int,
    // 제품 이미지
    var deviceImage: Bitmap
)