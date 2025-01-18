import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhasib.snooq.mvvm.AppWriteRepository
import kotlinx.coroutines.launch

class AppWriteViewModel(private val appWriteRepository: AppWriteRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>() // LiveData to observe login result
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun emailVerification(email: String) {
        viewModelScope.launch {
            appWriteRepository.sendEmail(email)
        }
    }

    fun logginUser(secretCode: String) {
        viewModelScope.launch {
            val isLoginSuccessful = appWriteRepository.loginUser(secretCode)
            _loginResult.postValue(isLoginSuccessful) // Post result to LiveData
        }
    }
}
