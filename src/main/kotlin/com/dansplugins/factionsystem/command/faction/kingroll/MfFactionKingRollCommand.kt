package com.dansplugins.factionsystem.command.faction.kingroll

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.factionsystem.team.TeamColor
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfFactionKingRollCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.admin")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("$RED${plugin.language["CommandFactionKingRollUsage"]}")
            return true
        }
        val team = TeamColor.fromString(args[0])
        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }

        if (team == TeamColor.YELLOW) {
            sender.sendMessage("$RED${plugin.language["CommandFactionKingRollYellowNotAllowed"]}")
            return true
        }
        val eligible = plugin.server.onlinePlayers
            .map { MfPlayerId(it.uniqueId.toString()) }
            .filter { plugin.teamService.getPlayerTeam(it) == team }
        if (eligible.isEmpty()) {
            sender.sendMessage("$RED${plugin.language["CommandFactionKingRollNoEligible"]}")
            return true
        }
        val winner = eligible.random()
        plugin.teamService.setKing(team, winner)
        sender.sendMessage("$GREEN${plugin.language["CommandFactionKingRollSuccess", team.name.lowercase()]}")
        plugin.server.broadcastMessage("$GREEN${plugin.language["CommandFactionKingRollBroadcast", team.name.lowercase(), winner.toBukkitPlayer().name ?: "?" ]}")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> =
        if (args.size == 1) {
            listOf("red", "blue").filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
        }
}
