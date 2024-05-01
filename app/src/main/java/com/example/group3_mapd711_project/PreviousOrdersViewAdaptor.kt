package com.example.group3_mapd711_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.group3_mapd711_project.model.ItemCartList
import com.example.group3_mapd711_project.model.PreviousOrders


class PreviousItemViewAdaptor(
    private val item: List<PreviousOrders>
) : RecyclerView.Adapter<PreviousItemViewAdaptor.ItemPreviousOrderViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPreviousOrderViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.previous_orders, parent, false)
        return ItemPreviousOrderViewModel(view)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ItemPreviousOrderViewModel, position: Int) {
        holder.bind(item[position])
    }

    inner class ItemPreviousOrderViewModel(private val itemView: View) : ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.productNameTextViewPreviousOrder)
        private val price: TextView = itemView.findViewById(R.id.productPriceTextViewPreviousOrder)
        private val image: ImageView = itemView.findViewById(R.id.productImageViewPreviousOrder)
        private val qty: TextView = itemView.findViewById(R.id.productQtyTextViewPreviousOrder)
        private val status: TextView = itemView.findViewById(R.id.orderStatus)


        fun bind(item: PreviousOrders) {
            itemName.text = item.title
            price.text = "$ ${item.price.toString()}"
            qty.text = "Qty: ${item.qty.toString()}"
            val assetImagePath = item.productImage
            Glide.with(itemView.context)
                .load(assetImagePath)
                .into(image)

            status.text = "Order ${item.status}"

        }
    }
}