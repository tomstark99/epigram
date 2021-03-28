package com.epigram.android.data.model

import java.io.Serializable

data class Views(
    val views: List<String>,
    val slugs: List<String>
):Serializable{
    companion object{
        fun fromTemplate(template: ViewTemplate): Views? {
            var slugs = mutableListOf<String>()
            var views = mutableListOf<String>()
            template.rows.views.forEach {
                if (it.size == 1) {
                    views.add(it.first())
                } else {
                    slugs.add(it[0])
                    views.add(it[1])
                }
            }
            return Views(views, slugs)
        }
    }
}

data class ViewTemplate(
    val rows: Rows
)

data class GaWrapper<T>(
    val content: T
)

data class Rows(
    val views: List<List<String>>
)