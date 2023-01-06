package com.example.unsplashapitutorial.ui.gallery


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.unsplashapitutorial.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val unsplashRepository: UnsplashRepository) :
    ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)


    val photos = currentQuery.switchMap {
        unsplashRepository.getSearchResult(it).cachedIn(viewModelScope)
    }





    companion object {
        const val DEFAULT_QUERY = "iphone"
    }

    fun getSearchResult(query: String) {
        currentQuery.value = query
    }
}