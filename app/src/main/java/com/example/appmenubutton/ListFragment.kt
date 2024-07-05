package com.example.appmenubutton

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment


class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var arrayList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // handle arguments if needed
        }
        setHasOptionsMenu(true) // Enable options menu for this fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Initialize views
        listView = view.findViewById(R.id.lstAlumnos)
        toolbar = view.findViewById(R.id.toolbar)

        // Set toolbar in the hosting activity
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // Populate list items
        val items = resources.getStringArray(R.array.alumnos)
        arrayList = ArrayList()
        arrayList.addAll(items)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = adapter

        // Handle item click
        listView.setOnItemClickListener { parent, view, position, id ->
            val alumno: String = parent.getItemAtPosition(position).toString()
            showAlertDialog("$position: $alumno")
        }

        return view
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Lista de Alumnos")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, which ->
                // Handle OK button click if needed
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu) // Inflate your menu items

        // Find the search item in your menu
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Si el nuevo texto es nulo o vacío, no filtrar
                if (newText.isNullOrBlank()) {
                    adapter.filter.filter("")
                } else {
                    // Filtrar con el texto que incluye espacios
                    adapter.filter.filter(newText.trim())
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater) // Call super after inflating
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}