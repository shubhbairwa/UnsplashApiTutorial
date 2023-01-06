package com.example.unsplashapitutorial.ui.gallery

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.unsplashapitutorial.R
import com.example.unsplashapitutorial.adapter.UnsplashLoadStateAdapter
import com.example.unsplashapitutorial.adapter.UnsplashPhotoAdapter
import com.example.unsplashapitutorial.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.

 * create an instance of this fragment.
 */

@AndroidEntryPoint
class GalleryFragment : Fragment(), MenuProvider {
    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)
     //   setHasOptionsMenu(true)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = UnsplashPhotoAdapter()

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashLoadStateAdapter { adapter.retry() },
                footer = UnsplashLoadStateAdapter { adapter.retry() },

                )
        }


        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_gallery, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.getSearchResult(query)
                    searchView.clearFocus()

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }


}