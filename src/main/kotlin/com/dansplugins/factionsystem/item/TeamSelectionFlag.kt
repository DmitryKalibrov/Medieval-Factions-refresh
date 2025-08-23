package com.dansplugins.factionsystem.item

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.player.MfPlayerId
import com.dansplugins.factionsystem.team.TeamColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class TeamSelectionFlag(private val plugin: MedievalFactions) : Listener {

    companion object {
        private val FLAG_KEY = NamespacedKey("medievalfactions", "team_selection_flag")
        private val TEAM_KEY = NamespacedKey("medievalfactions", "team_color")
    }

    fun createFlag(): ItemStack {
        val flag = ItemStack(Material.WHITE_BANNER)
        val meta = flag.itemMeta

        if (meta != null) {
            meta.setDisplayName("${ChatColor.WHITE}🏁 Выбор команды")
            meta.lore = listOf(
                "${ChatColor.LIGHT_PURPLE}ПКМ для выбора команды",
                "${ChatColor.LIGHT_PURPLE}Нельзя выкинуть или переместить"
            )

            // Добавляем зачарование для визуального эффекта
            meta.addEnchant(Enchantment.DURABILITY, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

            // Добавляем метку для идентификации
            meta.persistentDataContainer.set(FLAG_KEY, PersistentDataType.BYTE, 1.toByte())

            flag.itemMeta = meta
        }

        return flag
    }

    fun isTeamSelectionFlag(item: ItemStack?): Boolean {
        return item?.itemMeta?.persistentDataContainer?.has(FLAG_KEY, PersistentDataType.BYTE) == true
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return
        if (!isTeamSelectionFlag(event.item)) return

        event.isCancelled = true
        openTeamSelectionGUI(event.player)
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (isTeamSelectionFlag(event.itemDrop.itemStack)) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}Нельзя выкинуть флаг выбора команды!")
        }
    }

    @EventHandler
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        if (isTeamSelectionFlag(event.item.itemStack)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title.contains("Выбор команды")) {
            event.isCancelled = true

            if (event.currentItem == null) return

            val team = when (event.slot) {
                3 -> TeamColor.RED
                5 -> TeamColor.BLUE
                7 -> TeamColor.YELLOW
                else -> return
            }

            val player = event.whoClicked as Player
            selectTeam(player, team)
            player.closeInventory()
        }

        // Запрещаем перемещение флага в инвентаре
        if (isTeamSelectionFlag(event.cursor) || isTeamSelectionFlag(event.currentItem)) {
            if (event.action.name.contains("PLACE") || event.action.name.contains("MOVE")) {
                event.isCancelled = true
                event.whoClicked.sendMessage("${ChatColor.RED}Нельзя переместить флаг выбора команды!")
            }
        }
    }

    private fun openTeamSelectionGUI(player: Player) {
        val inventory = plugin.server.createInventory(null, 9, "${ChatColor.WHITE}Выбор команды")

        // Красная команда
        val redTeam = ItemStack(Material.RED_BANNER)
        val redMeta = redTeam.itemMeta
        if (redMeta != null) {
            redMeta.setDisplayName("${ChatColor.RED}Красные")
            redMeta.lore = listOf(
                "${ChatColor.RED}Присоединиться к Красным",
                "${ChatColor.RED}Имеют короля",
                "${ChatColor.RED}Командная игра"
            )
            redMeta.persistentDataContainer.set(TEAM_KEY, PersistentDataType.STRING, TeamColor.RED.name)
            redTeam.itemMeta = redMeta
        }
        inventory.setItem(3, redTeam)

        // Синяя команда
        val blueTeam = ItemStack(Material.BLUE_BANNER)
        val blueMeta = blueTeam.itemMeta
        if (blueMeta != null) {
            blueMeta.setDisplayName("${ChatColor.AQUA}Синие")
            blueMeta.lore = listOf(
                "${ChatColor.AQUA}Присоединиться к Синим",
                "${ChatColor.AQUA}Имеют короля",
                "${ChatColor.AQUA}Командная игра"
            )
            blueMeta.persistentDataContainer.set(TEAM_KEY, PersistentDataType.STRING, TeamColor.BLUE.name)
            blueTeam.itemMeta = blueMeta
        }
        inventory.setItem(5, blueTeam)

        // Жёлтая команда (авантюристы)
        val yellowTeam = ItemStack(Material.YELLOW_BANNER)
        val yellowMeta = yellowTeam.itemMeta
        if (yellowMeta != null) {
            yellowMeta.setDisplayName("${ChatColor.YELLOW}Жёлтые (Авантюристы)")
            yellowMeta.lore = listOf(
                "${ChatColor.GOLD}Стать авантюристом",
                "${ChatColor.GOLD}Свободные отношения",
                "${ChatColor.GOLD}Независимость от команд"
            )
            yellowMeta.persistentDataContainer.set(TEAM_KEY, PersistentDataType.STRING, TeamColor.YELLOW.name)
            yellowTeam.itemMeta = yellowMeta
        }
        inventory.setItem(7, yellowTeam)

        player.openInventory(inventory)
    }

    private fun selectTeam(player: Player, team: TeamColor) {
        val playerId = MfPlayerId(player.uniqueId.toString())
        val currentTeam = plugin.teamService.getPlayerTeam(playerId)

        if (currentTeam == team) {
            player.sendMessage("${ChatColor.RED}Вы уже состоите в этой команде!")
            return
        }

        // Проверяем лимит смен команд
        val success = plugin.teamService.setPlayerTeam(playerId, team)
        if (!success) {
            player.sendMessage("${ChatColor.RED}Вы достигли лимита смен команд (максимум 2 раза)!")
            return
        }

        val teamName = when (team) {
            TeamColor.RED -> "Красные"
            TeamColor.BLUE -> "Синие"
            TeamColor.YELLOW -> "Жёлтые (Авантюристы)"
        }

        player.sendMessage("${ChatColor.GREEN}Вы присоединились к команде: $teamName")

        // Удаляем флаг из 9 слота после выбора команды
        val flagSlot = player.inventory.getItem(8)
        if (isTeamSelectionFlag(flagSlot)) {
            player.inventory.setItem(8, null)
            player.sendMessage("${ChatColor.LIGHT_PURPLE}Флаг выбора команды удален из инвентаря")
        }

        // Уведомляем о смене команды
        if (currentTeam != null) {
            val currentTeamName = when (currentTeam) {
                TeamColor.RED -> "Красных"
                TeamColor.BLUE -> "Синих"
                TeamColor.YELLOW -> "Жёлтых"
            }
            plugin.server.broadcastMessage("${ChatColor.GREEN}${player.name} перешел из команды $currentTeamName в $teamName")
        }
    }

    fun giveFlagToPlayerOnJoin(player: Player) {
        val playerId = MfPlayerId(player.uniqueId.toString())
        val currentTeam = plugin.teamService.getPlayerTeam(playerId)

        // Если игрок уже в команде, не выдаем флаг
        if (currentTeam != null) return

        // Проверяем, есть ли уже флаг в 9 слоте
        val currentItem = player.inventory.getItem(8)
        if (currentItem != null && !currentItem.type.isAir && !isTeamSelectionFlag(currentItem)) {
            // Если слот занят другим предметом, не выдаем флаг
            return
        }

        // Если флаг уже есть, не выдаем повторно
        if (isTeamSelectionFlag(currentItem)) return

        player.inventory.setItem(8, createFlag())
        player.sendMessage("${ChatColor.GREEN}🏁 Добро пожаловать! Выберите команду с помощью флага в 9-м слоте")
        player.sendMessage("${ChatColor.LIGHT_PURPLE}Используйте ПКМ по флагу для выбора команды")
    }
}
