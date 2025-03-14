package com.example.zonacaliente2

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.os.Handler
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import java.util.concurrent.TimeUnit
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Message
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.serialization.json.Json
import java.util.Calendar
import kotlinx.serialization.Serializable

@Serializable
data class Mensaje(
    val type: String,
    val tag: String,
    val data: String,
    val hour: String
)

class MainActivity : FragmentActivity(), WebSocketEventListener  {

    private lateinit var webSocket: WebSocketManager

    private lateinit var tablaDatos: TableLayout
    private val handler = Handler()

    private lateinit var modelos: List<TextView>
    private lateinit var defectos: List<TextView>
    private lateinit var eficiencia: TextView
    private val mapa = Defectos.map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tablaDatos = findViewById(R.id.tabla_datos)
        eficiencia = findViewById(R.id.eficiencia)

        modelos=listOf(findViewById(R.id.M1),findViewById(R.id.M2),findViewById(R.id.M3),findViewById(R.id.M4),findViewById(R.id.M5),findViewById(R.id.M6))
        defectos= listOf(findViewById(R.id.D1),findViewById(R.id.D2),findViewById(R.id.D3),findViewById(R.id.D4),findViewById(R.id.D5),findViewById(R.id.D6))

        webSocket = WebSocketManager(this)
        webSocket.connect()

        // Programa las horas de limpieza
        verificarHora()
    }





    fun mostrarMensaje(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()

        val color = ContextCompat.getColor(this, R.color.primary)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
    }


    fun mostrarMensaje(
        titulo: String? = null,
        mensaje: String,
        botonAceptar: Pair<String, () -> Unit>? = null,
        botonCancelar: Pair<String, () -> Unit>? = null
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)

        if(botonAceptar != null) {
            builder.setPositiveButton(botonAceptar.first) { dialog, _ ->
                botonAceptar.second() // Ejecuta la función asociada
                dialog.dismiss()
            }
        }

        if(botonCancelar != null) {
            builder.setNegativeButton(botonCancelar.first) { dialog, _ ->
                botonCancelar.second() // Ejecuta la función asociada
                dialog.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
        if(botonCancelar != null){
            alertDialog.setCancelable(false)
        }

        val colorAceptar = ContextCompat.getColor(this, R.color.primary)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(colorAceptar)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
    }

    private fun verificarHora() {
        handler.postDelayed({
            val calendar = Calendar.getInstance()
            val minutos = calendar.get(Calendar.MINUTE)

            if (minutos == 0) {
                Log.d("Hora", "Es una hora en punto, limpiando tabla")
                limpiarTabla()
                eficiencia.text = ""
            }

            verificarHora() // Verifica nuevamente en el siguiente minuto
        }, 60000) // 60000 milisegundos = 1 minuto
    }


    private fun limpiarTabla() {
        Log.d("Hora", "limpiarTabla() llamado")

        val filas = tablaDatos.childCount
        val columnas = if (filas > 0) (tablaDatos.getChildAt(0) as TableRow).childCount else 0

        for (i in 0 until filas) {
            val fila = tablaDatos.getChildAt(i) as TableRow
            for (j in 0 until columnas) {
                if (i != 0 && j != 0) { // Excluye la primera fila y la primera columna
                    val columna = fila.getChildAt(j) as? TextView
                    columna?.text = ""
                }
            }
        }
    }

    private fun agregarFilasEncabezado() {
        val encabezado = TableRow(this)

        val seccion = TextView(this)
        seccion.text = "Sección"
        encabezado.addView(seccion)

        for (i in 1..6) {
            val columna = TextView(this)
            columna.text = i.toString()
            encabezado.addView(columna)
        }

        tablaDatos.addView(encabezado)
    }


    override fun onConnectionFailed(reason: String) {
        runOnUiThread {
            mostrarMensaje(
                titulo = "Error",
                mensaje = "Hubo un error conectandose con el servidor: \n $reason",
                botonAceptar = null,
                botonCancelar = "Cerrar app" to { finishAffinity() }
            )
        }
    }

    override fun onMessageReceived(message: String) {
        runOnUiThread {
            val miObjeto = Json.decodeFromString<Mensaje>(message)
            if (miObjeto.type == "molde") {
                modelos[miObjeto.tag.last().toString().toInt() - 1].text = miObjeto.data
            }
            if (miObjeto.hour.toInt() == Calendar.getInstance()[Calendar.HOUR]) {
                if (miObjeto.type == "defecto") {
                    defectos[miObjeto.tag.last().toString().toInt() - 1].text = mapa[miObjeto.data]

                }
                 else if (miObjeto.type == "eficiencia") {
                    eficiencia.text = miObjeto.data
                }
            }
        }
    }

}