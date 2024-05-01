package com.example.group3_mapd711_project
import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.group3_mapd711_project.model.ItemCartList
import com.google.firebase.firestore.FirebaseFirestore


class ItemCartViewAdaptor(
    private val item: List<ItemCartList>
) : RecyclerView.Adapter<ItemCartViewAdaptor.ItemCartViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCartViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return ItemCartViewModel(view)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ItemCartViewModel, position: Int) {
        holder.bind(item[position])
    }

    inner class ItemCartViewModel(private val itemView: View) : ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.productNameTextViewCart)
        private val price: TextView = itemView.findViewById(R.id.productPriceTextViewCart)
        private val image: ImageView = itemView.findViewById(R.id.productImageViewCart)
        private val qty: TextView = itemView.findViewById(R.id.productQtyTextViewCart)
        private val productTotalPrice: TextView = itemView.findViewById(R.id.productTotalPrice)
        private val cancelTextViewCart: TextView = itemView.findViewById(R.id.cancelTextViewCart)
        private val firestoreDB = FirebaseFirestore.getInstance()



        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: ItemCartList) {
            itemName.text = item.title
            price.text = "${'$'} ${item.price.toString()}"
            qty.text = "Qty: ${item.qty.toString()}"
            productTotalPrice.text = "Total Price: ${item.totalPrice.toString()}"
            val assetImagePath = item.productImage
            Glide.with(itemView.context)
                .load(assetImagePath)
                .into(image)// Set

            val documentIdToDelete = item.id

            // Create a reference to the document you want to delete
            val documentToDeleteRef = firestoreDB.collection("Users")
                .document(item.email.toString())
                .collection("cartItems")
                .document(documentIdToDelete)

            cancelTextViewCart.setOnClickListener {
                // Delete the document from Firestore
                documentToDeleteRef.delete()
                    .addOnSuccessListener {
                        val intent = Intent(itemView.context, Home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        itemView.context.startActivity(intent)

                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that occurred while deleting the document
                    }
            }


        }
    }
}