package com.ucrconductors.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Repository {

    private val _conductor = MutableLiveData<Conductor?>()
    val conductor: LiveData<Conductor?> = _conductor
    fun fetchConductor(login: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fetchedConductor = async { postConductorToServer(login, password) }.await()
            let { _conductor.postValue(fetchedConductor!!) }
        }
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun fetchUser(cardNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fetchedUser = fetchUserFromServer(cardNumber)
            fetchedUser?.let {
                _user.postValue(it)
            }
        }
    }
    private suspend fun fetchUserFromServer(cardNumber: String): User? {
        NetworkService().fetchUserByCardNumber(cardNumber)
        return User(id = "1", lastName = "Иван Иванович")
    }

    private suspend fun postConductorToServer(login: String, password: String): Conductor? {
        return NetworkService().postConductorsSession(login, password)
    }

    fun postTransportToServer(transport: Transport) {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkService().postTransportInfo(transport)
        }
    }

    fun removeConductor() {
        _conductor.postValue(null)
    }

}
