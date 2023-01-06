package com.example.unsplashapitutorial.data

data class UnsplashResponse(
    val results:List<UnsplashPhoto>,
    val total_pages:Int?
)
