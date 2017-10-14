package com.pierrejacquier.todoboard.features.devices

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.inflate
import javax.inject.Inject

class DevicesFragment : RxBaseFragment() {

    companion object {
        private val KEY_REDDIT_NEWS = "redditNews"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_devices)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_REDDIT_NEWS)) {
//            redditNews = savedInstanceState[KEY_REDDIT_NEWS] as RedditNews
//            (news_list.adapter as NewsAdapter).clearAndAddNews(redditNews!!.news)
        } else {
//            requestNews()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        val news = (news_list.adapter as NewsAdapter).getNews()
//        if (redditNews != null && news.isNotEmpty()) {
//            outState.putParcelable(KEY_REDDIT_NEWS, redditNews?.copy(news = news))
//        }
    }
//
//    fun initAdapter() {
//        if (news_list.adapter == null) {
//            news_list.adapter = NewsAdapter()
//        }
//    }
}