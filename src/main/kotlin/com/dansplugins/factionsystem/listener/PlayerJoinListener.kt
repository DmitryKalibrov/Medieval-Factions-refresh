package com.dansplugins.factionsystem.listener

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.item.TeamSelectionFlag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: MedievalFactions) : Listener {

    private val teamSelectionFlag = TeamSelectionFlag(plugin)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Выдаем флаг выбора команды новым игрокам
        teamSelectionFlag.giveFlagToPlayerOnJoin(event.player)
    }
}
