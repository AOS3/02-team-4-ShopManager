package com.lion.a02_team_4_deviceshop_seller1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.a02_team_4_deviceshop_seller1.MainActivity
import com.lion.a02_team_4_deviceshop_seller1.R
import com.lion.a02_team_4_deviceshop_seller1.databinding.FragmentDeviceListBinding
import com.lion.a02_team_4_deviceshop_seller1.databinding.RowMainBinding
import com.lion.a02_team_4_deviceshop_seller1.repository.DeviceRepository
import com.lion.a02_team_4_deviceshop_seller1.util.formatToComma
import com.lion.a02_team_4_deviceshop_seller1.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DeviceListFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentDeviceListBinding: FragmentDeviceListBinding
    lateinit var mainActivity: MainActivity

    var deviceList = mutableListOf<DeviceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDeviceListBinding = FragmentDeviceListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // Toolbar 구성 메서드 호출
        settingToolbar()
        // RecyclerView 구성 메서드 호출
        settingRecyclerView()
        // RecyclerView 갱신 메서드 호출
        refreshRecyclerViewMain()

        return fragmentDeviceListBinding.root
    }

    // Toolbar를 구성하는 메서드
    fun settingToolbar() {
        fragmentDeviceListBinding.apply {
            // 네비게이션 메뉴
            toolbarDeviceList.setNavigationIcon(R.drawable.menu_24px)
            toolbarDeviceList.setNavigationOnClickListener {
                mainFragment.fragmentMainBinding.drawerLayoutMain.open()
            }

            // 타이틀
            toolbarDeviceList.title = "디바이스 목록"
            // 메뉴
            toolbarDeviceList.inflateMenu(R.menu.toolbar_device_list_menu)
            toolbarDeviceList.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.main_toolbar_menu_add -> {
                        mainFragment.replaceFragment(SubFragmentName.INPUT_DEVICE_FRAGMENT, true, true, null)
                    }
                    R.id.main_toolbar_menu_Search -> {
                        mainFragment.replaceFragment(SubFragmentName.SEARCH_DEVICE_FRAGMENT, true, true, null)
                    }
                }
                true
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentDeviceListBinding.apply {
            // 어뎁터
            recyclerViewDeviceList.adapter = RecyclerViewMainAdapter()
            // LayoutManager
            recyclerViewDeviceList.layoutManager = GridLayoutManager(mainActivity, 2)
            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewDeviceList.addItemDecoration(deco)
        }
    }

    // RecyclerView의 어뎁터
    inner class RecyclerViewMainAdapter :
        RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>() {
        // ViewHolder
        inner class ViewHolderMain(val rowMainBinding: RowMainBinding) :
            RecyclerView.ViewHolder(rowMainBinding.root), OnClickListener {
            override fun onClick(v: View?) {
                val dataBundle = Bundle()
                dataBundle.putInt("deviceIdx", deviceList[adapterPosition].deviceIdx)
                // ShowDeviceList 로 이동
                mainFragment.replaceFragment(SubFragmentName.SHOW_DEVICE_FRAGMENT, true, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater, parent, false)
            val viewHolderMain = ViewHolderMain(rowMainBinding)
            rowMainBinding.root.setOnClickListener(viewHolderMain)
            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }

        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            val device = deviceList[position]
            holder.rowMainBinding.imageViewRow.setImageBitmap(device.deviceImage)
            holder.rowMainBinding.textViewRowProductName.text = device.deviceName
            holder.rowMainBinding.textViewRowProductPrice.text = "${device.devicePrice.formatToComma()}"
        }
    }

    // RecyclerView를 갱신하는 메서드
    fun refreshRecyclerViewMain() {
        // 제품 정보를 가져온다.
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                DeviceRepository.selectDeviceInfoAll(mainActivity)
            }
            deviceList = work1.await()
            // 리사이클러뷰 갱신
            fragmentDeviceListBinding.recyclerViewDeviceList.adapter?.notifyDataSetChanged()
        }
    }

}