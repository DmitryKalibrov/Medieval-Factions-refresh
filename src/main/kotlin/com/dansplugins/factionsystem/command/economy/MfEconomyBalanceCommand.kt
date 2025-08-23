package com.dansplugins.factionsystem.command.economy

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfEconomyBalanceCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.economy.balance")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        val targetPlayer = if (args.isNotEmpty()) {
            args[0]
        } else {
            if (sender.name == null) {
                sender.sendMessage("$RED${plugin.language["CommandEconomyBalanceNotAPlayer"]}")
                return true
            }
            sender.name
        }

        val playerId = MfPlayerId(targetPlayer)
        val balance = plugin.economyService.getBalance(playerId)

        if (args.isNotEmpty() && sender.name != targetPlayer) {
            // Просмотр баланса другого игрока
            if (!sender.hasPermission("mf.economy.balance.other")) {
                sender.sendMessage("$RED${plugin.language["NoPermission"]}")
                return true
            }
            sender.sendMessage("$GREEN${plugin.language["CommandEconomyBalanceOther", targetPlayer, balance.toString()]}")
        } else {
            // Просмотр собственного баланса
            sender.sendMessage("$GREEN${plugin.language["CommandEconomyBalanceSelf", balance.toString()]}")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return if (args.size == 1 && sender.hasPermission("mf.economy.balance.other")) {
            plugin.server.onlinePlayers
                .map { it.name }
                .filter { it.startsWith(args[0]) }
                .sorted()
        } else {
            emptyList()
        }
    }
}
