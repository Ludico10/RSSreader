package com.example.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class RSSFragment : Fragment() {
    private val RSS_FEED_LINK = "https://proweb63.ru/feed.xml"
    private lateinit var rvList: RecyclerView
    private lateinit var txtEmpty: TextView
    private var adapter: Adapter? = null
    private var rssItems = ArrayList<RssItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        txtEmpty = view.findViewById(R.id.txtEmpty)
        rvList = view.findViewById(R.id.rvList)
        adapter = Adapter(rssItems, activity)
        rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvList.adapter = adapter
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart(): Unit = runBlocking{
        super.onStart()
        rvList.visibility = View.INVISIBLE
        val url = URL(RSS_FEED_LINK)
        var list: List<RssItem>? = null
        val job = GlobalScope.launch {
            list = getItems(url)
        }
        job.join()
        rvList.visibility = View.VISIBLE
        updateRV(list)
    }

    private fun getItems(param: URL?) : List<RssItem>? {
        var list: List<RssItem>? = null
        try {
            val connect = param?.openConnection() as HttpsURLConnection
            connect.run {
                readTimeout = 8000
                connectTimeout = 8000
                requestMethod = "GET"
                connect()
            }
            val responseCode = connect.responseCode
            if (responseCode == 200) {
                val stream = connect.inputStream
                val parser = RssParser()
                list = parser.parse(stream!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRV(rssItemsL: List<RssItem>?) {
        if (rssItemsL != null) {
            if (rssItemsL.isNotEmpty()) {
                txtEmpty.visibility = View.INVISIBLE
                rssItems.addAll(rssItemsL)
                adapter?.notifyDataSetChanged()
            } else {
                txtEmpty.text = context?.getString(R.string.empty_message)
                txtEmpty.visibility = View.VISIBLE
            }
        } else {
            txtEmpty.text = context?.getString(R.string.connection_message)
            txtEmpty.visibility = View.VISIBLE
        }
    }
}