package com.felipe.pomodoroapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipe.pomodoroapp.model.Configuracion
import com.felipe.pomodoroapp.model.ConfiguracionViewModel
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PantallaConfiguracion(
    onBackPressed: () -> Unit
) {
    val viewModel: ConfiguracionViewModel = viewModel()
    val config by viewModel.configuracion.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Configuracion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = config.minutosTrabajo.toString(),
            onValueChange = { nuevoValor ->
                nuevoValor.toIntOrNull()?.let {
                    viewModel.actualizarMinutosTrabajo(it)
                }
            },
            label = { Text("Minutos de trabajo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = config.minutosDescansoCorto.toString(),
            onValueChange = { nuevoValor ->
                nuevoValor.toIntOrNull()?.let {
                    viewModel.actualizarMinutosDescansoCorto(it)
                }
            },
            label = { Text("Minutos de descanso corto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = config.minutosDescansoLargo.toString(),
            onValueChange = { nuevoValor ->
                nuevoValor.toIntOrNull()?.let {
                    viewModel.actualizarMinutosDescansoLargo(it)
                }
            },
            label = { Text("Minutos de descanso largo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar y volver")
        }
    }
}