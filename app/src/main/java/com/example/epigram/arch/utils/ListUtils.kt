package com.example.epigram.arch.utils

class ListUtils{


    fun <T>duplicatePost(list: List<T>): List<T>{
        return list.toMutableList().distinct()
    }
}