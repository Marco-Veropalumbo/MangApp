package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class MainViewModelFactory(
    private val repository: Repository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}