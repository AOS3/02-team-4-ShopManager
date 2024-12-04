package com.lion.a02_team_4_deviceshop_seller1.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lion.a02_team_4_deviceshop_seller1.MainActivity
import com.lion.a02_team_4_deviceshop_seller1.R
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentInputDeviceBinding
import com.lion.a02_team_4_deviceshop_seller1.repository.DeviceRepository
import com.lion.a02_team_4_deviceshop_seller1.util.DeviceType
import com.lion.a02_team_4_deviceshop_seller1.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream

class InputDeviceFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentInputDeviceBinding: FragmentInputDeviceBinding
    lateinit var mainActivity: MainActivity

    lateinit var fileAppLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputDeviceBinding = FragmentInputDeviceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 툴바 구성 메서드 호출
        settingToolbar()
        // 이미지 추가 버튼 메서드 호출
        settingButtonAddImage()
        // 파일 런처
        createFileLauncher()

        return fragmentInputDeviceBinding.root
    }

    // Toolbar를 설정하는 메서드
    fun settingToolbar(){
        fragmentInputDeviceBinding.apply {
            // 타이틀
            toolbarInputFragment.title = "상품 정보 입력"
            // 네비게이션 아이콘
            toolbarInputFragment.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarInputFragment.setNavigationOnClickListener {
                // 이전 화면으로 돌아간다.
                mainFragment.removeFragment(SubFragmentName.INPUT_DEVICE_FRAGMENT)
            }

            // 메뉴
            toolbarInputFragment.inflateMenu(R.menu.toolbar_device_input_menu)
            toolbarInputFragment.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.input_toolbar_menu_done -> {
                        inputDone()
                    }
                }
                true
            }
        }
    }

    // 입력 완료 처리 메서드
    fun inputDone() {
        fragmentInputDeviceBinding.apply {
            // 사용자가 입력한 데이터를 가져온다.

            // 디바이스 브랜드(타입)
            val deviceType = when(toggleGroupTypeInputFragment.checkedButtonId) {
                // 삼성
                R.id.buttonTypeSamsungInputFragment -> DeviceType.DEVICE_TYPE_SAMSUNG
                // 애플
                R.id.buttonTypeAppleInputFragment -> DeviceType.DEVICE_TYPE_APPLE
                // 화웨이
                else -> DeviceType.DEVICE_TYPE_HUAWEI
            }

            // 제품명
            val deviceName = textFieldProductNameInputFragment.editText?.text.toString()
            // 가격
            val devicePrice = textFieldProductPriceInputFragment.editText?.text.toString()
            // 가격을 정수값으로 변환
            val devicePriceInt = try {
                devicePrice.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(mainActivity, "가격은 2,147,483,647 이하의 숫자여야 합니다.", Toast.LENGTH_SHORT).show()
                return
            }
            val bitmapImage = (imageViewAddInputFragment.drawable as BitmapDrawable).bitmap
            // 이미지 파일 이름
            val imageFileName = "device_image_${System.currentTimeMillis()}.png"
            // 이미지 경로, 파일 반환
            val deviceImage = saveImageToStorage(bitmapImage, imageFileName)

            // 객체에 담는다.
            val deviceModel = DeviceViewModel(0, deviceType, deviceName, devicePriceInt, deviceImage)

            // 데이터를 저장한다.
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    // 저장
                    DeviceRepository.insertDeviceInfo(mainActivity, deviceModel)
                }
                work1.await()
                // 이전 화면으로 돌아간다.
                mainFragment.removeFragment(SubFragmentName.INPUT_DEVICE_FRAGMENT)

            }
        }
    }

    // 이미지 추가 버튼 메서드
    fun settingButtonAddImage() {
        fragmentInputDeviceBinding.apply {
            buttonAddImageInputFragment.setOnClickListener {
                // Intent 생성
                val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                // 이미지 파일만 선택
                fileIntent.type = "image/*"
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE)

                fileAppLauncher.launch(fileIntent)
            }
        }
    }

    // 파일 관리 앱의 Activity를 실행하기 위한 런처 객체 생성
    fun createFileLauncher() {
        fileAppLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // ResultCode 로 분기
            // 선택하고 돌아왔을 경우 : RESULT_OK
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                // 사용자가 선택한 파일에 접근할 수 있는 경로를 담은 객체를 추출

                val des1 = requireContext().contentResolver.openFileDescriptor(it.data?.data!!, "r")

                val fileInputStream = FileInputStream(des1?.fileDescriptor)
                val dataInputStream = DataInputStream(fileInputStream)

                val bitmap = BitmapFactory.decodeStream(dataInputStream)

                fragmentInputDeviceBinding.imageViewAddInputFragment.setImageBitmap(bitmap)

                fileInputStream.close()
            }
        }
    }

    // 이미지를 내부 저장소에 저장하고 Bitmap을 반환하는 메서드
    fun saveImageToStorage(bitmap: Bitmap, fileName:String): Bitmap {
        fragmentInputDeviceBinding.apply {
            val fileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            val dataOutputStream = DataOutputStream(fileOutputStream)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, dataOutputStream)

            dataOutputStream.flush()
            dataOutputStream.close()

            return bitmap
        }
    }
}