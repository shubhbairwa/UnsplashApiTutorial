package com.example.unsplashapitutorial.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashapitutorial.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi, private val query: String
) : PagingSource<Int, UnsplashPhoto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE
        return try {
            val response = unsplashApi.searchPhotos(query, position,params.loadSize)
            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE) null else position - 1,
                nextKey = if (position==response.total_pages) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)

        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}