package com.tiendaapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProductScreen(
    productId: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        db.collection("products").document(productId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    nombre = doc.getString("nombre") ?: ""
                    precio = (doc.getDouble("precio") ?: 0.0).toString()
                    stock = ((doc.getLong("stock") ?: 0L).toInt()).toString()
                    categoria = doc.getString("categoria") ?: ""
                } else {
                    mensaje = "Producto no encontrado"
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                mensaje = "Error al cargar: ${e.message ?: "desconocido"}"
                isLoading = false
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Editar Producto", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; mensaje = null },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it; mensaje = null },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it; mensaje = null },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it; mensaje = null },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            mensaje?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        mensaje = "Nombre es obligatorio"
                        return@Button
                    }
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val stockInt = stock.toIntOrNull() ?: 0

                    db.collection("products").document(productId)
                        .update(
                            "nombre", nombre,
                            "precio", precioDouble,
                            "stock", stockInt,
                            "categoria", categoria
                        )
                        .addOnSuccessListener {
                            onBack()
                        }
                        .addOnFailureListener { e ->
                            mensaje = "Error al actualizar: ${e.message ?: "desconocido"}"
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}
