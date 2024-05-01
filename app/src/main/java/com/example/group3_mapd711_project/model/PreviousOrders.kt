package com.example.group3_mapd711_project.model

data class PreviousOrders(val id: String,
                          val email: String,
                          val title: String,
                          val description: String,
                          val color: String,
                          val storage: String,
                          val price:Int,
                          val qty:Int,
                          val totalPrice: Int,
                          val productImage: String,
                          val status : String)