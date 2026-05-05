package com.felipe.pomodoroapp.model

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracion")

class ConfiguracionViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val KEY_MINUTOS_TRABAJO = intPreferencesKey("minutos_trabajo")
        private val KEY_MINUTOS_DESCANSO_CORTO = intPreferencesKey("minutos_descanso_corto")
        private val KEY_MINUTOS_DESCANSO_LARGO = intPreferencesKey("minutos_descanso_largo")
    }

    // Obtenemos el contexto de la aplicacion (valido mientras la app viva)
    private val appContext = getApplication<Application>().applicationContext

    private val _configuracion = MutableStateFlow(Configuracion())
    val configuracion: StateFlow<Configuracion> = _configuracion.asStateFlow()

    init {
        cargarConfiguracion()
    }

    private fun cargarConfiguracion() {
        viewModelScope.launch {
            appContext.dataStore.data.map { preferences ->
                Configuracion(
                    minutosTrabajo = preferences[KEY_MINUTOS_TRABAJO] ?: 25,
                    minutosDescansoCorto = preferences[KEY_MINUTOS_DESCANSO_CORTO] ?: 5,
                    minutosDescansoLargo = preferences[KEY_MINUTOS_DESCANSO_LARGO] ?: 15
                )
            }.collect { config ->
                _configuracion.value = config
            }
        }
    }

    fun actualizarMinutosTrabajo(minutos: Int) {
        viewModelScope.launch {
            appContext.dataStore.edit { preferences ->
                preferences[KEY_MINUTOS_TRABAJO] = minutos
            }
        }
    }

    fun actualizarMinutosDescansoCorto(minutos: Int) {
        viewModelScope.launch {
            appContext.dataStore.edit { preferences ->
                preferences[KEY_MINUTOS_DESCANSO_CORTO] = minutos
            }
        }
    }

    fun actualizarMinutosDescansoLargo(minutos: Int) {
        viewModelScope.launch {
            appContext.dataStore.edit { preferences ->
                preferences[KEY_MINUTOS_DESCANSO_LARGO] = minutos
            }
        }
    }
}