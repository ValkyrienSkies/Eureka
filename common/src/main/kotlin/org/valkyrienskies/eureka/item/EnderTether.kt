package org.valkyrienskies.eureka.item

import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.valkyrienskies.eureka.block.EnderAnchorBlock
import org.valkyrienskies.eureka.gui.ender_tether.EnderTetherEditScreen
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML

class EnderTether(
    properties: Properties
) : Item(properties) {
    // TODO: Add data to this item when the ship is tethered?
    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(EnderTetherEditScreen())
        }
        return super.use(level, player, usedHand)
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        val player = ctx.player ?: return super.useOn(ctx)
        val level = ctx.level
        val pos = ctx.clickedPos
        val blockState = level.getBlockState(pos)
        val isBlockAnAnchor = blockState.block is EnderAnchorBlock
        if (ctx.level.isClientSide) {
            if (!isBlockAnAnchor) {
                // Open the gui if we didn't click an anchor
                Minecraft.getInstance().setScreen(EnderTetherEditScreen())
                return InteractionResult.SUCCESS
            }
            return super.useOn(ctx)
        }
        level as ServerLevel
        if (!isBlockAnAnchor) return super.useOn(ctx)
        val ship = level.getShipManagingPos(pos) ?: return super.useOn(ctx)
        val eurekaControl = ship.getAttachment(EurekaShipControl::class.java)

        if (eurekaControl == null) {
            // Tell player they need to place a helm for the ender anchor to work
            // TODO: Put this message in translation file
            player.sendMessage(TextComponent("Ship Helm is necessary for Ender Anchors to function!"), Util.NIL_UUID)
            return super.useOn(ctx)
        }

        eurekaControl.enderTetherControlData = EurekaShipControl.EnderTetherControlData(
            followingPlayerId = player.uuid,
            enderAnchorBlockPos = pos.toJOML(),
            followingPlayerDistance = 10.0,
        )

        // TODO: Put this message in translation file
        player.sendMessage(TextComponent("Ender Anchor tethered successfully!"), Util.NIL_UUID)

        return InteractionResult.SUCCESS
    }
}
