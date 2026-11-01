package org.valkyrienskies.eureka

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer

/**
 * Warns admins on join when a Valkyrien Skies version with known Eureka-breaking bugs is installed.
 */
object VsVersionWarning {
    // VS version -> link to the issue describing what breaks
    private val BROKEN_VERSIONS = mapOf(
        "2.4.11" to "https://github.com/ValkyrienSkies/Eureka/issues/500"
    )

    /**
     * @param vsVersion the installed Valkyrien Skies version, as reported by the mod loader
     */
    @JvmStatic
    fun onPlayerJoin(player: ServerPlayer, vsVersion: String?) {
        if (vsVersion == null || !EurekaConfig.SERVER.warnAboutBrokenVsVersion) return

        // Loader versions may carry build metadata, e.g. "2.4.11+9a09dd2609"
        val baseVersion = vsVersion.substringBefore('+')
        val issueUrl = BROKEN_VERSIONS[baseVersion] ?: return

        if (!isAdmin(player)) return

        val link = Component.literal(issueUrl).withStyle(
            Style.EMPTY
                .withColor(ChatFormatting.AQUA)
                .withUnderlined(true)
                .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, issueUrl))
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(issueUrl)))
        )

        player.sendSystemMessage(
            Component.translatable("info.vs_eureka.broken_vs_version", baseVersion, link)
                .withStyle(ChatFormatting.YELLOW)
        )
    }

    private fun isAdmin(player: ServerPlayer): Boolean =
        player.hasPermissions(2) || player.server.isSingleplayerOwner(player.gameProfile)
}
