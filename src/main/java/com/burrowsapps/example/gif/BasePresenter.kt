package com.burrowsapps.example.gif

interface BasePresenter<in T> {
    fun takeView(view: T)
    fun dropView()
}
