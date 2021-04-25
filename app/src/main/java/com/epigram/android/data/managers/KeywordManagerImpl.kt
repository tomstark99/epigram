package com.epigram.android.data.managers

import com.epigram.android.data.api.epigram.EpigramService
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.BuildConfig
import com.epigram.android.data.model.Post
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.Single
import kotlin.math.ln
import kotlin.time.TimedValue

class KeywordManagerImpl (val service: EpigramService,
                          private val likedIds: Preference<MutableSet<String>> = PreferenceModule.likedPosts) : KeywordManager{

    private var stopwords = listOf("the", "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now")

    override fun generateKeywordsFromLiked(): Single<List<String>> {
//        var ids = listOf("60355a38a023e2004516e71c","5fb8f3eed654ef00392c2799","605c7f369d0b91003b11788d","6034eb4038d228003946c7d0","5fb2f19f45dd500039b47fe3","6048e74058317400392f9eee","5fda709615753a0039bf4b0a","5fbe9fa641c5df003974eedf","6061c283afc701003bcf5373","606eda4eafc701003bcf6203")//likedIds.get().toList()
        likedIds.get().toList().forEach { /* Timber.d("ids: %s", it) */ }
        return service.getPostsIDs(BuildConfig.API_KEY, "id:[${likedIds.get().toList().joinToString(",")}]", "title", "200").map { body ->
            body.posts.map { it.title.toLowerCase().replace(Regex("[^a-zA-Z\\d\\s]"), "").replace("  ", " ") }
        }.flatMap { titles ->

            Single.just(generateKeywords(titles))
//            Single.just(getTop(tfIdfScore, words.size, titles.size))
        }
    }

    override fun generateKeywordsFromTitle(title: String): Single<List<String>> {
        var words = title.toLowerCase().replace(Regex("[^a-zA-Z\\d\\s]"), "").replace("  ", " ").split(" ").toMutableList()
        words.removeAll(stopwords)
        return Single.just(words.toList())
    }

    private fun generateKeywords(titles: List<String>): List<String> {

        titles.forEach { /* Timber.d("title: %s", it) */ }
        val words = titles.map { it.split(" ") }.flatten().map { it.toLowerCase() }.toMutableList()
        words.removeAll(stopwords)
        val tfScore = mutableMapOf<String, Double>()
        words.forEach { if(tfScore.containsKey(it)) tfScore[it] = tfScore[it]!! + 1.0 else tfScore[it] = 1.0 }

        val tfScoreU = tfScore.mapValues { it.value/words.size }

        val idfScore = mutableMapOf<String, Double>()
        words.forEach { if (idfScore.containsKey(it)) idfScore[it] = checkSent(it, titles) else idfScore[it] = 1.0 }
        val idfScoreU = idfScore.mapValues { ln(titles.size/it.value) }
        val tfIdfScore = idfScoreU.mapValues { it.value * (tfScoreU[it.key] ?: error("")) }

        return getTop(tfIdfScore, words.size, titles.size)
    }

    private fun checkSent(word: String, titles: List<String>): Double {
        return titles.filterIndexed { i, _ -> titles.map { it.contains(word) }[i] }.size.toDouble()
    }

    private fun getTop(dict: Map<String, Double>, wLen: Int, tLen: Int): List<String> {
        return dict.toList().sortedByDescending { (_, v) -> v }.takeWhile { it.second > ((1.0/wLen) * (ln(tLen/1.0)))}.toMap().keys.toList()
    }

}