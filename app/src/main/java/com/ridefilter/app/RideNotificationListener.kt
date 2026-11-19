package com.ridefilter.app

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class RideNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        Log.d("RideFilter", "Nova notificação recebida de: $packageName")

        if (packageName.contains("uber") || packageName.contains("99app")) {
            Log.d("RideFilter", "Corrida detectada! Título: $title | Texto: $text")
            processRideNotification(packageName, title, text)
        }
    }

    private fun processRideNotification(app: String, title: String, text: String) {
        val rideData = parseRideInfo(app, text)
        if (rideData != null) {
            val displayString = "${rideData.app.takeLast(5).uppercase()} | R$ ${rideData.value} | ${rideData.distance}km | ${rideData.destination}"
            Log.d("RideFilter", "CORRIDA FILTRADA: $displayString")
            RideManager.addRide(displayString)
        } else {
            Log.d("RideFilter", "Notificação de $app ignorada: $text")
        }
    }

    private fun parseRideInfo(app: String, text: String): RideInfo? {
        try {
            var value = 0.0
            var distance = 0.0
            var destination = "Desconhecido"

            if (app.contains("uber")) {
                // Exemplo Uber: "Nova oferta: R$ 12,50 • 1.2 km • Centro"
                val parts = text.split("•")
                if (parts.size >= 2) {
                    value = extractValue(parts[0])
                    distance = extractDistance(parts[1])
                    if (parts.size >= 3) destination = parts[2].trim()
                }
            } else if (app.contains("99app")) {
                // Exemplo 99: "Ganhe R$ 15,00 | 2.5km | Bairro X"
                val parts = text.split("|")
                if (parts.size >= 2) {
                    value = extractValue(parts[0])
                    distance = extractDistance(parts[1])
                    if (parts.size >= 3) destination = parts[2].trim()
                }
            }

            if (value == 0.0) return null // Não é uma oferta de valor
            return RideInfo(app, value, distance, destination)
        } catch (e: Exception) {
            Log.e("RideFilter", "Erro ao parsear: ${e.message}")
            return null
        }
    }

    private fun extractValue(text: String): Double {
        val regex = "R\\s?\\$\\s?([0-9]+[,.][0-9]{2})".toRegex()
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
    }

    private fun extractDistance(text: String): Double {
        val regex = "([0-9]+[,.][0-9]*)\\s?km".toRegex()
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
    }

    data class RideInfo(val app: String, val value: Double, val distance: Double, val destination: String)
}

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d("RideFilter", "Notificação removida: ${sbn.packageName}")
    }
}
