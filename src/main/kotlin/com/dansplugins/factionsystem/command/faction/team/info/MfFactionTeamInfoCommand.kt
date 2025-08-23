package com.dansplugins.factionsystem.command.faction.team.info

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.factionsystem.team.TeamColor
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.ChatColor.YELLOW
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfFactionTeamInfoCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.team.info")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        val team = if (args.isNotEmpty()) {
            TeamColor.fromString(args[0])
        } else {
            // Если команда не указана, показываем команду игрока
            val playerId = MfPlayerId(sender.name)
            plugin.teamService.getPlayerTeam(playerId)
        }

        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }

        showTeamInfo(sender, team)
        return true
    }

    private fun showTeamInfo(sender: CommandSender, team: TeamColor) {
        val teamName = when (team) {
            TeamColor.RED -> "Красные"
            TeamColor.BLUE -> "Синие"
            TeamColor.YELLOW -> "Жёлтые (Авантюристы)"
        }

        val teamColor = when (team) {
            TeamColor.RED -> RED
            TeamColor.BLUE -> AQUA
            TeamColor.YELLOW -> YELLOW
        }

        val teamSize = plugin.teamService.getTeamSize(team)
        val king = plugin.teamService.getKing(team)

        sender.sendMessage("$teamColor=== $teamName ===")
        sender.sendMessage("$GREEN" + "Members: $teamSize")

        if (king != null && (team == TeamColor.RED || team == TeamColor.BLUE)) {
            val kingPlayer = king.toBukkitPlayer()
            val kingName = kingPlayer?.name ?: "Unknown"
            sender.sendMessage("$GREEN" + "King: $kingName")
        } else if (team == TeamColor.YELLOW) {
            sender.sendMessage("$GREEN" + "Status: Adventurers (no team)")
            sender.sendMessage("$GREEN" + "Capabilities:")
            sender.sendMessage("$GREEN" + "• Fight on any team's side")
            sender.sendMessage("$GREEN" + "• Free relationships with other players")
            sender.sendMessage("$GREEN" + "• Independence from team restrictions")
        }

        // Show player's current team
        val playerId = MfPlayerId(sender.name)
        val playerTeam = plugin.teamService.getPlayerTeam(playerId)
        if (playerTeam == team) {
            sender.sendMessage("$GREEN" + "You are in this team!")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> =
        if (args.size == 1) {
            listOf("red", "blue", "yellow").filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
        }
}
