package com.lion.a02_team_4_deviceshop_seller1.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.a02_team_4_deviceshop_seller1.MainActivity
import com.lion.a02_team_4_deviceshop_seller1.R
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentShowDeviceBinding
import com.lion.a02_team_4_deviceshop_seller1.repository.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ShowDeviceFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentShowDeviceBinding: FragmentShowDeviceBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShowDeviceBinding = FragmentShowDeviceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 툴바 구성 메서드 호출
        settingToolbar()
        // 데이터를 출력하는 메서드 호출
        settingTextView()

        return fragmentShowDeviceBinding.root
    }

    // Toolbar를 설정하는 메서드
    fun settingToolbar(){
        fragmentShowDeviceBinding.apply {
            // 타이틀
            toolbarShowFragment.title = "상품 정보 보기"
            // 네비게이션 아이콘
            toolbarShowFragment.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShowFragment.setNavigationOnClickListener {
                // 이전 화면으로 돌아간다.
                mainFragment.removeFragment(SubFragmentName.SHOW_DEVICE_FRAGMENT)
            }

            // 메뉴
            toolbarShowFragment.inflateMenu(R.menu.toolbar_device_show_menu)
            toolbarShowFragment.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.show_toolbar_menu_delete -> {
                        // 삭제 Dialog 표시 메서드 호출
                        deleteDeviceInfo()
                    }
                    R.id.show_toolbar_menu_edit -> {
                        // 제품 번호를 추출하여 전달
                        val deviceIdx = arguments?.getInt("deviceIdx")
                        val dataBundle = Bundle()
                        dataBundle.putInt("deviceIdx", deviceIdx!!)
                        // EditFragment 이동
                        mainFragment.replaceFragment(SubFragmentName.EDIT_DEVICE_FRAGMENT, true,  true, dataBundle)
                    }
                }
                true
            }
        }
    }

    // ImageView & TextView에 값을 설정하는 메서드
    fun settingTextView() {
        // 만약을 위한 초기화
        fragmentShowDeviceBinding.imageViewProductImageShowFragment.setImageBitmap(null)
        fragmentShowDeviceBinding.textViewTypeShowFragment.text = ""
        fragmentShowDeviceBinding.textViewNameShowFragment.text = ""
        fragmentShowDeviceBinding.textViewPriceShowFragment.text = ""
        // 제품 번호 추출
        val deviceIdx = arguments?.getInt("deviceIdx")
        // 제품 데이터를 가져와 출력
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                DeviceRepository.selectDeviceInfoByDeviceIdx(mainActivity, deviceIdx!!)
            }
            val deviceViewModel = work1.await()

            fragmentShowDeviceBinding.imageViewProductImageShowFragment.setImageBitmap(deviceViewModel.deviceImage)
            fragmentShowDeviceBinding.textViewTypeShowFragment.text = deviceViewModel.deviceType.str
            fragmentShowDeviceBinding.textViewNameShowFragment.text = deviceViewModel.deviceName
            fragmentShowDeviceBinding.textViewPriceShowFragment.text = "${deviceViewModel.devicePrice.toString()}원"
        }
    }

    // 정보 삭제 처리 메서드
    fun deleteDeviceInfo() {
        // 다이얼로그 표시
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("정말 삭제할까요?")
        materialAlertDialogBuilder.setMessage("삭제 할 경우 복원이 불가능 합니다.")
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i:Int ->
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    val deviceIdx = arguments?.getInt("deviceIdx")
                    DeviceRepository.deleteDeviceInfoByDeviceIdx(mainActivity, deviceIdx!!)
                }
                work1.join()
                mainFragment.removeFragment(SubFragmentName.SHOW_DEVICE_FRAGMENT)
            }

        }
        materialAlertDialogBuilder.show()
    }
}