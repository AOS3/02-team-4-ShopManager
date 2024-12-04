package com.lion.a02_team_4_deviceshop_seller1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.a02_team_4_deviceshop_seller1.MainActivity
import com.lion.a02_team_4_deviceshop_seller1.R
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentMainBinding
import com.lion.a02_team_4_deviceshop_seller1.databinding.NavigationViewHeaderMainBinding


class MainFragment : Fragment() {
    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // NavigationView 설정 메서드 호출
        settingNavigationViewMain()

        // 첫 화면이 보이도록 설정
        replaceFragment(SubFragmentName.DEVICE_LIST_FRAGMENT, false, false, null)

        return fragmentMainBinding.root
    }

    // NavigationView를 설정하는 메서드
    fun settingNavigationViewMain(){
        fragmentMainBinding.apply {
            // 네비게이션 뷰의 헤더
            val navigationViewHeaderMainBinding = NavigationViewHeaderMainBinding.inflate(layoutInflater)
            navigationViewMain.addHeaderView(navigationViewHeaderMainBinding.root)
            // 메뉴
            navigationViewMain.inflateMenu(R.menu.navigation_main_menu)
            // 첫 번째 메뉴가 선택 되어 있도록 한다.
            navigationViewMain.setCheckedItem(R.id.navigation_main_menu_device_list)
            navigationViewMain.setNavigationItemSelectedListener {
                // 메뉴 id로 분기
                when(it.itemId){
                    // 제품 목록
                    R.id.navigation_main_menu_device_list -> {
                        replaceFragment(SubFragmentName.DEVICE_LIST_FRAGMENT, false, false, null)
                    }
                }
                drawerLayoutMain.close()
                true
            }
        }
    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: SubFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(fragmentName){
            // 제품 목록 화면
            SubFragmentName.DEVICE_LIST_FRAGMENT -> DeviceListFragment(this)
            // 제품 정보 입력 화면
            SubFragmentName.INPUT_DEVICE_FRAGMENT -> InputDeviceFragment(this)
            // 제품 정보 출력 화면
            SubFragmentName.SHOW_DEVICE_FRAGMENT -> ShowDeviceFragment(this)
            // 제품 정보 수정 화면
            SubFragmentName.EDIT_DEVICE_FRAGMENT -> EditDeviceFragment(this)
            // 제품 검색 화면
            SubFragmentName.SEARCH_DEVICE_FRAGMENT -> SearchDeviceFragment(this)
        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        mainActivity.supportFragmentManager.commit {

            if(animate) {
                newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fragmentContainerViewNavigation, newFragment)
            if(isAddToBackStack){
                addToBackStack(fragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: SubFragmentName){
        mainActivity.supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}

// 프래그먼트를 나타내는 값
enum class SubFragmentName(var number:Int, var str:String){
    // 첫 화면
    DEVICE_LIST_FRAGMENT(1, "DeviceListFragment"),
    // 입력 화면
    INPUT_DEVICE_FRAGMENT(2, "InputDeviceFragment"),
    // 출력 화면
    SHOW_DEVICE_FRAGMENT(3, "ShowDeviceFragment"),
    // 수정 화면
    EDIT_DEVICE_FRAGMENT(4, "EditDeviceFragment"),
    // 검색 화면
    SEARCH_DEVICE_FRAGMENT(5, "SearchDeviceFragment"),
}