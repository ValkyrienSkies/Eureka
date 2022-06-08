package org.valkyrienskies.eureka.block

import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material

private val FACING = HorizontalDirectionalBlock.FACING!!

object EngineBlock : BaseEntityBlock(Properties.of(Material.METAL).strength(3F, 1200.0f).sound(SoundType.METAL)) {
    override fun newBlockEntity(blockGetter: BlockGetter): BlockEntity = FurnaceBlockEntity()

    init {
        registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}