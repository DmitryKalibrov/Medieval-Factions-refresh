package com.dansplugins.factionsystem.command.economy

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfEconomyPayCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.economy.pay")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        if (sender.name == null) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPayNotAPlayer"]}")
            return true
        }

        if (args.size != 2) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPayUsage"]}")
            return true
        }

        val targetPlayer = args[0]
        val amountStr = args[1]

        // Проверяем, что игрок не пытается перевести деньги самому себе
        if (sender.name == targetPlayer) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPaySelf"]}")
            return true
        }

        // Парсим сумму
        val amount = try {
            amountStr.toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPayInvalidAmount"]}")
            return true
        }

        if (amount <= 0) {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPayInvalidAmount"]}")
            return true
        }

        val fromPlayerId = MfPlayerId(sender.name)
        val toPlayerId = MfPlayerId(targetPlayer)

        // Выполняем перевод
        val result = plugin.economyService.transferMoney(fromPlayerId, toPlayerId, amount)
        if (result != null) {
            sender.sendMessage("$GREEN${plugin.language["CommandEconomyPaySuccess", amount.toString(), targetPlayer, result.fromBalance.toString()]}")

            // Уведомляем получателя
            val targetPlayerBukkit = plugin.server.getPlayer(targetPlayer)
            targetPlayerBukkit?.sendMessage("$GREEN${plugin.language["CommandEconomyPayReceived", sender.name, amount.toString(), result.toBalance.toString()]}")
        } else {
            sender.sendMessage("$RED${plugin.language["CommandEconomyPayInsufficientFunds"]}")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> {
                plugin.server.onlinePlayers
                    .map { it.name }
                    .filter { it != sender.name && it.startsWith(args[0]) }
                    .sorted()
            }
            2 -> {
                // Можно добавить подсказки по суммам, но пока оставим пустым
                emptyList()
            }
            else -> emptyList()
        }
    }
}
