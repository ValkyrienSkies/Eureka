package org.valkyrienskies.eureka.fabric.integrations.cc_restitched;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.block.ShipHelmBlock;

public class ShipHelmPeripheralProvider implements IPeripheralProvider {
    @NotNull
    @Override
    public IPeripheral getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (
                level.getBlockState(blockPos).getBlock() instanceof ShipHelmBlock &&
                        !EurekaConfig.SERVER.getComputerCraft().getDisableComputerCraft()
        ) {
            if (level.getBlockEntity(blockPos.relative(direction.getOpposite())) instanceof TileComputerBase) {
                return new ShipHelmPeripheral(level, blockPos, (TileComputerBase) level.getBlockEntity(blockPos));
            }
            return new ShipHelmPeripheral(level, blockPos, null);
        }
        return null;
    }
}
