package com.epigram.android.arch.utils

class ListUtils{

    fun <T>duplicatePost(list: List<T>): List<T>{
        return list.toMutableList().distinct()
    }

}