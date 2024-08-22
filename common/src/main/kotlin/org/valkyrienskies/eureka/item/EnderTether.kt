package org.valkyrienskies.eureka.item

import net.minecraft.Util
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.eureka.block.EnderAnchorBlock
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos

class EnderTether(
    properties: Properties
) : Item(properties) {

    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        if (ctx.level.isClientSide) {
            return super.useOn(ctx)
        }
        val player = ctx.player ?: return super.useOn(ctx)
        val level = ctx.level as ServerLevel
        val pos = ctx.clickedPos
        val blockState = level.getBlockState(pos)
        if (blockState.block !is EnderAnchorBlock) return super.useOn(ctx)
        val ship = level.getShipManagingPos(pos) ?: return super.useOn(ctx)
        val eurekaControl = ship.getAttachment(EurekaShipControl::class.java)

        if (eurekaControl == null) {
            // Tell player they need to place a helm for the ender anchor to work
            // TODO: Put this message in translation file
            player.sendMessage(TextComponent("Ship Helm is necessary for Ender Anchors to function!"), Util.NIL_UUID)
            return super.useOn(ctx)
        }

        eurekaControl.followingPlayerId = player.uuid
        // TODO: Put this message in translation file
        player.sendMessage(TextComponent("Ender Anchor tethered successfully!"), Util.NIL_UUID)

        return InteractionResult.SUCCESS
    }
}
