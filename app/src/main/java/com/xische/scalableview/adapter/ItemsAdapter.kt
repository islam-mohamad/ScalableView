package com.xische.scalableview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xische.scalableview.R
import com.xische.scalableview.databinding.ActionViewBinding
import com.xische.scalableview.model.ItemsModel


class ItemsAdapter : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    var itemList: List<ItemsModel> = listOf()

    class ViewHolder(private val binding: ActionViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsModel) {
            binding.tvTitle.text = "${item.id}- ${item.title}"
            binding.tvSubTitle.text = item.subTitle
            binding.ivTile.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.ivTile.context,
                    R.drawable.ic_action_file
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}