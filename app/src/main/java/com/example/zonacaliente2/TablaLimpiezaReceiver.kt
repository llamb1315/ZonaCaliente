package com.example.zonacaliente2


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.zonacaliente2.MainActivity // Asegúrate de que esto coincida con tu paquete

class TablaLimpiezaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Alarm", "TablaLimpiezaReceiver.onReceive() llamado")

        // Enviar un Intent a MainActivity para que limpie la tabla
        val limpiezaIntent = Intent(context, MainActivity::class.java)
        limpiezaIntent.action = "LIMPIAR_TABLA"
        limpiezaIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(limpiezaIntent)
    }
}