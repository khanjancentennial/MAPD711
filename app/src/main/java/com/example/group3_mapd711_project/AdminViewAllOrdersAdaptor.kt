package com.example.group3_mapd711_project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.group3_mapd711_project.model.ItemCartList
import com.example.group3_mapd711_project.model.PreviousOrders


class AdminViewAllOrdersAdaptor(
    private val item: List<PreviousOrders>
) : RecyclerView.Adapter<AdminViewAllOrdersAdaptor.ItemPreviousOrderViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPreviousOrderViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_view_all_order, parent, false)
        return ItemPreviousOrderViewModel(view)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ItemPreviousOrderViewModel, position: Int) {
        holder.bind(item[position])
    }

    inner class ItemPreviousOrderViewModel(private val itemView: View) : ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.productNameTextViewPreviousOrderAdmin)
        private val price: TextView = itemView.findViewById(R.id.productPriceTextViewPreviousOrderAdmin)
        private val image: ImageView = itemView.findViewById(R.id.productImageViewPreviousOrderAdmin)
        private val qty: TextView = itemView.findViewById(R.id.productQtyTextViewPreviousOrderAdmin)
        private val changeStatusButtonAdmin: Button = itemView.findViewById(R.id.changeStatusButtonAdmin)
        private val status: TextView = itemView.findViewById(R.id.statusAdmin)


        fun bind(item: PreviousOrders) {
            itemName.text = item.title
            price.text = "$ ${item.price.toString()}"
            qty.text = "Qty: ${item.qty.toString()}"
            val assetImagePath = item.productImage
            Glide.with(itemView.context)
                .load(assetImagePath)
                .into(image)// Set

            status.text = item.status

            if(item.status == "In-Process"){
                changeStatusButtonAdmin.isVisible
                changeStatusButtonAdmin.setOnClickListener{
                    val intent = Intent(itemView.context, AdminOrderChangeStatusPage::class.java)

                    intent.putExtra("documentId",item.id)
                    intent.putExtra("productName",item.title)
                    intent.putExtra("productDescription",item.description)
                    intent.putExtra("productColor",item.color)
                    intent.putExtra("productStorage",item.storage)
                    intent.putExtra("productPrice",item.price.toString())
                    intent.putExtra("productImage",item.productImage)
                    intent.putExtra("productQty",item.qty.toString())
                    intent.putExtra("orderStatus",item.status)
                    itemView.context.startActivity(intent)
                }

            }else{
                changeStatusButtonAdmin.isVisible = false
            }


        }
    }
}