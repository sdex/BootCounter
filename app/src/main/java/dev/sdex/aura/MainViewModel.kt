package dev.sdex.aura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sdex.aura.data.DataStore
import dev.sdex.aura.time.DateTimeFormatter
import dev.sdex.aura.time.DateTimeFormatter.Companion.EVENTS_TIME_FORMAT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BootEvent(
    val date: String,
    val count: Int,
)

class MainViewModel : ViewModel() {

    private val _bootEvents = MutableStateFlow<List<BootEvent>?>(null)
    val bootEvents = _bootEvents.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            _bootEvents.value = getEvents()
        }
    }

    private fun getEvents(): List<BootEvent> {
        val events = DataStore.getBootEvents()
        val dateTimeFormatter = DateTimeFormatter()
        return events.map {
            dateTimeFormatter.format(it, EVENTS_TIME_FORMAT)
        }.groupBy { it }
            .map {
                BootEvent(it.key, it.value.size)
            }
    }
}