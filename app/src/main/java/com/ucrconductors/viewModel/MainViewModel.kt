package com.ucrconductors.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ucrconductors.model.Conductor
import com.ucrconductors.model.Repository

class MainViewModel(override val repository: Repository) : ViewModel(), ViewModelWithRepository {

    private val _conductor = repository.conductor
    val conductor: LiveData<Conductor?> = _conductor

    private val textLoginMutable = MutableLiveData<String>()
    val textLogin: LiveData<String> = textLoginMutable

    private val textPasswordMutable = MutableLiveData<String>()
    val textPassword: LiveData<String> = textPasswordMutable

    fun checkConductorInDb(login: String, password: String) {
        repository.fetchConductor(login, password)
    }

    fun setLoginText(text: String) {
        textLoginMutable.value = text
    }

    fun setPasswordText(text: String) {
        textPasswordMutable.value = text
    }

    fun isConductorPresent(): LiveData<Boolean> {
        val isPresent = MutableLiveData<Boolean>()
        conductor.observeForever(object : Observer<Conductor?> {
            override fun onChanged(value: Conductor?) {
                isPresent.value = value != null
                conductor.removeObserver(this)
            }
        })
        return isPresent
    }

}
