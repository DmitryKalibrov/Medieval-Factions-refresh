package com.dansplugins.factionsystem.command.faction.admin

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.factionsystem.team.TeamColor
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfFactionAdminTeamCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.admin")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }
        if (args.size < 2) {
            sender.sendMessage("$RED${plugin.language["CommandFactionAdminTeamUsage"]}")
            return true
        }
        val team = TeamColor.fromString(args[0])
        val targetName = args[1]
        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }
        val target = plugin.server.getOfflinePlayer(targetName)
        val playerId = MfPlayerId.fromBukkitPlayer(target)
        val teamService = plugin.teamService
        val ok = teamService.setPlayerTeam(playerId, team, adminOverride = true)
        if (ok) {
            sender.sendMessage("$GREEN${plugin.language["CommandFactionAdminTeamSuccess", targetName, team.name.lowercase()]}")
        } else {
            sender.sendMessage("$RED${plugin.language["CommandFactionAdminTeamFailed"]}")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> = when (args.size) {
        1 -> listOf("red", "blue", "yellow").filter { it.startsWith(args[0].lowercase()) }
        else -> emptyList()
    }
}
