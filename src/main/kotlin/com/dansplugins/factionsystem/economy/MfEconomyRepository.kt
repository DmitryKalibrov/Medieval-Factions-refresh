package com.dansplugins.factionsystem.economy

import com.dansplugins.factionsystem.player.MfPlayerId

interface MfEconomyRepository {
    /**
     * Получить балансы всех игроков
     */
    fun getPlayerBalances(): Map<MfPlayerId, Double>

    /**
     * Установить баланс игрока
     */
    fun setPlayerBalance(playerId: MfPlayerId, balance: Double): Double

    /**
     * Получить баланс конкретного игрока
     */
    fun getPlayerBalance(playerId: MfPlayerId): Double?
}
