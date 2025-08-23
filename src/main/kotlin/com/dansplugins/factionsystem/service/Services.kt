package com.dansplugins.factionsystem.service

import com.dansplugins.factionsystem.chat.MfChatService
import com.dansplugins.factionsystem.claim.MfClaimService
import com.dansplugins.factionsystem.duel.MfDuelService
import com.dansplugins.factionsystem.economy.MfEconomyService
import com.dansplugins.factionsystem.faction.MfFactionService
import com.dansplugins.factionsystem.gate.MfGateService
import com.dansplugins.factionsystem.interaction.MfInteractionService
import com.dansplugins.factionsystem.law.MfLawService
import com.dansplugins.factionsystem.locks.MfLockService
import com.dansplugins.factionsystem.map.MapService
import com.dansplugins.factionsystem.notification.MfNotificationService
import com.dansplugins.factionsystem.player.MfPlayerService
import com.dansplugins.factionsystem.potion.MfPotionService
import com.dansplugins.factionsystem.relationship.MfFactionRelationshipService
import com.dansplugins.factionsystem.teleport.MfTeleportService
import com.dansplugins.factionsystem.ui.MfUi

class Services(
    val playerService: MfPlayerService,
    val factionService: MfFactionService,
    val lawService: MfLawService,
    val factionRelationshipService: MfFactionRelationshipService,
    val claimService: MfClaimService,
    val lockService: MfLockService,
    val interactionService: MfInteractionService,
    val notificationService: MfNotificationService,
    val gateService: MfGateService,
    val chatService: MfChatService,
    val duelService: MfDuelService,
    val potionService: MfPotionService,
    val teleportService: MfTeleportService,
    val mapService: MapService?,
    val ui: MfUi,
    val economyService: MfEconomyService
)
