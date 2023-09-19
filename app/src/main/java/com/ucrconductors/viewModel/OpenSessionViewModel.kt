package com.ucrconductors.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ucrconductors.model.Conductor
import com.ucrconductors.model.Repository
import com.ucrconductors.model.Transport

class OpenSessionViewModel(override val repository: Repository) : ViewModel(), ViewModelWithRepository {

    private val _conductor = repository.conductor
    val conductor: LiveData<Conductor?> = _conductor

    fun postTransportToServer(transport: Transport) {
        repository.postTransportToServer(transport)
    }
    fun removeConductor() {
        repository.removeConductor()
    }

}