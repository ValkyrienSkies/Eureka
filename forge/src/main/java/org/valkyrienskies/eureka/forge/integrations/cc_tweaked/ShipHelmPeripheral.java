package org.valkyrienskies.eureka.forge.integrations.cc_tweaked;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.game.ships.ShipData;
import org.valkyrienskies.eureka.block.ShipHelmBlock;
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity;
import org.valkyrienskies.eureka.ship.EurekaShipControl;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class ShipHelmPeripheral implements IPeripheral {
    Level world;
    BlockPos pos;

    public ShipHelmPeripheral(Level level, BlockPos blockPos) {
        this.world = level;
        this.pos = blockPos;
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
    public final boolean forward() throws LuaException {
        return applyThrust("forward");
    }

    @LuaFunction
    public final boolean turnLeft() throws LuaException {
        return applyThrust("left");
    }

    @LuaFunction
    public final boolean turnRight() throws LuaException {
        return applyThrust("right");
    }

    @LuaFunction
    public final boolean reverse() throws LuaException {
        return applyThrust("back");
    }

    @LuaFunction
    public final boolean raise() throws LuaException {
        return applyThrust("up");
    }

    @LuaFunction
    public final boolean lower() throws LuaException {
        return applyThrust("down");
    }

    @LuaFunction
    public final boolean isCruising() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    SeatedControllingPlayer fakePlayer = ship.getAttachment(SeatedControllingPlayer.class);
                    return fakePlayer != null && fakePlayer.getCruise();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean startCruising() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    SeatedControllingPlayer fakePlayer = ship.getAttachment(SeatedControllingPlayer.class);
                    if (fakePlayer == null) {
                        fakePlayer = new SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
                    }

                    fakePlayer.setCruise(true);
                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);

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

    @LuaFunction
    public final boolean stopCruising() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    SeatedControllingPlayer fakePlayer = ship.getAttachment(SeatedControllingPlayer.class);
                    if (fakePlayer == null) {
                        fakePlayer = new SeatedControllingPlayer(world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
                    }

                    fakePlayer.setCruise(false);
                    ship.saveAttachment(SeatedControllingPlayer.class, fakePlayer);

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

    @LuaFunction
    public final boolean isAligning() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getAligning();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean startAlignment() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    control.setAligning(true);
                    ship.saveAttachment(EurekaShipControl.class, control);
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean stopAlignment() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    control.setAligning(false);
                    ship.saveAttachment(EurekaShipControl.class, control);
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean isAligned() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    Vec3i shipHelmFacing = world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().getNormal();
                    Vector3d dirVec = VectorConversionsMCKt.transformDirection(ship.getShipToWorld(), shipHelmFacing);
                    return ship.getOmega().y() == dirVec.y;
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean canDisassemble() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getCanDisassemble();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean isDisassembling() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getDisassembling();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean disassemble() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof ShipHelmBlockEntity) {
                        ShipHelmBlockEntity helm = (ShipHelmBlockEntity) be;
                        helm.disassemble();
                    } else {
                        throw new LuaException("no ship helm");
                    }
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final boolean assemble() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship == null) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ShipHelmBlockEntity) {
                    ShipHelmBlockEntity helm = (ShipHelmBlockEntity) be;
                    helm.assemble();

                    return true;
                } else {
                    throw new LuaException("no ship helm");
                }
            } else {
                throw new LuaException("already assembled");
            }
        }
        return false;
    }

    @LuaFunction
    public final int getBalloonAmount() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getBalloons();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final int getAnchorAmount() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getAnchors();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final int getActiveAnchorAmount() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getAnchorsActive();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final boolean areAnchorsActive() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getAnchorsActive() > 0;
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final int getShipHelmAmount() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                EurekaShipControl control = ship.getAttachment(EurekaShipControl.class);
                if (control != null) {
                    return control.getHelms();
                } else {
                    throw new LuaException("not Eureka ship");
                }
            } else {
                throw new LuaException("no ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final String getShipName() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                return ship.getName();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return "";
    }

    @LuaFunction
    public final boolean setShipName(String string) throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                ship.setName(string);
                return true;
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return false;
    }

    @LuaFunction
    public final long getShipID() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                return ship.getId();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return 0;
    }

    @LuaFunction
    public final double getMass() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                return ship.getInertiaData().getShipMass();
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return 0.0;
    }

    @LuaFunction
    public final Object[] getVelocity() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                Vector3dc vel =ship.getVelocity();
                return new Object[] { vel.x(), vel.y(), vel.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getPosition() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                Vector3dc vec = ship.getShipTransform().getShipPositionInWorldCoordinates();
                return new Object[] { vec.x(), vec.y(), vec.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    @LuaFunction
    public final Object[] getScale() throws LuaException {
        if (!world.isClientSide()) {
            ShipData ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                Vector3dc scale = ship.getShipTransform().getShipCoordinatesToWorldCoordinatesScaling();
                return new Object[] { scale.x(), scale.y(), scale.z() };
            } else {
                throw new LuaException("Not on a Ship");
            }
        }
        return new Object[0];
    }

    public boolean applyThrust(String direction) throws LuaException {
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
                    int ticks = 0;
                    while (ticks < 20) {
                        if (world.getGameTime() - originTime == 1) {
                            originTime = world.getGameTime();

                            ticks++;
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
