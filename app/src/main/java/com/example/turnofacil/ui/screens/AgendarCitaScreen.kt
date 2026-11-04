package com.example.turnofacil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.turnofacil.R
import com.example.turnofacil.data.demo.DatosDemo
import com.example.turnofacil.data.model.Servicio
import com.example.turnofacil.data.model.TipoAtencion
import com.example.turnofacil.ui.components.formatearPrecio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    servicios: List<Servicio>,
    onConfirmar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf("") }
    var servicioSeleccionado by remember { mutableStateOf(servicios.firstOrNull()) }
    var menuServicios by remember { mutableStateOf(false) }
    var tipoAtencion by remember { mutableStateOf(TipoAtencion.LOCAL) }
    var adicionales by remember { mutableStateOf(setOf<String>()) }
    var hora by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.agendar_titulo)) }) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(R.string.agendar_nombre)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Selección de servicio
                Text(
                    text = stringResource(R.string.agendar_servicio),
                    style = MaterialTheme.typography.titleSmall,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { menuServicios = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = servicioSeleccionado?.let {
                                "${it.nombre} — " + stringResource(
                                    R.string.precio_formato,
                                    formatearPrecio(it.precio),
                                )
                            } ?: stringResource(R.string.agendar_seleccionar_servicio),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    DropdownMenu(
                        expanded = menuServicios,
                        onDismissRequest = { menuServicios = false },
                    ) {
                        servicios.forEach { servicio ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${servicio.nombre} — " + stringResource(
                                            R.string.precio_formato,
                                            formatearPrecio(servicio.precio),
                                        ),
                                    )
                                },
                                onClick = {
                                    servicioSeleccionado = servicio
                                    menuServicios = false
                                },
                            )
                        }
                    }
                }

                // Tipo de atención (RadioButton)
                Text(
                    text = stringResource(R.string.agendar_tipo_atencion),
                    style = MaterialTheme.typography.titleSmall,
                )
                Column {
                    FilaRadio(
                        texto = stringResource(R.string.agendar_tipo_local),
                        seleccionado = tipoAtencion == TipoAtencion.LOCAL,
                        onClick = { tipoAtencion = TipoAtencion.LOCAL },
                    )
                    FilaRadio(
                        texto = stringResource(R.string.agendar_tipo_domicilio),
                        seleccionado = tipoAtencion == TipoAtencion.DOMICILIO,
                        onClick = { tipoAtencion = TipoAtencion.DOMICILIO },
                    )
                }

                // Servicios adicionales (Checkbox)
                Text(
                    text = stringResource(R.string.agendar_adicionales),
                    style = MaterialTheme.typography.titleSmall,
                )
                Column {
                    DatosDemo.serviciosAdicionales.forEach { adicional ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = adicionales.contains(adicional),
                                onCheckedChange = { marcado ->
                                    adicionales = if (marcado) {
                                        adicionales + adicional
                                    } else {
                                        adicionales - adicional
                                    }
                                },
                            )
                            Text(adicional)
                        }
                    }
                }

                // Hora (TimePicker)
                Text(
                    text = stringResource(R.string.agendar_hora),
                    style = MaterialTheme.typography.titleSmall,
                )
                SelectorHora(hora = hora, onHoraSeleccionada = { hora = it })

                Spacer(Modifier.height(8.dp))
                Button(onClick = onConfirmar, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.agendar_confirmar))
                }
            }
        }
    }
}

@Composable
private fun FilaRadio(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = seleccionado, onClick = onClick)
        Text(texto)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorHora(
    hora: String,
    onHoraSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val estadoHora = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = false)

    OutlinedButton(
        onClick = { mostrarDialogo = true },
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_schedule),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(hora.ifEmpty { stringResource(R.string.agendar_seleccionar_hora) })
    }

    if (mostrarDialogo) {
        Dialog(onDismissRequest = { mostrarDialogo = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.agendar_hora),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    TimePicker(state = estadoHora)
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text(stringResource(R.string.accion_cancelar))
                        }
                        TextButton(
                            onClick = {
                                onHoraSeleccionada(
                                    String.format("%02d:%02d", estadoHora.hour, estadoHora.minute)
                                )
                                mostrarDialogo = false
                            },
                        ) {
                            Text(stringResource(R.string.accion_aceptar))
                        }
                    }
                }
            }
        }
    }
}
