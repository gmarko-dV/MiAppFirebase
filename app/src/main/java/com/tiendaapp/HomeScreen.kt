package com.tiendaapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userEmail = user?.email ?: "Usuario"
    val db = FirebaseFirestore.getInstance()

    // Estados del formulario
    var codigo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esExito by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrar Producto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = codigo,
            onValueChange = { 
                codigo = it
                mensaje = null
            },
            label = { Text("Código") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            enabled = !isLoading
        )

        OutlinedTextField(
            value = cantidad,
            onValueChange = { 
                cantidad = it
                mensaje = null
            },
            label = { Text("Cantidad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            enabled = !isLoading
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { 
                categoria = it
                mensaje = null
            },
            label = { Text("Categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            enabled = !isLoading
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { 
                descripcion = it
                mensaje = null
            },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            minLines = 3,
            enabled = !isLoading
        )

        mensaje?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (esExito) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = msg,
                    color = if (esExito) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Button(
            onClick = {
                if (codigo.isBlank() || cantidad.isBlank() || categoria.isBlank() || descripcion.isBlank()) {
                    mensaje = "Por favor completa todos los campos"
                    esExito = false
                    return@Button
                }

                val cantidadInt = cantidad.toIntOrNull()
                if (cantidadInt == null || cantidadInt < 0) {
                    mensaje = "La cantidad debe ser un número válido"
                    esExito = false
                    return@Button
                }

                isLoading = true
                mensaje = null

                val producto = Producto(
                    nombre = if (descripcion.isNotBlank()) "$codigo - $descripcion" else codigo,
                    stock = cantidadInt,
                    categoria = categoria,
                    userId = user?.uid ?: ""
                )

                Log.d("HomeScreen", "Intentando registrar producto: ${producto.nombre}")

                try {
                    db.collection("productos")
                        .add(producto.toMap())
                        .addOnSuccessListener { documentReference ->
                            Log.d("HomeScreen", "Producto registrado con ID: ${documentReference.id}")
                            isLoading = false
                            mensaje = "Producto registrado exitosamente"
                            esExito = true
                            // Limpiar formulario
                            codigo = ""
                            cantidad = ""
                            categoria = ""
                            descripcion = ""
                        }
                        .addOnFailureListener { e ->
                            Log.e("HomeScreen", "Error al registrar producto", e)
                            isLoading = false
                            val errorMsg = when {
                                e.message?.contains("PERMISSION_DENIED") == true -> 
                                    "Error: No tienes permiso. Verifica las reglas de Firestore en Firebase Console"
                                e.message?.contains("UNAVAILABLE") == true -> 
                                    "Error: Firestore no está disponible. Verifica tu conexión a internet"
                                e.message?.contains("NOT_FOUND") == true -> 
                                    "Error: Firestore no está habilitado. Habilítalo en Firebase Console"
                                else -> "Error al registrar producto: ${e.message ?: "Error desconocido"}"
                            }
                            mensaje = errorMsg
                            esExito = false
                        }
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Excepción al registrar producto", e)
                    isLoading = false
                    mensaje = "Error: ${e.message ?: "Error desconocido"}"
                    esExito = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrar Producto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            Text("Cerrar Sesión")
        }
    }
}

