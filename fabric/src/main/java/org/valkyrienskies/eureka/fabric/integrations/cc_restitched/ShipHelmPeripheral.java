package org.valkyrienskies.eureka.fabric.integrations.cc_restitched;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.eureka.block.ShipHelmBlock;
import org.valkyrienskies.eureka.ship.EurekaShipControl;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipHelmPeripheral implements IPeripheral {
    Level world;
    BlockPos pos;
    TileComputerBase computer;

    public ShipHelmPeripheral(Level level, BlockPos blockPos, TileComputerBase blockEntity) {
        this.world = level;
        this.pos = blockPos;
        this.computer = blockEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "ship_helm";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        if (world != null) {
            return world.getBlockState(pos).getBlock() instanceof ShipHelmBlock;
        }
        return false;
    }

    @LuaFunction
    public final String getName() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                return ship.getName();
            }
            throw new LuaException("no ship");
        }
        return "";
    }

    @LuaFunction
    public final boolean forward(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "forward");
    }

    @LuaFunction
    public final boolean turnLeft(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "left");
    }

    @LuaFunction
    public final boolean turnRight(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "right");
    }

    @LuaFunction
    public final boolean reverse(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "back");
    }

    @LuaFunction
    public final boolean raise(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "up");
    }

    @LuaFunction
    public final boolean lower(int seconds) throws LuaException {
        return applyThrustOverTime(seconds, "down");
    }

    public boolean applyThrustOverTime(int seconds, String direction) throws LuaException {
        if (seconds > 10) {
            throw new LuaException("too long max 10 seconds");
        } else {
            if (!world.isClientSide()) {
                ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                    if (control != null) {
                        SeatedControllingPlayer fakePlayer = ship.getAttachment(SeatedControllingPlayer.class);
                        if (fakePlayer == null) {
                            fakePlayer = new SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
                        }

                        long originTime = world.getGameTime();
                        while (seconds > 0) {
                            if (((world.getGameTime() - originTime) % 20) == 1) {
                                originTime = world.getGameTime();

                                seconds--;
                            }

                            switch (direction) {
                                case "forward":
                                    fakePlayer.setForwardImpulse(1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                                case "left":
                                    fakePlayer.setLeftImpulse(1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                                case "right":
                                    fakePlayer.setLeftImpulse(-1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                                case "back":
                                    fakePlayer.setForwardImpulse(-1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                                case "up":
                                    fakePlayer.setUpImpulse(1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                                default:
                                    fakePlayer.setUpImpulse(-1.0f);
                                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                    break;
                            }
                        }

                        switch (direction) {
                            case "forward":
                            case "back":
                                fakePlayer.setForwardImpulse(0.0f);
                                ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                break;
                            case "left":
                            case "right":
                                fakePlayer.setLeftImpulse(0.0f);
                                ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                break;
                            default:
                                fakePlayer.setUpImpulse(0.0f);
                                ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);
                                break;
                        }

                        return true;
                    } else {
                        throw new LuaException("not Eureka ship");
                    }
                } else {
                    throw new LuaException("no ship");
                }
            }
            return false;
        }
    }
}
