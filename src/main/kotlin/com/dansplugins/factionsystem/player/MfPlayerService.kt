package com.dansplugins.factionsystem.player

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.failure.OptimisticLockingFailureException
import com.dansplugins.factionsystem.failure.ServiceFailure
import com.dansplugins.factionsystem.failure.ServiceFailureType
import com.dansplugins.factionsystem.failure.ServiceFailureType.CONFLICT
import com.dansplugins.factionsystem.failure.ServiceFailureType.GENERAL
import com.dansplugins.factionsystem.team.TeamColor
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.resultFrom
import org.bukkit.OfflinePlayer
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level.SEVERE
import kotlin.collections.set

class MfPlayerService(private val plugin: MedievalFactions, private val playerRepository: MfPlayerRepository) {

    private val playersById: MutableMap<MfPlayerId, MfPlayer> = ConcurrentHashMap()

    init {
        plugin.logger.info("Loading players...")
        val startTime = System.currentTimeMillis()
        playersById.putAll(playerRepository.getPlayers().associateBy(MfPlayer::id))
        val playersToUpdate = playersById.values
            .associateWith(MfPlayer::toBukkit)
            .filter { (mfPlayer, bukkitPlayer) -> bukkitPlayer.name != mfPlayer.name }
        playersToUpdate.forEach { (mfPlayer, bukkitPlayer) ->
            val updatedPlayer = resultFrom {
                playerRepository.upsert(mfPlayer.copy(name = bukkitPlayer.name))
            }.onFailure {
                plugin.logger.log(SEVERE, "Failed to update player: ${it.reason.message}", it.reason.cause)
                return@forEach
            }
            playersById[updatedPlayer.id] = updatedPlayer
        }
        plugin.logger.info("${playersById.size} players loaded (${System.currentTimeMillis() - startTime}ms)")
    }

    @JvmName("getPlayerByPlayerId")
    fun getPlayer(id: MfPlayerId): MfPlayer? {
        return playersById[id]
    }

    @JvmName("getPlayerByBukkitPlayer")
    fun getPlayer(player: OfflinePlayer): MfPlayer? = getPlayer(MfPlayerId(player.uniqueId.toString()))

    fun save(player: MfPlayer): Result4k<MfPlayer, ServiceFailure> = resultFrom {
        val result = playerRepository.upsert(player)
        playersById[result.id] = result
        val mapService = plugin.services.mapService
        if (mapService != null) {
            val factionService = plugin.services.factionService
            val faction = factionService.getFaction(result.id)
            if (faction != null && !plugin.config.getBoolean("dynmap.onlyRenderTerritoriesUponStartup")) {
                plugin.server.scheduler.runTask(
                    plugin,
                    Runnable {
                        mapService.scheduleUpdateClaims(faction)
                    }
                )
            }
        }
        return@resultFrom result
    }.mapFailure { exception ->
        ServiceFailure(exception.toServiceFailureType(), "Service error: ${exception.message}", exception)
    }

    @JvmName("updatePlayerPower")
    fun updatePlayerPower(onlinePlayerIds: List<MfPlayerId>): Result4k<Unit, ServiceFailure> {
        return resultFrom {
            // Обычное увеличение силы для всех игроков
            playerRepository.increaseOnlinePlayerPower(onlinePlayerIds)

            // Дополнительный бонус для королей
            applyKingPowerBonus(onlinePlayerIds)

            playerRepository.decreaseOfflinePlayerPower(onlinePlayerIds)
            playersById.putAll(playerRepository.getPlayers().associateBy(MfPlayer::id))
            val mapService = plugin.services.mapService
            if (mapService != null && !plugin.config.getBoolean("dynmap.onlyRenderTerritoriesUponStartup")) {
                val factionService = plugin.services.factionService
                factionService.factions.forEach { faction ->
                    plugin.server.scheduler.runTask(
                        plugin,
                        Runnable {
                            mapService.scheduleUpdateClaims(faction)
                        }
                    )
                }
            }
        }.mapFailure { exception ->
            ServiceFailure(exception.toServiceFailureType(), "Service error: ${exception.message}", exception)
        }
    }

    /**
     * Применяет дополнительный бонус силы для королей команд
     */
    private fun applyKingPowerBonus(onlinePlayerIds: List<MfPlayerId>) {
        val teamService = plugin.teamService

        onlinePlayerIds.forEach { playerId ->
            val player = getPlayer(playerId) ?: return@forEach
            val team = teamService.getPlayerTeam(playerId) ?: return@forEach

            // Проверяем, является ли игрок королем
            val isKing = teamService.getKing(team) == playerId

            if (isKing && (team == TeamColor.RED || team == TeamColor.BLUE)) {
                // Применяем дополнительный бонус для королей (еще одно стандартное увеличение)
                val currentPower = player.power
                val maxPower = plugin.config.getDouble("players.maxPower")
                val minPower = plugin.config.getDouble("players.minPower")
                val hoursToReachMax = plugin.config.getDouble("players.hoursToReachMaxPower")
                val timeIncrementHours = 0.25

                // Рассчитываем дополнительное увеличение силы для короля
                val kingBonusIncrease = calculateKingPowerIncrease(currentPower, maxPower, minPower, hoursToReachMax, timeIncrementHours)

                if (kingBonusIncrease > 0) {
                    val newPower = (currentPower + kingBonusIncrease).coerceAtMost(maxPower).coerceAtLeast(minPower)
                    save(player.copy(power = newPower))
                    plugin.logger.info("King bonus applied to ${player.name}: +${newPower - currentPower} power")
                }
            }
        }
    }

    /**
     * Рассчитывает дополнительное увеличение силы для королей
     */
    private fun calculateKingPowerIncrease(currentPower: Double, maxPower: Double, minPower: Double, hoursToReachMax: Double, timeIncrementHours: Double): Double {
        // Простой бонус: 50% от стандартного увеличения
        val standardIncrease = (maxPower - currentPower) * 0.01 // 1% от оставшейся силы до максимума
        return standardIncrease.coerceAtLeast(0.0)
    }

    private fun Exception.toServiceFailureType(): ServiceFailureType {
        return when (this) {
            is OptimisticLockingFailureException -> CONFLICT
            else -> GENERAL
        }
    }
}
