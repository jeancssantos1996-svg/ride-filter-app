package com.ridefilter.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object RideManager {
    val rideList = mutableListOf<String>()
    var onUpdate: (() -> Unit)? = null

    fun addRide(ride: String) {
        rideList.add(0, ride) // Adiciona no topo
        onUpdate?.invoke()
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPermission = findViewById<Button>(R.id.btnPermission)
        listView = findViewById(R.id.lvRides)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPermission = findViewById<Button>(R.id.btnPermission)
        listView = findViewById(R.id.lvRides)

        // Custom Adapter para usar o layout bonito
        adapter = object : ArrayAdapter<String>(this, R.layout.item_ride, RideManager.rideList) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = convertView ?: android.view.LayoutInflater.from(this@MainActivity).inflate(R.layout.item_ride, parent, false)
                val rideString = getItem(position) ?: ""

                // Parse simples da string para preencher o layout
                val parts = rideString.split(" | ")
                if (parts.size >= 3) {
                    view.findViewById<android.widget.TextView>(R.id.tvApp).text = parts[0]
                    view.findViewById<android.widget.TextView>(R.id.tvValue).text = parts[1]
                    view.findViewById<android.widget.TextView>(R.id.tvDistance).text = parts[2]
                }
                return view
            }
        }
        listView.adapter = adapter

        btnPermission.setOnClickListener {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Ative o RideFilter na lista", Toast.LENGTH_LONG).show()
        }

        // Atualiza a lista quando chegar nova corrida
        RideManager.onUpdate = {
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }
}
