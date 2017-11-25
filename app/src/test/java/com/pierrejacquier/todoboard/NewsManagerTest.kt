//package com.pierrejacquier.todoboard
//
//import com.nhaarman.mockito_kotlin.*
//import com.pierrejacquier.todoboard.data.api.*
//import com.pierrejacquier.todoboard.commons.RedditNews
//import com.pierrejacquier.todoboard.features.news.NewsManager
//import okhttp3.MediaType
//import okhttp3.ResponseBody
//import org.junit.Before
//import org.junit.Test
//import org.junit.Assert.assertEquals
//import retrofit2.Call
//import retrofit2.Response
//import rx.observers.TestSubscriber
//import java.util.*
//
//class NewsManagerTest {
//    var testSub = TestSubscriber<RedditNews>()
//    var apiMock = mock<NewsApiBase>()
//    var callMock = mock<Call<RedditNewsResponse>>()
//
//    @Before
//    fun setup() {
//        testSub = TestSubscriber<RedditNews>()
//        apiMock = mock<NewsApiBase>()
//        callMock = mock<Call<RedditNewsResponse>>()
//        whenever(apiMock.getNews(any(), any())).thenReturn(callMock)
//    }
//
//    @Test
//    fun testSuccess_basic() {
//        // prepare
//        val redditNewsResponse = RedditNewsResponse(RedditDataResponse(listOf(), null, null))
//        val response = Response.success(redditNewsResponse)
//
//        whenever(callMock.execute()).thenReturn(response)
//
//        // call
//        val newsManager = NewsManager(apiMock)
//        newsManager.getNews("").subscribe(testSub)
//
//        // assert
//        with(testSub) {
//            assertNoErrors()
//            assertValueCount(1)
//            assertCompleted()
//        }
//    }
//
//    @Test
//    fun testSuccess_checkOneNews() {
//        // prepare
//        val newsData = RedditNewsDataResponse(
//                "author", "title", 10, Date().time, "thumbnail", "url"
//        )
//        val newsResponse = RedditChildrenResponse(newsData)
//        val redditNewsResponse = RedditNewsResponse(RedditDataResponse(listOf(newsResponse), null, null))
//        val response = Response.success(redditNewsResponse)
//
//        whenever(callMock.execute()).thenReturn(response)
//
//        // call
//        val newsManager = NewsManager(apiMock)
//        newsManager.getNews("").subscribe(testSub)
//
//        // assert
//        with(testSub) {
//            assertNoErrors()
//            assertValueCount(1)
//            assertCompleted()
//        }
//
//        assertEquals(newsData.author, testSub.onNextEvents[0].news[0].author)
//        assertEquals(newsData.title, testSub.onNextEvents[0].news[0].title)
//    }
//
//    @Test
//    fun testError() {
//        // prepare
//        val responseError = Response.error<RedditNewsResponse>(500,
//                ResponseBody.create(MediaType.parse("application/json"), ""))
//
//        whenever(callMock.execute()).thenReturn(responseError)
//
//        // call
//        val newsManager = NewsManager(apiMock)
//        newsManager.getNews("").subscribe(testSub)
//
//        // assert
//        assertEquals(1, testSub.onErrorEvents.size)
//    }
//
//}