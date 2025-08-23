package com.dansplugins.factionsystem.command.faction.guide

import com.dansplugins.factionsystem.MedievalFactions
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import net.md_5.bungee.api.ChatColor as SpigotChatColor

class MfFactionGuideCommand(private val plugin: MedievalFactions) : CommandExecutor, TabCompleter {

    // Bronze and gold color palette for guide sections
    private val titleColor = SpigotChatColor.of("#CD7F32") // Classic bronze
    private val sectionColor = SpigotChatColor.of("#FFD700") // Gold for section headers
    private val contentColor = SpigotChatColor.of("#D2691E") // Chocolate
    private val highlightColor = SpigotChatColor.of("#DA8A67") // Light copper
    private val commandColor = SpigotChatColor.of("#B87333") // Copper for commands
    private val tipColor = SpigotChatColor.of("#C19A6B") // Tan
    private val navigationColor = SpigotChatColor.of("#DA8A67") // Light copper for navigation

    // Guide sections with detailed explanations
    private val guideSections = mapOf(
        "start" to GuideSection(
            title = "Добро пожаловать!",
            content = """
                Здесь вы можете:
                • Создавать и управлять фракциями
                • Захватывать территории
                • Вести войны и заключать союзы
                • Строить экономику и политику 
            """.trimIndent(),
            nextSection = "basics"
        ),
        "basics" to GuideSection(
            title = "Основы фракций",
            content = """
                Основные концепции:
                
                Фракция - группа игроков, объединенных общей целью
                Территория - земля, принадлежащая фракции
                Сила - ресурс для захвата территорий
                Роли - права и обязанности в фракции
                
                Первые шаги:
                1. Создайте фракцию: /faction create <название>
                2. Пригласите игроков: /faction invite <игрок>
                3. Захватите территорию: /faction claim
                4. Постройте дом: /faction sethome
            """.trimIndent(),
            nextSection = "commands",
            prevSection = "start"
        ),
        "commands" to GuideSection(
            title = "Основные команды",
            content = """
                Команды управления фракцией:
                
                Создание и управление:
                • /faction create <название> - создать фракцию
                • /faction invite <игрок> - пригласить игрока
                • /faction kick <игрок> - исключить игрока
                • /faction leave - покинуть фракцию
                • /faction disband - распустить фракцию
                
                Территория:
                • /faction claim - захватить чанк
                • /faction unclaim - освободить чанк
                • /faction map - показать карту
                • /faction sethome - установить дом
                • /faction home - телепорт домой
            """.trimIndent(),
            nextSection = "diplomacy",
            prevSection = "basics"
        ),
        "diplomacy" to GuideSection(
            title = "Дипломатия и войны",
            content = """
                Дипломатические отношения:
                
                Союзы:
                • /faction ally <фракция> - заключить союз
                • /faction breakalliance <фракция> - разорвать союз
                • /faction invoke <союзник> <враг> - призвать союзника в войну
                
                Войны:
                • /faction declarewar <фракция> - объявить войну
                • /faction makepeace <фракция> - заключить мир
                
                Вассалитет:
                • /faction vassalize <фракция> - предложить вассалитет
                • /faction swearfealty <фракция> - принести клятву верности
                • /faction declareindependence - объявить независимость
            """.trimIndent(),
            nextSection = "economy",
            prevSection = "commands"
        ),
        "economy" to GuideSection(
            title = "Экономика и ресурсы",
            content = """
                Экономическая система:
                
                Сила (Power):
                • Сила генерируется автоматически
                • Требуется для захвата территорий
                • Можно потерять при смерти
                • /faction power - посмотреть силу
                
                Бонусная сила:
                • Админы могут давать бонусную силу
                • Увеличивает возможности фракции
                
                Территории:
                • Каждый чанк требует силы для захвата
                • Больше территорий = больше ресурсов
                • Защищайте свои земли от врагов
            """.trimIndent(),
            nextSection = "roles",
            prevSection = "diplomacy"
        ),
        "roles" to GuideSection(
            title = "Роли и права",
            content = """
                Система ролей:
                
                Создание ролей:
                • /faction role create <название> - создать роль
                • /faction role set <игрок> <роль> - назначить роль
                • /faction role setpermission <роль> <право> <allow|deny> - настроить права
                
                Управление ролями:
                • /faction role list - список ролей
                • /faction role view <роль> - посмотреть роль
                • /faction role delete <роль> - удалить роль
            """.trimIndent(),
            nextSection = "advanced",
            prevSection = "economy"
        ),
        "advanced" to GuideSection(
            title = "Продвинутые функции",
            content = """
                Продвинутые возможности:
                
                Флаги фракции:
                • /faction flag list - список флагов
                • /faction flag set <флаг> <значение> - установить флаг
                
                Законы:
                • /faction law add <закон> - добавить закон
                • /faction law list - список законов
                • /faction law remove <номер> - удалить закон
                
                Блокировка:
                • /lock - заблокировать блок
                • /unlock - разблокировать блок
                • /accessors add <игрок> - дать доступ к блоку
                
                Ворота:
                • /gate create - создать ворота
                • /gate remove - удалить ворота
            """.trimIndent(),
            nextSection = "tips",
            prevSection = "roles"
        ),
        "tips" to GuideSection(
            title = "Советы и стратегии",
            content = """
                Полезные советы:
                
                Начало игры:
                • Найдите союзников для защиты
                • Захватывайте стратегически важные территории
                • Стройте укрепления на границах
                
                Управление фракцией:
                • Назначайте ответственных за разные задачи
                • Создавайте четкие законы
                • Поддерживайте активность членов
                
                Дипломатия:
                • Будьте осторожны с объявлением войн
                • Поддерживайте хорошие отношения с соседями
                • Используйте союзы для защиты
                
                Безопасность:
                • Регулярно проверяйте границы
                • Используйте блокировку для защиты ресурсов
                • Стройте убежища в разных местах
            """.trimIndent(),
            nextSection = "end",
            prevSection = "advanced"
        ),
        "end" to GuideSection(
            title = "Дополнительная информация",
            content = """
                Полезные ссылки:
                
                Вики: https://github.com/dmccoystephenson/Medieval-Factions/wiki
                Discord: https://discord.gg/eF5ZJKk2C8
                
                Команды помощи:
                • /faction info - информация о фракции
                • /faction map - карта территорий
                
                Поддержка:
                Если у вас есть вопросы, обратитесь к администрации сервера или в Discord.
                
                Удачной игры!
            """.trimIndent(),
            prevSection = "tips"
        )
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("mf.guide")) {
            sender.sendMessage("${ChatColor.RED}У вас нет прав для просмотра гайда.")
            return true
        }

        val section = args.firstOrNull() ?: "start"
        showGuideSection(sender, section)
        return true
    }

    private fun showGuideSection(sender: CommandSender, sectionKey: String) {
        val section = guideSections[sectionKey] ?: guideSections["start"]!!

        // Clear chat with empty lines
        repeat(10) { sender.sendMessage("") }

        // Title with bronze color
        val titleComponent = TextComponent("${titleColor}${section.title}")
        sender.spigot().sendMessage(titleComponent)
        sender.sendMessage("")

        // Content with proper formatting and colors
        section.content.split("\n").forEach { line ->
            when {
                line.trim().isEmpty() -> sender.sendMessage("")
                line.contains(":") && !line.contains("•") -> {
                    // Section headers
                    sender.sendMessage("${sectionColor}$line")
                }
                line.contains("•") -> {
                    // Command lists
                    sender.sendMessage("${commandColor}$line")
                }
                line.contains("http") -> {
                    // Links
                    sender.sendMessage("${highlightColor}$line")
                }
                else -> {
                    // Regular content
                    sender.sendMessage("${contentColor}$line")
                }
            }
        }

        sender.sendMessage("")

        // Navigation buttons
        val navigationComponent = TextComponent()

        // Previous button
        if (section.prevSection != null) {
            val prevButton = TextComponent("$navigationColor🡸 Назад")
            prevButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction guide ${section.prevSection}")
            prevButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Перейти к предыдущему разделу"))
            navigationComponent.addExtra(prevButton)
            navigationComponent.addExtra(TextComponent(" "))
        }

        // Home button
        val homeButton = TextComponent("$navigationColor[ Главная ]")
        homeButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction guide start")
        homeButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Вернуться к началу гайда"))
        navigationComponent.addExtra(homeButton)

        // Next button
        if (section.nextSection != null) {
            navigationComponent.addExtra(TextComponent(" "))
            val nextButton = TextComponent("$navigationColor🡺 Вперед")
            nextButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction guide ${section.nextSection}")
            nextButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Перейти к следующему разделу"))
            navigationComponent.addExtra(nextButton)
        }

        sender.spigot().sendMessage(navigationComponent)

        // Page indicator
        val sections = guideSections.keys.toList()
        val currentIndex = sections.indexOf(sectionKey) + 1
        val totalPages = sections.size
        sender.sendMessage("${tipColor}Страница $currentIndex из $totalPages")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return if (args.size == 1) {
            guideSections.keys.filter { it.startsWith(args[0]) }
        } else {
            emptyList()
        }
    }

    private data class GuideSection(
        val title: String,
        val content: String,
        val nextSection: String? = null,
        val prevSection: String? = null
    )
}
