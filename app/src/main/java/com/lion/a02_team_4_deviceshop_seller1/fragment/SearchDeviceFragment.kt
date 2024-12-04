package com.lion.a02_team_4_deviceshop_seller1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.a02_team_4_deviceshop_seller1.MainActivity
import com.lion.a02_team_4_deviceshop_seller1.R
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentSearchDeviceBinding
import com.lion.a02_team_4_deviceshop_seller1.databinding.RowMainBinding
import com.lion.a02_team_4_deviceshop_seller1.repository.DeviceRepository
import com.lion.a02_team_4_deviceshop_seller1.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchDeviceFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentSearchDeviceBinding: FragmentSearchDeviceBinding
    lateinit var mainActivity: MainActivity

    var deviceList = mutableListOf<DeviceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSearchDeviceBinding = FragmentSearchDeviceBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 툴바 구성 메서드 호출
        settingToolbarSearchStudent()
        // RecyclerView 구성 메서드 호출
        settingRecyclerView()
        // 입력 요소 설정 메서드 호출
        settingTextField()

        return fragmentSearchDeviceBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingToolbarSearchStudent(){
        fragmentSearchDeviceBinding.apply {
            toolbarSearchDevice.title = "제품 정보 검색"

            toolbarSearchDevice.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarSearchDevice.setNavigationOnClickListener {
                mainFragment.removeFragment(SubFragmentName.SEARCH_DEVICE_FRAGMENT)
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentSearchDeviceBinding.apply {
            // 어뎁터
            recyclerViewSearchDevice.adapter = RecyclerViewDeviceSearchAdapter()
            // LayoutManager
            recyclerViewSearchDevice.layoutManager = GridLayoutManager(mainActivity, 2)
            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewSearchDevice.addItemDecoration(deco)
        }
    }

    // Recyclerview의 어뎁터
    inner class RecyclerViewDeviceSearchAdapter : RecyclerView.Adapter<RecyclerViewDeviceSearchAdapter.ViewHolderDeviceSearch>(){
        inner class ViewHolderDeviceSearch(var rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDeviceSearch {
            val rowText1Binding = RowMainBinding.inflate(layoutInflater, parent, false)
            val viewHolderDeviceSearch = ViewHolderDeviceSearch(rowText1Binding)
            return viewHolderDeviceSearch
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }

        override fun onBindViewHolder(holder: ViewHolderDeviceSearch, position: Int) {
            val device = deviceList[position]
            holder.rowMainBinding.imageViewRow.setImageBitmap(device.deviceImage)
            holder.rowMainBinding.textViewRowProductPrice.text = device.deviceName
            holder.rowMainBinding.textViewRowProductPrice.text = "${device.devicePrice.toString()}원"
        }
    }

    // 입력 요소 설정
    fun settingTextField() {
        fragmentSearchDeviceBinding.apply {
            // 검색창에 포커스를 둔다
            mainActivity.showSoftInput(textFieldSearchDeviceName.editText!!)
            // 키보드의 엔터를 누르면 동작하는 리스너
            textFieldSearchDeviceName.editText?.setOnEditorActionListener { v, actionId, event ->
                // 검색 데이터를 가져와 보여준다.
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO) {
                        val keyword = textFieldSearchDeviceName.editText?.text.toString()
                        DeviceRepository.selectDeviceDataAllByDeviceName(mainActivity, keyword)
                    }
                    deviceList = work1.await()
                    recyclerViewSearchDevice.adapter?.notifyDataSetChanged()
                }
                mainActivity.hideSoftInput()
                true
            }

        }
    }

}