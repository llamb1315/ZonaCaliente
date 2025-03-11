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
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import java.util.Calendar
/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    private lateinit var tablaDatos: TableLayout
    private val handler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tablaDatos = findViewById(R.id.tabla_datos)

        // Programa las horas de limpieza
        verificarHora()


    }

    private fun verificarHora() {
        handler.postDelayed({
            val calendar = Calendar.getInstance()
            val minutos = calendar.get(Calendar.MINUTE)

            if (minutos == 42) {
                Log.d("Hora", "Es una hora en punto, limpiando tabla")
                limpiarTabla()
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






}


