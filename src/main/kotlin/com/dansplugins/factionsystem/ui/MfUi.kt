package com.dansplugins.factionsystem.ui

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text

class MfUi(
    private val primary: ChatColor,
    private val accent: ChatColor,
    private val info: ChatColor,
    private val success: ChatColor,
    private val warning: ChatColor,
    private val danger: ChatColor,
    private val muted: ChatColor
) {

    fun text(message: String, color: ChatColor = primary): BaseComponent =
        TextComponent(message).apply { this.color = color }

    fun button(
        label: String,
        runCommand: String,
        hover: String? = null,
        color: ChatColor = accent
    ): BaseComponent =
        TextComponent(label).apply {
            this.color = color
            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, runCommand)
            if (hover != null) hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hover))
        }

    fun hint(message: String): BaseComponent = text(message, muted)
    fun confirmButton(
        label: String,
        runCommand: String,
        hover: String? = null
    ): BaseComponent =
        TextComponent(label).apply {
            this.color = success
            isBold = true
            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, runCommand)
            if (hover != null) hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hover))
        }

    fun cancelButton(
        label: String,
        runCommand: String,
        hover: String? = null
    ): BaseComponent =
        TextComponent(label).apply {
            this.color = danger
            isBold = true
            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, runCommand)
            if (hover != null) hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hover))
        }

    fun confirmPrompt(
        title: String,
        yesLabel: String,
        yesCommand: String,
        yesHover: String? = null,
        noLabel: String,
        noCommand: String,
        noHover: String? = null
    ): Array<BaseComponent> = arrayOf(
        text(title, info),
        TextComponent(" "),
        confirmButton(yesLabel, yesCommand, yesHover),
        TextComponent(" "),
        cancelButton(noLabel, noCommand, noHover)
    )
}
