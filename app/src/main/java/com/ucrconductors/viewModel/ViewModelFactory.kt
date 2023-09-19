package com.ucrconductors.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ucrconductors.model.Repository

interface ViewModelWithRepository {
    val repository: Repository
}

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            val ctor = modelClass.getDeclaredConstructor(Repository::class.java)
            return ctor.newInstance(repository)
        } catch (e: NoSuchMethodException) {
            // Log the exception or handle it as necessary
        }

        if (ViewModelWithRepository::class.java.isAssignableFrom(modelClass)) {
            val viewModel = modelClass.getConstructor(Repository::class.java).newInstance(repository)
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}