package com.example.turnofacil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.turnofacil.R
import com.example.turnofacil.data.model.Servicio
import com.example.turnofacil.ui.components.formatearPrecio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    servicios: List<Servicio>,
    onAgendar: () -> Unit,
    onAdminServicios: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.servicios_titulo)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.servicios_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(onClick = onAgendar, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.servicios_agendar))
                }
                OutlinedButton(onClick = onAdminServicios, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.servicios_admin))
                }
            }

            if (servicios.isEmpty()) {
                Text(stringResource(R.string.servicios_vacio))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(servicios, key = { it.id }) { servicio ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = servicio.nombre,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                },
                                supportingContent = {
                                    Text(stringResource(R.string.servicios_duracion, servicio.duracionMin))
                                },
                                trailingContent = {
                                    Text(
                                        text = stringResource(
                                            R.string.precio_formato,
                                            formatearPrecio(servicio.precio),
                                        ),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
