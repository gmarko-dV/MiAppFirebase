package com.tiendaapp

data class Producto(
    val codigo: String = "",
    val cantidad: Int = 0,
    val categoria: String = "",
    val descripcion: String = "",
    val userId: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "codigo" to codigo,
            "cantidad" to cantidad,
            "categoria" to categoria,
            "descripcion" to descripcion,
            "userId" to userId,
            "fechaCreacion" to fechaCreacion
        )
    }
}

