package com.dansplugins.factionsystem.command.faction.team

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayer
import com.dansplugins.factionsystem.team.TeamColor
import dev.forkhandles.result4k.onFailure
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MfFactionTeamCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("$RED${plugin.language["NotAPlayer"]}")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamUsage"]}")
            return true
        }
        val team = TeamColor.fromString(args[0])
        if (team == null) {
            sender.sendMessage("$RED${plugin.language["CommandFactionTeamInvalid"]}")
            return true
        }
        plugin.server.scheduler.runTaskAsynchronously(
            plugin,
            Runnable {
                val playerService = plugin.services.playerService
                val teamService = plugin.teamService
                val mfPlayer = playerService.getPlayer(sender)
                    ?: playerService.save(
                        MfPlayer(
                            plugin,
                            sender
                        )
                    ).onFailure {
                        sender.sendMessage("$RED${plugin.language["CommandFactionCreateFailedToSavePlayer"]}")
                        return@Runnable
                    }
                val ok = teamService.setPlayerTeam(mfPlayer.id, team)
                plugin.server.scheduler.runTask(
                    plugin,
                    Runnable {
                        if (ok) {
                            sender.sendMessage("$GREEN${plugin.language["CommandFactionTeamSuccess", team.name.lowercase()]}")
                        } else {
                            sender.sendMessage("$RED${plugin.language["CommandFactionTeamLimit"]}")
                        }
                    }
                )
            }
        )
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> =
        if (args.size == 1) {
            listOf("red", "blue", "yellow").filter { it.startsWith(args[0].lowercase()) }
        } else {
            emptyList()
        }
}
