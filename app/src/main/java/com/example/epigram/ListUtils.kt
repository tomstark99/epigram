package com.example.epigram

class ListUtils{

    fun <T>duplicatePost(list: List<T>): List<T>{
        return list.toMutableList().distinct()
    }
}