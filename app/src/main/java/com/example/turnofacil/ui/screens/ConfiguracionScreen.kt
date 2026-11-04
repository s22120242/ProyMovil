package com.example.turnofacil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.turnofacil.R
import com.example.turnofacil.data.prefs.PreferenciasApp
import com.example.turnofacil.data.prefs.buscarActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var idioma by remember { mutableStateOf(PreferenciasApp.idioma(context)) }
    var horaApertura by remember { mutableStateOf("09:00") }
    var horaCierre by remember { mutableStateOf("19:00") }
    var syncAutomatica by remember { mutableStateOf(true) }

    val cambiarIdioma: (String) -> Unit = { nuevoIdioma ->
        if (nuevoIdioma != idioma) {
            PreferenciasApp.establecerIdioma(context, nuevoIdioma)
            idioma = nuevoIdioma
            // Reinicia la Activity para que se apliquen los recursos del nuevo idioma.
            context.buscarActivity()?.recreate()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.config_titulo)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Idioma
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.config_idioma),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    FilaIdioma(
                        texto = stringResource(R.string.config_idioma_sistema),
                        seleccionado = idioma == PreferenciasApp.IDIOMA_SISTEMA,
                        onClick = { cambiarIdioma(PreferenciasApp.IDIOMA_SISTEMA) },
                    )
                    FilaIdioma(
                        texto = stringResource(R.string.config_idioma_es),
                        seleccionado = idioma == PreferenciasApp.IDIOMA_ES,
                        onClick = { cambiarIdioma(PreferenciasApp.IDIOMA_ES) },
                    )
                    FilaIdioma(
                        texto = stringResource(R.string.config_idioma_en),
                        seleccionado = idioma == PreferenciasApp.IDIOMA_EN,
                        onClick = { cambiarIdioma(PreferenciasApp.IDIOMA_EN) },
                    )
                }
            }

            // Horario de atención
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.config_horario),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    CampoHora(
                        etiqueta = stringResource(R.string.config_horario_apertura),
                        valor = horaApertura,
                        onValor = { horaApertura = it },
                    )
                    CampoHora(
                        etiqueta = stringResource(R.string.config_horario_cierre),
                        valor = horaCierre,
                        onValor = { horaCierre = it },
                    )
                }
            }

            // Sincronización
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.config_sync_activada),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Switch(
                            checked = syncAutomatica,
                            onCheckedChange = { syncAutomatica = it },
                        )
                    }
                    Text(
                        text = stringResource(R.string.config_sync_descripcion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider()
                    Text(
                        text = stringResource(R.string.config_ultima_sincronizacion),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaIdioma(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = seleccionado,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = seleccionado, onClick = null)
        Text(texto)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoHora(
    etiqueta: String,
    valor: String,
    onValor: (String) -> Unit,
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val estadoHora = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = true)

    OutlinedButton(onClick = { mostrarDialogo = true }, modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.ic_schedule),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text("$etiqueta: $valor", modifier = Modifier.weight(1f))
    }

    if (mostrarDialogo) {
        Dialog(onDismissRequest = { mostrarDialogo = false }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
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
                                onValor(String.format("%02d:%02d", estadoHora.hour, estadoHora.minute))
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
