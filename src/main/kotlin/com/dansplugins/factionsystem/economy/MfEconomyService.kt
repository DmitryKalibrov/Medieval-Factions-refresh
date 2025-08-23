package com.dansplugins.factionsystem.economy

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level.SEVERE
import kotlin.collections.set

class MfEconomyService(
    private val plugin: MedievalFactions,
    private val economyRepository: MfEconomyRepository
) {

    private val playerBalances: MutableMap<MfPlayerId, Double> = ConcurrentHashMap()

    init {
        plugin.logger.info("Loading economy data...")
        val startTime = System.currentTimeMillis()
        playerBalances.putAll(economyRepository.getPlayerBalances())
        plugin.logger.info("${playerBalances.size} player balances loaded (${System.currentTimeMillis() - startTime}ms)")
    }

    /**
     * Получить баланс игрока
     */
    fun getBalance(playerId: MfPlayerId): Double {
        return playerBalances[playerId] ?: 0.0
    }

    /**
     * Установить баланс игрока
     */
    fun setBalance(playerId: MfPlayerId, amount: Double): Boolean {
        return try {
            val newBalance = amount.coerceAtLeast(0.0)
            economyRepository.setPlayerBalance(playerId, newBalance)
            playerBalances[playerId] = newBalance
            true
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to set player balance: ${e.message}", e)
            false
        }
    }

    /**
     * Добавить деньги к балансу игрока
     */
    fun addMoney(playerId: MfPlayerId, amount: Double): Boolean {
        return try {
            val currentBalance = getBalance(playerId)
            val newBalance = currentBalance + amount
            setBalance(playerId, newBalance)
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to add money: ${e.message}", e)
            false
        }
    }

    /**
     * Снять деньги с баланса игрока
     */
    fun removeMoney(playerId: MfPlayerId, amount: Double): Boolean {
        return try {
            val currentBalance = getBalance(playerId)
            if (currentBalance < amount) {
                return false
            }
            val newBalance = currentBalance - amount
            setBalance(playerId, newBalance)
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to remove money: ${e.message}", e)
            false
        }
    }

    /**
     * Перевести деньги между игроками
     */
    fun transferMoney(fromPlayerId: MfPlayerId, toPlayerId: MfPlayerId, amount: Double): TransferResult? {
        return try {
            if (amount <= 0) {
                return null
            }

            val fromBalance = getBalance(fromPlayerId)
            if (fromBalance < amount) {
                return null
            }

            // Снимаем деньги с отправителя
            if (!removeMoney(fromPlayerId, amount)) {
                return null
            }

            // Добавляем деньги получателю
            if (!addMoney(toPlayerId, amount)) {
                // Если не удалось добавить получателю, возвращаем деньги отправителю
                addMoney(fromPlayerId, amount)
                return null
            }

            TransferResult(getBalance(fromPlayerId), getBalance(toPlayerId))
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to transfer money: ${e.message}", e)
            null
        }
    }

    /**
     * Получить топ игроков по балансу
     */
    fun getTopPlayers(limit: Int = 10): List<PlayerBalance> {
        return playerBalances.entries
            .map { (playerId, balance) -> PlayerBalance(playerId, balance) }
            .sortedByDescending { it.balance }
            .take(limit)
    }

    /**
     * Сохранить все данные в базу
     */
    fun saveAll(): Boolean {
        return try {
            playerBalances.forEach { (playerId, balance) ->
                economyRepository.setPlayerBalance(playerId, balance)
            }
            true
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to save economy data: ${e.message}", e)
            false
        }
    }

    /**
     * Обновить данные из базы
     */
    fun refreshData() {
        try {
            playerBalances.clear()
            playerBalances.putAll(economyRepository.getPlayerBalances())
        } catch (e: Exception) {
            plugin.logger.log(SEVERE, "Failed to refresh economy data: ${e.message}", e)
        }
    }
}

/**
 * Результат перевода денег
 */
data class TransferResult(
    val fromBalance: Double,
    val toBalance: Double
)

/**
 * Баланс игрока для топ-списка
 */
data class PlayerBalance(
    val playerId: MfPlayerId,
    val balance: Double
)
