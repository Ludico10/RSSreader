package com.example.rssreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.jsoup.Jsoup
import java.io.IOException

class Adapter(
    private val iValues: List<RssItem>,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    inner class ViewHolder(val iView: View) : RecyclerView.ViewHolder(iView) {
        val titleTV: TextView? = iView.findViewById(R.id.txtTitle)
        val contentTV: TextView? = iView.findViewById(R.id.txtContent)
        val pubDateTV: TextView? = iView.findViewById(R.id.txtPubDate)
        val featuredImg: ImageView? = iView.findViewById(R.id.featuredImg)
    }

    private val itemOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val activity = context as MainActivity
        activity.curFragment = PageFragment()
        val bundle = Bundle()
        bundle.putString("URL", v.tag as String)
        (activity.curFragment as PageFragment).arguments = bundle
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_root, activity.curFragment!!, "READER")
            .addToBackStack(null).commit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = iValues[position]
        holder.titleTV?.text = item.title
        holder.contentTV?.text  = item.description.substringBefore('<')
        holder.pubDateTV?.text = item.pubDate
        holder.iView.tag = item.link
        holder.iView.setOnClickListener(itemOnClickListener)

        val link = getFeaturedImageLink(item.description)
        if (link != null) {
            holder.featuredImg?.contentDescription = link
            context?.let {
                holder.featuredImg?.let { it1 ->
                    Glide.with(it)
                        .load(link)
                        .centerCrop()
                        .into(it1)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return iValues.size
    }

    private fun getFeaturedImageLink(htmlText: String): String? {
        var result: String? = null
        try {
            val doc = Jsoup.parse(htmlText)
            val imgs = doc.select("img")
            for (img in imgs)
                result = img.attr("src")
        } catch (_: IOException) { }
        return result
    }
}