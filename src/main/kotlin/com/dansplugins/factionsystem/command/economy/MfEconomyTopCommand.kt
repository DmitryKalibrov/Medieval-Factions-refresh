package com.dansplugins.factionsystem.command.economy

import com.dansplugins.factionsystem.MedievalFactions
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.GOLD
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfEconomyTopCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.economy.top")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        val limit = if (args.isNotEmpty()) {
            try {
                args[0].toInt().coerceIn(1, 50) // Ограничиваем от 1 до 50
            } catch (e: NumberFormatException) {
                sender.sendMessage("$RED${plugin.language["CommandEconomyTopInvalidLimit"]}")
                return true
            }
        } else {
            10 // По умолчанию показываем топ-10
        }

        val topPlayers = plugin.economyService.getTopPlayers(limit)

        if (topPlayers.isEmpty()) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyTopNoData"]}")
            return true
        }

        sender.sendMessage("$AQUA${plugin.language["CommandEconomyTopTitle"]}")
        sender.sendMessage("$GREEN${plugin.language["CommandEconomyTopHeader"]}")

        topPlayers.forEachIndexed { index, playerBalance ->
            val position = index + 1
            val playerName = playerBalance.playerId.value
            val balance = playerBalance.balance

            val positionColor = when (position) {
                1 -> GOLD
                2 -> AQUA
                3 -> GREEN
                else -> GREEN
            }

            val positionSymbol = when (position) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "$position."
            }

            val currency = if (balance == 1.0) plugin.language["EconomyCurrencySingular"] else plugin.language["EconomyCurrency"]
            sender.sendMessage("$positionColor$positionSymbol $playerName: $balance $currency")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return if (args.size == 1) {
            listOf("5", "10", "15", "20", "25", "30", "40", "50")
                .filter { it.startsWith(args[0]) }
        } else {
            emptyList()
        }
    }
}
