package com.example.movie.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movie.MyApplication
import com.example.movie.R
import com.example.movie.activity.PlayMovieActivity
import com.example.movie.adapter.MovieAdapter
import com.example.movie.model.Movie
import com.example.movie.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.github.rupinderjeet.kprogresshud.KProgressHUD
import java.util.*

class HomeFragment : Fragment() {

    private var mView: View? = null
    private var progressHUD: KProgressHUD? = null
    private var rcvHome: RecyclerView? = null
    private var listMovies: MutableList<Movie?>? = null
    private var movieAdapter: MovieAdapter? = null
    private var imgSearch: ImageView? = null
    private var edtSearchName: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        rcvHome = mView?.findViewById(R.id.rcv_home)
        edtSearchName = mView?.findViewById<EditText?>(R.id.edt_search_name)
        imgSearch = mView?.findViewById<ImageView?>(R.id.img_search)
        progressHUD = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        rcvHome?.layoutManager = gridLayoutManager
        listMovies = ArrayList()
        movieAdapter = MovieAdapter(listMovies, activity, object : MovieAdapter.IClickItemListener {
            override fun onClickItem(movie: Movie?) {
                onClickItemMovie(movie)
            }

            override fun onClickFavorite(id: Int, favorite: Boolean) {
                onClickFavoriteMovie(id, favorite)
            }
        })
        rcvHome?.adapter = movieAdapter
        getListMovies("")
        imgSearch?.setOnClickListener { searchMovie() }
        edtSearchName?.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchMovie()
                    return true
                }
                return false
            }
        })
        edtSearchName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val strKey: String = s.toString().trim { it <= ' ' }
                if ((strKey == "") || strKey.isEmpty()) {
                    listMovies?.clear()
                    getListMovies("")
                }
            }
        })
        return mView
    }

    private fun searchMovie() {
        val strKey: String = edtSearchName?.text.toString().trim { it <= ' ' }
        listMovies?.clear()
        getListMovies(strKey)
        Utils.hideSoftKeyboard(activity)
    }

    private fun getListMovies(key: String?) {
        progressHUD?.show()
        MyApplication[activity]?.getDatabaseReference()
                ?.addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        progressHUD?.dismiss()
                        val movie: Movie? = dataSnapshot.getValue<Movie>(Movie::class.java)
                        if (movie == null || listMovies == null || movieAdapter == null) {
                            return
                        }
                        if (key == null || (key == "")) {
                            listMovies?.add(0, movie)
                        } else {
                            if (movie.getTitle()!!.trim().lowercase(Locale.getDefault())
                                            .contains(key.trim().lowercase(Locale.getDefault()))) {
                                listMovies?.add(0, movie)
                            }
                        }
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
                                movieEntity.setImage(movie.getImage())
                                movieEntity.setTitle(movie.getTitle())
                                movieEntity.setUrl(movie.getUrl())
                                movieEntity.setFavorite(movie.isFavorite())
                                movieEntity.setHistory(movie.isHistory())
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