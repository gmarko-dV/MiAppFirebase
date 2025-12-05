package com.tiendaapp

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val categoria: String = "",
    val userId: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nombre" to nombre,
            "precio" to precio,
            "stock" to stock,
            "categoria" to categoria,
            "userId" to userId
        )
    }
}
