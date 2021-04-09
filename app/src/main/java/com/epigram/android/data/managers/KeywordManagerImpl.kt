package com.epigram.android.data.managers

import com.epigram.android.data.api.epigram.EpigramService
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.BuildConfig
import com.epigram.android.data.model.Post
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.Single
import timber.log.Timber
import kotlin.math.ln
import kotlin.time.TimedValue

class KeywordManagerImpl (val service: EpigramService,
                          private val likedIds: Preference<MutableSet<String>> = PreferenceModule.likedPosts) : KeywordManager{

    private var stopwords = listOf("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now")

    override fun generateKeywords(): Single<List<String>> {
        var ids = listOf("60355a38a023e2004516e71c","5fb8f3eed654ef00392c2799","605c7f369d0b91003b11788d","6034eb4038d228003946c7d0","5fb2f19f45dd500039b47fe3","6048e74058317400392f9eee","5fda709615753a0039bf4b0a","5fbe9fa641c5df003974eedf","6061c283afc701003bcf5373","606eda4eafc701003bcf6203")//likedIds.get().toList()
        ids.forEach { Timber.d("ids: %s", it) }
        return service.getPostsIDs(BuildConfig.API_KEY, "id:[${ids.joinToString(",")}]","200").map { body ->
            val posts = ArrayList<Post>()
            body.posts.forEach { post -> Post.fromTemplate(post)?.let { posts.add(it) } }
            posts
        }.flatMap { posts ->

            val titles = posts.map { it.title.replace(Regex("[^a-zA-Z\\d\\s]"), "").replace("  ", " ") }
            titles.forEach { Timber.d("title: %s", it) }
            val words = titles.map { it.split(" ") }.flatten().toMutableList()
            words.removeAll(stopwords)
            val tfScore = mutableMapOf<String, Int>()
            words.forEach { if(tfScore.containsKey(it)) tfScore[it] = tfScore[it]!! + 1 else tfScore[it] = 1 }

            tfScore.mapValues { it.value/words.size }
            val idfScore = mutableMapOf<String, Double>()
            words.forEach { if (idfScore.containsKey(it)) idfScore[it] = checkSent(it, titles) else idfScore[it] = 1.0 }
            idfScore.mapValues { ln(titles.size/it.value) }
            val tfIdfScore = idfScore.mapValues { it.value * tfScore[it.key]!! }

            Single.just(getTop(tfIdfScore, 10))
        }
    }

    fun checkSent(word: String, titles: List<String>): Double {
        return titles.filterIndexed { i, _ -> titles.map { it.contains(word) }[i] }.size.toDouble()
    }

    fun getTop(dict: Map<String, Double>, n: Int): List<String> {
        return dict.toList().sortedBy { (_, v) -> v }.take(n).toMap().keys.toList()
    }

}