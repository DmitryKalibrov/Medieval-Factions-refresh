package com.dansplugins.factionsystem.team

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.faction.MfFactionId
import com.dansplugins.factionsystem.player.MfPlayerId
import java.util.concurrent.ConcurrentHashMap

class MfTeamService(private val plugin: MedievalFactions) {

    private val playerTeamById: MutableMap<MfPlayerId, TeamColor> = ConcurrentHashMap()
    private val playerTeamChanges: MutableMap<MfPlayerId, Int> = ConcurrentHashMap()
    private val teamKings: MutableMap<TeamColor, MfPlayerId> = ConcurrentHashMap()
    private val factionTeamById: MutableMap<MfFactionId, TeamColor> = ConcurrentHashMap()

    fun getPlayerTeam(id: MfPlayerId): TeamColor? = playerTeamById[id]

    fun setPlayerTeam(id: MfPlayerId, team: TeamColor, adminOverride: Boolean = false): Boolean {
        if (!adminOverride) {
            val used = playerTeamChanges.getOrDefault(id, 0)
            if (used >= 2) return false
            playerTeamChanges[id] = used + 1
        }
        playerTeamById[id] = team
        return true
    }

    fun setKing(team: TeamColor, playerId: MfPlayerId) {
        teamKings[team] = playerId
    }

    fun getKing(team: TeamColor): MfPlayerId? = teamKings[team]

    fun setFactionTeam(factionId: MfFactionId, team: TeamColor) {
        factionTeamById[factionId] = team
    }

    fun getFactionTeam(factionId: MfFactionId): TeamColor? = factionTeamById[factionId]

    fun getTeamSize(team: TeamColor): Int = playerTeamById.values.count { it == team }
}
