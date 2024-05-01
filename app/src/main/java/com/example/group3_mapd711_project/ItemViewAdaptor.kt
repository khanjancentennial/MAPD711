package com.example.group3_mapd711_project
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.group3_mapd711_project.model.ItemList


class ItemViewAdaptor(
    private val item: List<ItemList>
) : RecyclerView.Adapter<ItemViewAdaptor.ItemViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ItemViewModel(view)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ItemViewModel, position: Int) {
        holder.bind(item[position])
    }

    inner class ItemViewModel(private val itemView: View) : ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.productNameTextView)
        private val price: TextView = itemView.findViewById(R.id.priceTextView)
        private val image: ImageView = itemView.findViewById(R.id.productImageView)

//        val plusButton: Button = itemView.findViewById(R.id.buttonPlus)
//        val minusButton: Button = itemView.findViewById(R.id.buttonMinus)
        val viewDetailsButton: Button = itemView.findViewById(R.id.buyButton)
//        val textView: TextView = itemView.findViewById(R.id.qtyTextView)


        private var quantity: Int = 0

        fun bind(item: ItemList) {
            itemName.text = item.title
            price.text = "${'$'} ${item.price.toString()}"
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .into(image)


            viewDetailsButton.setOnClickListener{

                    val intent = Intent(itemView.context, customerViewProductDetails::class.java)

                intent.putExtra("documentId",item.documentId)
                intent.putExtra("productName",item.title)
                intent.putExtra("productDescription",item.description)
                intent.putExtra("productColor",item.color)
                intent.putExtra("productStorage",item.storage)
                intent.putExtra("productPrice",item.price.toString())
                intent.putExtra("productImage",item.imageUrl)
                itemView.context.startActivity(intent)

            }
        }
    }
}