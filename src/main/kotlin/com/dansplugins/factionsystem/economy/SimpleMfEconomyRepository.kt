package com.dansplugins.factionsystem.economy

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class SimpleMfEconomyRepository(
    private val plugin: MedievalFactions
) : MfEconomyRepository {

    private val dataFile = File(plugin.dataFolder, "economy.json")
    private val balances = ConcurrentHashMap<MfPlayerId, Double>()

    init {
        loadData()
    }

    private fun loadData() {
        try {
            if (dataFile.exists()) {
                val json = dataFile.readText()
                val data = plugin.gson.fromJson(json, Map::class.java)
                balances.clear()
                data.forEach { (key, value) ->
                    val playerId = MfPlayerId(key.toString())
                    val balance = (value as? Number)?.toDouble() ?: 0.0
                    balances[playerId] = balance
                }
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to load economy data: ${e.message}")
        }
    }

    private fun saveData() {
        try {
            dataFile.parentFile?.mkdirs()
            val json = plugin.gson.toJson(balances.mapKeys { it.key.value })
            dataFile.writeText(json)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to save economy data: ${e.message}")
        }
    }

    override fun getPlayerBalances(): Map<MfPlayerId, Double> {
        return balances.toMap()
    }

    override fun setPlayerBalance(playerId: MfPlayerId, balance: Double): Double {
        balances[playerId] = balance
        saveData()
        return balance
    }

    override fun getPlayerBalance(playerId: MfPlayerId): Double? {
        return balances[playerId]
    }
}
