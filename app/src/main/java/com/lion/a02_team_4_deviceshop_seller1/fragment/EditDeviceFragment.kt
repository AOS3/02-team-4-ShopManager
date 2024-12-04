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
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentEditDeviceBinding
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

class EditDeviceFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentEditDeviceBinding: FragmentEditDeviceBinding

    lateinit var mainActivity: MainActivity

    lateinit var fileAppLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEditDeviceBinding = FragmentEditDeviceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 툴바 구성 메서드 호출
        settingToolbar()
        // 입력 초기 설정 메서드 호출
        settingInput()
        // 이미지 변경 버튼 메서드 호출
        settingButtonChangeImage()
        // 파일 런처
        createFileLauncher()

        return fragmentEditDeviceBinding.root
    }

    // Toolbar를 설정하는 메서드
    fun settingToolbar(){
        fragmentEditDeviceBinding.apply {
            // 타이틀
            toolbarEditFragment.title = "상품 정보 수정"
            // 네비게이션 아이콘
            toolbarEditFragment.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarEditFragment.setNavigationOnClickListener {
                // 이전 화면으로 돌아간다.
                mainFragment.removeFragment(SubFragmentName.EDIT_DEVICE_FRAGMENT)
            }

            // 메뉴
            toolbarEditFragment.inflateMenu(R.menu.toolbar_device_edit_menu)
            toolbarEditFragment.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.edit_toolbar_menu_done -> {
                        // 수정 완료 메서드 호출
                        modifyDone()
                    }
                }
                true
            }
        }
    }

    // 입력 요소 초기 설정 메서드
    fun settingInput() {
        fragmentEditDeviceBinding.apply {
            // 제품 번호를 가져온다.
            val deviceIdx = arguments?.getInt("deviceIdx")!!
            // 제품 데이터를 가져온다.
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    DeviceRepository.selectDeviceInfoByDeviceIdx(mainActivity, deviceIdx)
                }
                val deviceViewModel = work1.await()

                when(deviceViewModel.deviceType) {
                    DeviceType.DEVICE_TYPE_SAMSUNG -> {
                        toggleGroupTypeEditFragment.check(R.id.buttonTypeSamsungEditFragment)
                    }
                    DeviceType.DEVICE_TYPE_APPLE -> {
                        toggleGroupTypeEditFragment.check(R.id.buttonTypeAppleEditFragment)
                    }
                    DeviceType.DEVICE_TYPE_HUAWEI -> {
                        toggleGroupTypeEditFragment.check(R.id.buttonTypeHuaweiEditFragment)
                    }
                }
                textFieldProductNameEditFragment.editText?.setText(deviceViewModel.deviceName)
                textFieldProductPriceEditFragment.editText?.setText(deviceViewModel.devicePrice.toString())
                imageViewAddEditFragment.setImageBitmap(deviceViewModel.deviceImage)
            }
        }
    }

    // 수정 처리 메서드
    fun modifyDone() {
        val deviceIdx = arguments?.getInt("deviceIdx")!!
        val deviceType = when(fragmentEditDeviceBinding.toggleGroupTypeEditFragment.checkedButtonId) {
            R.id.buttonTypeSamsungEditFragment -> DeviceType.DEVICE_TYPE_SAMSUNG
            R.id.buttonTypeAppleEditFragment -> DeviceType.DEVICE_TYPE_APPLE
            else -> DeviceType.DEVICE_TYPE_HUAWEI
        }
        val deviceName = fragmentEditDeviceBinding.textFieldProductNameEditFragment.editText?.text.toString()
        val devicePrice = fragmentEditDeviceBinding.textFieldProductPriceEditFragment.editText?.text.toString()
        // 가격을 정수값으로 변환
        val devicePriceInt = try {
            devicePrice.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(mainActivity, "가격은 2,147,483,647 이하의 숫자여야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val bitmapImage = (fragmentEditDeviceBinding.imageViewAddEditFragment.drawable as BitmapDrawable).bitmap
        // 이미지 파일 이름
        val imageFileName = "device_image_${System.currentTimeMillis()}.png"
        // 이미지 경로, 파일 반환
        val deviceImage = saveImageToStorage(bitmapImage, imageFileName)
        val deviceViewModel = DeviceViewModel(deviceIdx, deviceType, deviceName, devicePriceInt, deviceImage)

        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                DeviceRepository.updateDeviceInfo(mainActivity, deviceViewModel)
            }
            work1.join()
            mainFragment.removeFragment(SubFragmentName.EDIT_DEVICE_FRAGMENT)
        }
    }

    // 이미지 변경 버튼 메서드
    fun settingButtonChangeImage() {
        fragmentEditDeviceBinding.apply {
            buttonChangeImageEditFragment.setOnClickListener {
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

                fragmentEditDeviceBinding.imageViewAddEditFragment.setImageBitmap(bitmap)

                fileInputStream.close()
            }
        }
    }

    // 이미지를 내부 저장소에 저장하고 Bitmap을 반환하는 메서드
    fun saveImageToStorage(bitmap: Bitmap, fileName:String): Bitmap {
        fragmentEditDeviceBinding.apply {
            val fileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            val dataOutputStream = DataOutputStream(fileOutputStream)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, dataOutputStream)

            dataOutputStream.flush()
            dataOutputStream.close()

            return bitmap
        }
    }


}