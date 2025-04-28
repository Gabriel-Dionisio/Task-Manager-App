package com.example.taskmaneger.listener

import android.view.View

interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
    fun onLongItemClick(view: View, position: Int)
}