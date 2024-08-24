package org.valkyrienskies.eureka.item

import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.valkyrienskies.eureka.EurekaBlocks
import org.valkyrienskies.eureka.EurekaItems
import org.valkyrienskies.eureka.gui.ender_tether.EnderTetherEditScreen
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML

class EnderTether(
    properties: Properties
) : Item(properties) {

    fun getTetherDistance(itemStack: ItemStack): Double? {
        return if (itemStack.getOrCreateTag().contains(NBT_TAG)) {
            itemStack.getOrCreateTag().getDouble(NBT_TAG)
        } else {
            null
        }
    }

    fun setTetherDistance(itemStack: ItemStack, inventory: Inventory?, tetherDistance: Double) {
        itemStack.getOrCreateTag().putDouble(NBT_TAG, sanitizeInput(tetherDistance))
        inventory?.setChanged()
    }

    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(
                EnderTetherEditScreen(
                    EurekaItems.ENDER_TETHER.get().getTetherDistance(player.getItemInHand(usedHand))
                        ?: DEFAULT_TETHER_DISTANCE
                )
            )
        }
        return super.use(level, player, usedHand)
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        val player = ctx.player ?: return super.useOn(ctx)
        val level = ctx.level
        val pos = ctx.clickedPos
        val blockState = level.getBlockState(pos)
        val isBlockAnAnchor = blockState.block == EurekaBlocks.ENDER_ANCHOR.get()
        if (ctx.level.isClientSide) {
            if (isBlockAnAnchor) {
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
            player.sendMessage(SHIP_HELM_NECESSARY, Util.NIL_UUID)
            return super.useOn(ctx)
        }

        eurekaControl.enderTetherControlData = EurekaShipControl.EnderTetherControlData(
            followingPlayerId = player.uuid,
            enderAnchorBlockPos = pos.toJOML(),
            followingPlayerDistance = getTetherDistance(ctx.itemInHand) ?: DEFAULT_TETHER_DISTANCE,
        )

        player.sendMessage(TETHER_SUCCESSFUL, Util.NIL_UUID)

        return InteractionResult.SUCCESS
    }

    companion object {
        // TODO: Translation files
        private val SHIP_HELM_NECESSARY = TranslatableComponent("Ship Helm is necessary for Ender Anchors to function!")
        private val TETHER_SUCCESSFUL = TranslatableComponent("Ender Anchor tethered successfully!")
        private const val DEFAULT_TETHER_DISTANCE = 10.0
        const val MIN_TETHER_DISTANCE = 1.0
        const val MAX_TETHER_DISTANCE = 100.0
        private const val NBT_TAG = "tether_distance"

        fun sanitizeInput(tetherDistance: Double) =
            tetherDistance.coerceIn(MIN_TETHER_DISTANCE, MAX_TETHER_DISTANCE)
    }
}
