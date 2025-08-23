package com.dansplugins.factionsystem.command.economy

import com.dansplugins.factionsystem.MedievalFactions
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MfEconomyCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {

    private val balanceCommand = MfEconomyBalanceCommand(plugin)
    private val payCommand = MfEconomyPayCommand(plugin)
    private val topCommand = MfEconomyTopCommand(plugin)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.economy")) {
            sender.sendMessage("$RED${plugin.language["NoPermission"]}")
            return true
        }

        if (args.isEmpty()) {
            showHelp(sender)
            return true
        }

        val subcommand = args[0].lowercase()
        val subArgs = args.drop(1).toTypedArray()

        return when (subcommand) {
            "balance", "bal" -> balanceCommand.onCommand(sender, command, "balance", subArgs)
            "pay", "send" -> payCommand.onCommand(sender, command, "pay", subArgs)
            "top", "leaderboard" -> topCommand.onCommand(sender, command, "top", subArgs)
            "help" -> {
                showHelp(sender)
                true
            }
            else -> {
                sender.sendMessage("$RED${plugin.language["CommandEconomyUnknownSubcommand"]}")
                showHelp(sender)
                true
            }
        }
    }

    private fun showHelp(sender: CommandSender) {
        sender.sendMessage("$AQUA${plugin.language["CommandEconomyHelpTitle"]}")
        sender.sendMessage("$GREEN${plugin.language["CommandEconomyHelpBalance"]}")
        sender.sendMessage("$GREEN${plugin.language["CommandEconomyHelpPay"]}")
        sender.sendMessage("$GREEN${plugin.language["CommandEconomyHelpTop"]}")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val subcommands = mutableListOf<String>()

            if (sender.hasPermission("mf.economy.balance")) {
                subcommands.addAll(listOf("balance", "bal"))
            }
            if (sender.hasPermission("mf.economy.pay")) {
                subcommands.addAll(listOf("pay", "send"))
            }
            if (sender.hasPermission("mf.economy.top")) {
                subcommands.addAll(listOf("top", "leaderboard"))
            }

            subcommands.add("help")

            return subcommands.filter { it.startsWith(args[0].lowercase()) }
        }

        val subcommand = args[0].lowercase()
        val subArgs = args.drop(1).toTypedArray()

        return when (subcommand) {
            "balance", "bal" -> balanceCommand.onTabComplete(sender, command, "balance", subArgs)
            "pay", "send" -> payCommand.onTabComplete(sender, command, "pay", subArgs)
            "top", "leaderboard" -> topCommand.onTabComplete(sender, command, "top", subArgs)
            else -> emptyList()
        }
    }
}
