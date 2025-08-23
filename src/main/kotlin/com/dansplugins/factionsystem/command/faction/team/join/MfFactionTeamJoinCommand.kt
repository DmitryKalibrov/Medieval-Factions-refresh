package com.dansplugins.factionsystem.command.faction.team.join

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.factionsystem.team.TeamColor
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfFactionTeamJoinCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.team.join")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamJoinUsage"]}")
            return true
        }

        val team = TeamColor.fromString(args[0])
        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }

        val playerId = MfPlayerId(sender.name)
        val currentTeam = plugin.teamService.getPlayerTeam(playerId)

        if (currentTeam == team) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamAlreadyInTeam"]}")
            return true
        }

        // Проверяем лимит смен команд (максимум 2 раза)
        val success = plugin.teamService.setPlayerTeam(playerId, team)
        if (!success) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamChangeLimit"]}")
            return true
        }

        val teamName = when (team) {
            TeamColor.RED -> "Красные"
            TeamColor.BLUE -> "Синие"
            TeamColor.YELLOW -> "Жёлтые (Авантюристы)"
        }

        sender.sendMessage("$GREEN${plugin.language["CommandFactionTeamJoinSuccess", teamName]}")

        // Уведомляем о смене команды
        if (currentTeam != null) {
            val currentTeamName = when (currentTeam) {
                TeamColor.RED -> "Красных"
                TeamColor.BLUE -> "Синих"
                TeamColor.YELLOW -> "Жёлтых"
            }
            plugin.server.broadcastMessage("$GREEN${plugin.language["CommandFactionTeamJoinBroadcast", sender.name, currentTeamName, teamName]}")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> =
        if (args.size == 1) {
            listOf("red", "blue", "yellow").filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
        }
}
