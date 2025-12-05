package com.tiendaapp

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
fun ProductListScreen(
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Listener en tiempo real
    DisposableEffect(user?.uid) {
        val listener = db.collection("products")
            .whereEqualTo("userId", user?.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    error = "Error al cargar productos: ${e.message}"
                    isLoading = false
                    return@addSnapshotListener
                }
                productos = snapshots?.documents?.map { doc ->
                    Producto(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        stock = (doc.getLong("stock") ?: 0L).toInt(),
                        categoria = doc.getString("categoria") ?: "",
                        userId = user?.uid ?: ""
                    )
                } ?: emptyList()
                isLoading = false
            }

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Productos", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onAddProduct, modifier = Modifier.fillMaxWidth()) {
            Text("Agregar Producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        productos.forEach { producto ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Nombre: ${producto.nombre}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Precio: ${producto.precio}")
                    Text(text = "Stock: ${producto.stock}")
                    Text(text = "Categoría: ${producto.categoria}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onEditProduct(producto.id) }) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            db.collection("products").document(producto.id)
                                .delete()
                                .addOnFailureListener { e ->
                                    error = "Error al eliminar: ${e.message}"
                                }
                        }) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            auth.signOut()
            onLogout()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar Sesión")
        }
    }
}
