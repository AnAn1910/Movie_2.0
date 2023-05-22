package com.example.movie.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movie.MyApplication
import com.example.movie.R
import com.example.movie.activity.PlayMovieActivity
import com.example.movie.adapter.MovieAdapter
import com.example.movie.model.Movie
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.*

class FavoriteFragment : Fragment() {

    private var mView: View? = null
    private var rcvFavorite: RecyclerView? = null
    private var listMovies: MutableList<Movie?>? = null
    private var movieAdapter: MovieAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_favorite, container, false)
        rcvFavorite = mView?.findViewById(R.id.rcv_favorite)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        rcvFavorite?.layoutManager = gridLayoutManager
        listMovies = ArrayList()
        movieAdapter = MovieAdapter(listMovies, activity, object : MovieAdapter.IClickItemListener {
            override fun onClickItem(movie: Movie?) {
                onClickItemMovie(movie)
            }

            override fun onClickFavorite(id: Int, favorite: Boolean) {
                onClickFavoriteMovie(id, favorite)
            }
        })
        rcvFavorite?.adapter = movieAdapter
        getListMoviesFavorite()
        return mView
    }

    private fun getListMoviesFavorite() {
        MyApplication[activity]?.getDatabaseReference()?.orderByChild("favorite")?.equalTo(true)
                ?.addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        val movie: Movie? = dataSnapshot.getValue<Movie>(Movie::class.java)
                        if ((movie == null) || (listMovies == null) || (movieAdapter == null)) {
                            return
                        }
                        listMovies?.add(0, movie)
                        movieAdapter?.notifyDataSetChanged()
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                        val movie: Movie? = dataSnapshot.getValue<Movie>(Movie::class.java)
                        if ((movie == null) || (listMovies == null) || listMovies!!.isEmpty() || (movieAdapter == null)) {
                            return
                        }
                        for (movieEntity: Movie? in listMovies!!) {
                            if (movie.getId() == movieEntity?.getId()) {
                                if (!movie.isFavorite()) {
                                    listMovies?.remove(movieEntity)
                                } else {
                                    movieEntity.setImage(movie.getImage())
                                    movieEntity.setTitle(movie.getTitle())
                                    movieEntity.setUrl(movie.getUrl())
                                    movieEntity.setHistory(movie.isHistory())
                                }
                                break
                            }
                        }
                        movieAdapter?.notifyDataSetChanged()
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        val movie: Movie? = dataSnapshot.getValue<Movie>(Movie::class.java)
                        if ((movie == null) || (listMovies == null) || listMovies!!.isEmpty() || (movieAdapter == null)) {
                            return
                        }
                        for (movieDelete: Movie? in listMovies!!) {
                            if (movie.getId() == movieDelete?.getId()) {
                                listMovies?.remove(movieDelete)
                                break
                            }
                        }
                        movieAdapter?.notifyDataSetChanged()
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    private fun onClickItemMovie(movie: Movie?) {
        val intent = Intent(activity, PlayMovieActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("object_movie", movie)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun onClickFavoriteMovie(id: Int, favorite: Boolean) {
        val map = mapOf("favorite" to favorite)
        MyApplication[activity]?.getDatabaseReference()
                ?.child(id.toString())?.updateChildren(map)
    }

    override fun onDestroy() {
        super.onDestroy()
        movieAdapter?.release()
    }
}