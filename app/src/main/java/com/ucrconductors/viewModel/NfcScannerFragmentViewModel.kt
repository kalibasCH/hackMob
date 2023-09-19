package com.ucrconductors.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ucrconductors.model.Conductor
import com.ucrconductors.model.Repository
import com.ucrconductors.model.User

class NfcScannerFragmentViewModel(override val repository: Repository) : ViewModel(),
    ViewModelWithRepository {

    private val _user = repository.user
    val user: LiveData<User> = _user
    fun getUserByNumberOfCard(cardNumber: String) {
        repository.fetchUser(cardNumber)
    }
}