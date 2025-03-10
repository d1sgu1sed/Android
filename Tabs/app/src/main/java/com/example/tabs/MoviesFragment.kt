package com.example.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MoviesFragment : Fragment() {

    private val movieList = listOf(
        Movie("Inception", "Sci-Fi", R.drawable.inception),
        Movie("Titanic", "Drama", R.drawable.titanic),
        Movie("Interstellar", "Sci-Fi", R.drawable.interstellar),
        Movie("The Dark Knight", "Action", R.drawable.dark_knight)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_movies, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MovieAdapter(movieList)
        return view
    }
}
