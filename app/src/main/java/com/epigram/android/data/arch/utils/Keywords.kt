package com.epigram.android.data.arch.utils

import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.managers.PostManager
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.ln

class Keywords(private val postManager: PostManager = DataModule.postManager,
               private val likedIds: Preference<MutableSet<String>> = PreferenceModule.likedPosts) {

    private var stopwords = listOf("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now")

    fun generateKeywords(callback: KeywordsGenerated) {
        postManager.getPostsById(0, likedIds.get().toList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { posts ->
                val titles = posts.map { it.title.replace(Regex("[^a-zA-Z\\d\\s]"), "").replace("  ", " ") }
                val words = titles.map { it.split(" ") }.flatten().toMutableList()
                words.removeAll(stopwords)
                val tfScore = mutableMapOf<String, Int>()
                words.forEach { if(tfScore.containsKey(it)) tfScore[it] = tfScore[it]!! + 1 else tfScore[it] = 1 }
                tfScore.mapValues { it.value/words.size }
                val idfScore = mutableMapOf<String, Double>()
                words.forEach { if (idfScore.containsKey(it)) idfScore[it] = checkSent(it, titles) else idfScore[it] = 1.0 }
                idfScore.mapValues { ln(titles.size/it.value) }
                val tfIdfScore = idfScore.mapValues { it.value * tfScore[it.key]!! }

                callback.onKeywordsGenerated(getTop(tfIdfScore, 10))
            }.dispose()


    }

    fun checkSent(word: String, titles: List<String>): Double {
        return titles.filterIndexed { i, _ -> titles.map { it.contains(word) }[i] }.size.toDouble()
    }

    fun getTop(dict: Map<String, Double>, n: Int): List<String> {
        return dict.toList().sortedBy { (_, v) -> v }.take(n).toMap().keys.toList()
    }
}