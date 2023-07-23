package com.maou.beaconiotgateway.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maou.beaconiotgateway.databinding.ItemDeviceBinding
import com.maou.beaconiotgateway.domain.model.BleDevice
import com.maou.beaconiotgateway.utils.TimeHelper

class BleAdapter : RecyclerView.Adapter<BleAdapter.ViewHolder>() {
    private val listItem = arrayListOf<BleDevice>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(items: List<BleDevice>) {
        listItem.clear()
        listItem.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        listItem.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleAdapter.ViewHolder {
        val itemBinding =
            ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BleAdapter.ViewHolder, position: Int) {
        holder.bind(listItem[position])
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    inner class ViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(items: BleDevice) {
                with(binding) {
                    tvDeviceAddress.text = items.deviceAddress
                    tvDeviceRssi.text = "${items.rssi} dBm"
                    tvDeviceDate.text = TimeHelper.timestampToDate(items.timestamp)
                }
            }
    }
}