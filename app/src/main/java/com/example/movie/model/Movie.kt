package com.example.movie.model

import java.io.Serializable

class Movie : Serializable {

    private var id = 0
    private var title: String? = null
    private var image: String? = null
    private var url: String? = null
    private var favorite = false
    private var history = false

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
    }

    fun isFavorite(): Boolean {
        return favorite
    }

    fun setFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    fun isHistory(): Boolean {
        return history
    }

    fun setHistory(history: Boolean) {
        this.history = history
    }
}