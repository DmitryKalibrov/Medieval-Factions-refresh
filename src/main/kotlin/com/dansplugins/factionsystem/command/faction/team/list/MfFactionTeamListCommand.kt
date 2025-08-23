package com.dansplugins.factionsystem.command.faction.team.list

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

class MfFactionTeamListCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.team.list")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        val team = if (args.isNotEmpty()) {
            TeamColor.fromString(args[0])
        } else {
            // Если команда не указана, показываем все команды
            showAllTeams(sender)
            return true
        }

        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }

        showTeamMembers(sender, team)
        return true
    }

    private fun showAllTeams(sender: CommandSender) {
        sender.sendMessage("$AQUA=== Команды ===")

        for (team in TeamColor.values()) {
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

            sender.sendMessage("$teamColor$teamName: $teamSize members")
            if (king != null && (team == TeamColor.RED || team == TeamColor.BLUE)) {
                val kingPlayer = king.toBukkitPlayer()
                val kingName = kingPlayer?.name ?: "Unknown"
                sender.sendMessage("$GREEN" + "  King: $kingName")
            }
        }
    }

    private fun showTeamMembers(sender: CommandSender, team: TeamColor) {
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

        val king = plugin.teamService.getKing(team)
        val members = plugin.server.onlinePlayers
            .filter { player ->
                val playerId = MfPlayerId(player.uniqueId.toString())
                plugin.teamService.getPlayerTeam(playerId) == team
            }
            .map { it.name }
            .sorted()

        sender.sendMessage("$teamColor=== $teamName ===")
        sender.sendMessage("$GREEN" + "Members: ${members.size}")

        if (king != null && (team == TeamColor.RED || team == TeamColor.BLUE)) {
            val kingPlayer = king.toBukkitPlayer()
            val kingName = kingPlayer?.name ?: "Unknown"
            sender.sendMessage("$GREEN" + "King: $kingName")
        }

        if (members.isNotEmpty()) {
            sender.sendMessage("$GREEN" + "Members:")
            members.forEach { memberName ->
                val isKing = king?.toBukkitPlayer()?.name == memberName
                val prefix = if (isKing) "$GREEN" + "👑 " else "$GREEN" + "• "
                sender.sendMessage("$prefix$memberName")
            }
        } else {
            sender.sendMessage("$GREEN" + "No members in team")
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> =
        if (args.size == 1) {
            listOf("red", "blue", "yellow").filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
        }
}
