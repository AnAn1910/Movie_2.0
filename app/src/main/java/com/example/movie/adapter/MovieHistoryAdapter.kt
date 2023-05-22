package com.example.movie.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie.R
import com.example.movie.model.Movie

class MovieHistoryAdapter(private val listMovies: MutableList<Movie?>?, private var mActivity: Activity?,
                          private val iClickItemListener: IClickItemListener?) : RecyclerView.Adapter<MovieHistoryAdapter.MovieViewHolder?>() {

    interface IClickItemListener {
        fun onClickItem(movie: Movie?)
        fun onClickFavorite(id: Int, favorite: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_history, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = listMovies?.get(position) ?: return
        holder.tvTitleMovie?.text = movie.getTitle()
        if (movie.getImage() != null && movie.getImage() != "") {
            holder.imgMovie?.let { Glide.with(mActivity?.applicationContext!!).load(movie.getImage()).error(R.drawable.ic_no_image).into(it) }
        } else {
            holder.imgMovie?.setImageResource(R.drawable.ic_no_image)
        }
        if (movie.isFavorite()) {
            holder.imgFavorite?.setImageResource(R.drawable.icon_favorite_big_on)
        } else {
            holder.imgFavorite?.setImageResource(R.drawable.icon_favorite_big_off)
        }
        holder.layoutItem?.setOnClickListener { iClickItemListener?.onClickItem(movie) }
        holder.imgFavorite?.setOnClickListener {
            if (movie.isFavorite()) {
                iClickItemListener?.onClickFavorite(movie.getId(), false)
            } else {
                iClickItemListener?.onClickFavorite(movie.getId(), true)
            }
        }
    }

    fun release() {
        if (mActivity != null) {
            mActivity = null
        }
    }

    override fun getItemCount(): Int {
        return listMovies?.size ?: 0
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMovie: ImageView? = itemView.findViewById<ImageView?>(R.id.img_movie)
        val imgFavorite: ImageView? = itemView.findViewById<ImageView?>(R.id.img_favorite)
        val tvTitleMovie: TextView? = itemView.findViewById<TextView?>(R.id.tv_title_movie)
        val layoutItem: LinearLayout? = itemView.findViewById<LinearLayout?>(R.id.layout_item)

    }

}