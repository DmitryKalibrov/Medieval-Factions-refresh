package com.dansplugins.factionsystem.command.faction.disband

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.faction.MfFaction
import com.dansplugins.factionsystem.player.MfPlayer
import dev.forkhandles.result4k.onFailure
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.logging.Level.SEVERE

class MfFactionDisbandCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.disband")) {
            sender.sendMessage("$RED${plugin.language["CommandFactionDisbandNoPermission"]}")
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("$RED${plugin.language["CommandFactionDisbandNotAPlayer"]}")
            return true
        }
        plugin.server.scheduler.runTaskAsynchronously(
            plugin,
            Runnable {
                val playerService = plugin.services.playerService
                val mfPlayer = playerService.getPlayer(sender)
                    ?: playerService.save(MfPlayer(plugin, sender)).onFailure {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandFailedToSavePlayer"]}")
                        plugin.logger.log(SEVERE, "Failed to save player: ${it.reason.message}", it.reason.cause)
                        return@Runnable
                    }

                // handle confirm/cancel args and resolve faction
                val factionService = plugin.services.factionService
                val isConfirm = args.isNotEmpty() && args[0].equals("confirm", ignoreCase = true)
                val isCancel = args.isNotEmpty() && args[0].equals("cancel", ignoreCase = true)
                val specifiedTargetName = if (args.isNotEmpty() && (isConfirm || isCancel)) args.drop(1).joinToString(" ") else if (args.isNotEmpty()) args.joinToString(" ") else null
                val faction: MfFaction?
                if (specifiedTargetName == null || specifiedTargetName.isBlank()) {
                    // attempt to use player's faction
                    faction = factionService.getFaction(mfPlayer.id)

                    if (faction == null) {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandMustBeInAFaction"]}")
                        return@Runnable
                    }

                    val role = faction.getRole(mfPlayer.id)
                    if (role == null || !role.hasPermission(faction, plugin.factionPermissions.disband)) {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandNoFactionPermission"]}")
                        return@Runnable
                    }
                    if (faction.members.size != 1) {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandFactionMustBeEmpty"]}")
                        return@Runnable
                    }
                } else {
                    // attempt to use specified faction
                    if (!sender.hasPermission("mf.disband.others")) {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandOthersNoPermission"]}")
                        return@Runnable
                    }

                    faction = factionService.getFaction(specifiedTargetName)

                    if (faction == null) {
                        sender.sendMessage("$RED${plugin.language["CommandFactionDisbandSpecifiedFactionNotFound"]}")
                        return@Runnable
                    }
                }

                // confirmation step
                if (!isConfirm && !isCancel) {
                    val ui = plugin.services.ui
                    sender.spigot().sendMessage(
                        *ui.confirmPrompt(
                            plugin.language["CommandFactionDisbandConfirmTitle"],
                            plugin.language["ConfirmYes"],
                            "/faction disband confirm${if (specifiedTargetName.isNullOrBlank()) "" else " $specifiedTargetName"}",
                            plugin.language["ConfirmYesHover"],
                            plugin.language["ConfirmNo"],
                            "/faction disband cancel${if (specifiedTargetName.isNullOrBlank()) "" else " $specifiedTargetName"}",
                            plugin.language["ConfirmNoHover"]
                        )
                    )
                    return@Runnable
                }
                if (isCancel) {
                    sender.sendMessage("$RED${plugin.language["ActionCancelled"]}")
                    return@Runnable
                }

                // delete faction (confirmed)
                factionService.delete(faction.id).onFailure {
                    sender.sendMessage("$RED${plugin.language["CommandFactionDisbandFailedToDeleteFaction"]}")
                    plugin.logger.log(SEVERE, "Failed to delete faction: ${it.reason.message}", it.reason.cause)
                    return@Runnable
                }
                sender.sendMessage("$GREEN${plugin.language["CommandFactionDisbandSuccess"]}")

                val mapService = plugin.services.mapService
                if (mapService != null && !plugin.config.getBoolean("dynmap.onlyRenderTerritoriesUponStartup")) {
                    plugin.server.scheduler.runTask(
                        plugin,
                        Runnable {
                            mapService.scheduleUpdateClaims(faction)
                        }
                    )
                }
            }
        )
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        val factionService = plugin.services.factionService
        return when {
            args.isEmpty() -> factionService.factions.map(MfFaction::name)
            args.size == 1 ->
                factionService.factions
                    .filter { it.name.lowercase().startsWith(args[0].lowercase()) }
                    .map(MfFaction::name)
            else -> emptyList()
        }
    }
}
