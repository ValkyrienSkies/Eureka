package org.valkyrienskies.eureka.forge.integrations.cc_tweaked;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.block.ShipHelmBlock;

public class ShipHelmPeripheralProvider implements IPeripheralProvider {
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        if (
                level.getBlockState(blockPos).getBlock() instanceof ShipHelmBlock &&
                        !EurekaConfig.SERVER.getComputerCraft().getDisableComputerCraft()
        ) {
            return LazyOptional.of(() -> new ShipHelmPeripheral(level, blockPos));
        }
        return null;
    }
}
