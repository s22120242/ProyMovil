package com.example.turnofacil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ServiciosViewModel(
    servicioRepositorio: ServicioRepositorio,
) : ViewModel() {

    val uiState: StateFlow<ServiciosUiState> = servicioRepositorio
        .observarServicios()
        .map { lista ->
            // Filtra solo servicios activos
            val activos = lista.filter { it.activo }
            ServiciosUiState(servicios = activos, cargando = false, error = null)
        }
        .catch { throwable ->
            emit(ServiciosUiState(servicios = emptyList(), cargando = false, error = throwable.message ?: "Error al cargar servicios"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ServiciosUiState(cargando = true),
        )
}
