package com.tiendaapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddProductScreen(
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Registrar Producto", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(12.dp))

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

                isLoading = true
                val producto = Producto(
                    nombre = nombre,
                    precio = precioDouble,
                    stock = stockInt,
                    categoria = categoria,
                    userId = user?.uid ?: ""
                )

                db.collection("products")
                    .add(producto.toMap())
                    .addOnSuccessListener {
                        isLoading = false
                        onBack()
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        mensaje = "Error al guardar: ${e.message ?: "desconocido"}"
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator() else Text("Guardar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}
