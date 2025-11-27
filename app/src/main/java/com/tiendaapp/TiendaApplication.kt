package com.tiendaapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class TiendaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase explícitamente
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("TiendaApp", "Firebase inicializado correctamente")
            } else {
                Log.d("TiendaApp", "Firebase ya estaba inicializado")
            }
            
            // Verificar que Firebase Auth esté disponible
            val auth = FirebaseAuth.getInstance()
            Log.d("TiendaApp", "Firebase Auth disponible: ${auth.app.name}")
        } catch (e: Exception) {
            Log.e("TiendaApp", "Error al inicializar Firebase: ${e.message}", e)
        }
    }
}

