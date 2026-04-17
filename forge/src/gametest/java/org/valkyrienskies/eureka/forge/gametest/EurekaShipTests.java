package org.valkyrienskies.eureka.forge.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.eureka.EurekaBlocks;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.ship.EurekaShipControl;
import org.valkyrienskies.eureka.util.ShipAssembler;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@GameTestHolder(EurekaMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class EurekaShipTests {

    private static final Logger LOGGER = LoggerFactory.getLogger("EurekaShipTests");

    @GameTest(template = "empty_platform", batch = "shipAssemblyCreatesShip")
    public void shipAssemblyCreatesShip(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.assertBlockPresent(EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get(), helmPos);

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            BlockPos absolutePos = helper.absolutePos(helmPos);

            ServerShip ship;
            try {
                ship = ShipAssembler.INSTANCE.collectBlocks(level, absolutePos, state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null — too many blocks or no solid blocks");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble ship: " + e.getMessage());
                return;
            }

            helper.assertBlockPresent(Blocks.AIR, helmPos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_platform", timeoutTicks = 200, batch = "shipAssemblyWithHelmSucceeds")
    public void shipAssemblyWithHelmSucceeds(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship;
            try {
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(helmPos),
                    state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null for helm block");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble helm ship: " + e.getMessage());
                return;
            }

            LOGGER.info("Ship assembled with helm block (id={})", ship.getId());
            helper.assertBlockPresent(Blocks.AIR, helmPos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_platform", timeoutTicks = 200, batch = "shipAssemblyWithBalloonSucceeds")
    public void shipAssemblyWithBalloonSucceeds(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        BlockPos balloonPos = new BlockPos(1, 1, 2);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.setBlock(balloonPos, EurekaBlocks.INSTANCE.getBALLOON().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship;
            try {
                // BFS from helm will collect the adjacent balloon too
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(helmPos),
                    state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble helm+balloon ship: " + e.getMessage());
                return;
            }

            LOGGER.info("Ship assembled with helm+balloon (id={})", ship.getId());
            helper.assertBlockPresent(Blocks.AIR, helmPos);
            helper.assertBlockPresent(Blocks.AIR, balloonPos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_platform", batch = "assembleEmptyBlocksFails")
    public void assembleEmptyBlocksFails(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            // No block placed — BFS from air position should fail
            BlockPos airPos = helper.absolutePos(new BlockPos(1, 2, 1));

            try {
                ServerShip ship = ShipAssembler.INSTANCE.collectBlocks(level, airPos, state -> !state.isAir());
                if (ship == null) {
                    helper.succeed();
                    return;
                }
                helper.fail("Expected assembly of air blocks to fail but got ship " + ship.getId());
            } catch (Exception | AssertionError e) {
                helper.succeed();
            }
        });
    }

    @GameTest(template = "empty_platform", batch = "assemblyRemovesOriginalBlocks")
    public void assemblyRemovesOriginalBlocks(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        BlockPos balloonPos = new BlockPos(1, 1, 2);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.setBlock(balloonPos, EurekaBlocks.INSTANCE.getBALLOON().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            try {
                ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(helmPos),
                    state -> !state.isAir());
            } catch (Exception e) {
                helper.fail("Failed to assemble: " + e.getMessage());
                return;
            }

            helper.assertBlockPresent(Blocks.AIR, helmPos);
            helper.assertBlockPresent(Blocks.AIR, balloonPos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_platform", timeoutTicks = 200, batch = "multipleEurekaShipsAssembled")
    public void multipleEurekaShipsAssembled(GameTestHelper helper) {
        // Three isolated helms — BFS from each will only collect that one block
        BlockPos pos1 = new BlockPos(1, 1, 1);
        BlockPos pos2 = new BlockPos(3, 1, 1);
        BlockPos pos3 = new BlockPos(5, 1, 1);
        helper.setBlock(pos1, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.setBlock(pos2, EurekaBlocks.INSTANCE.getSPRUCE_SHIP_HELM().get().defaultBlockState());
        helper.setBlock(pos3, EurekaBlocks.INSTANCE.getBIRCH_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            int shipsCreated = 0;

            for (BlockPos pos : new BlockPos[]{pos1, pos2, pos3}) {
                try {
                    ServerShip ship = ShipAssembler.INSTANCE.collectBlocks(level,
                        helper.absolutePos(pos), state -> !state.isAir());
                    if (ship == null) {
                        helper.fail("collectBlocks returned null for ship " + (shipsCreated + 1));
                        return;
                    }
                    shipsCreated++;
                } catch (Exception e) {
                    helper.fail("Failed to assemble ship " + (shipsCreated + 1) + ": " + e.getMessage());
                    return;
                }
            }

            if (shipsCreated != 3) {
                helper.fail("Expected 3 ships, created " + shipsCreated);
                return;
            }

            helper.assertBlockPresent(Blocks.AIR, pos1);
            helper.assertBlockPresent(Blocks.AIR, pos2);
            helper.assertBlockPresent(Blocks.AIR, pos3);
            helper.succeed();
        });
    }

    /**
     * After assembling a ship via the helm BFS path, EurekaShipControl.helms should reach 1
     * once the ship appears in VS2's loadedShips and onPlace callbacks have fired.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 400, batch = "helmCountTrackedAfterAssembly")
    public void helmCountTrackedAfterAssembly(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship;
            try {
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(helmPos),
                    state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble: " + e.getMessage());
                return;
            }

            ChunkPos shipChunk = new ChunkPos(
                ship.getChunkClaim().getXMiddle(),
                ship.getChunkClaim().getZMiddle()
            );
            LOGGER.info("Helm ship assembled (id={}), polling for EurekaShipControl.helms...", ship.getId());
            pollHelmCount(helper, level, shipChunk, new int[]{0}, 200);
        });
    }

    private void pollHelmCount(GameTestHelper helper, ServerLevel level, ChunkPos shipChunk,
            int[] ticksWaited, int maxTicks) {
        helper.runAfterDelay(2, () -> {
            ticksWaited[0] += 2;

            LoadedServerShip loaded = VSGameUtilsKt.getLoadedShipManagingPos(level, shipChunk);
            if (loaded == null) {
                if (ticksWaited[0] >= maxTicks) {
                    helper.fail("Ship never appeared in VS2 loadedShips after " + maxTicks + " ticks");
                } else {
                    pollHelmCount(helper, level, shipChunk, ticksWaited, maxTicks);
                }
                return;
            }

            EurekaShipControl control = loaded.getAttachment(EurekaShipControl.class);
            if (control != null && control.getHelms() >= 1) {
                LOGGER.info("EurekaShipControl.helms = {} after {} ticks", control.getHelms(), ticksWaited[0]);
                helper.succeed();
            } else if (ticksWaited[0] >= maxTicks) {
                String msg = control == null
                    ? "EurekaShipControl attachment is null after " + maxTicks + " ticks"
                    : "EurekaShipControl.helms = " + control.getHelms() + " (expected >= 1) after " + maxTicks + " ticks";
                helper.fail(msg);
            } else {
                pollHelmCount(helper, level, shipChunk, ticksWaited, maxTicks);
            }
        });
    }

    /**
     * After assembling a ship with a helm + 3 balloons via BFS, EurekaShipControl.balloons
     * should reach 3 once the ship appears in VS2's loadedShips.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 400, batch = "balloonCountTrackedAfterAssembly")
    public void balloonCountTrackedAfterAssembly(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(2, 1, 1);
        BlockPos balloonPos1 = new BlockPos(1, 1, 1);
        BlockPos balloonPos2 = new BlockPos(3, 1, 1);
        BlockPos balloonPos3 = new BlockPos(2, 1, 2);
        helper.setBlock(helmPos,     EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.setBlock(balloonPos1, EurekaBlocks.INSTANCE.getBALLOON().get().defaultBlockState());
        helper.setBlock(balloonPos2, EurekaBlocks.INSTANCE.getRED_BALLOON().get().defaultBlockState());
        helper.setBlock(balloonPos3, EurekaBlocks.INSTANCE.getBLUE_BALLOON().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship;
            try {
                // BFS from helm collects all 4 adjacent blocks
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(helmPos),
                    state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble: " + e.getMessage());
                return;
            }

            ChunkPos shipChunk = new ChunkPos(
                ship.getChunkClaim().getXMiddle(),
                ship.getChunkClaim().getZMiddle()
            );
            LOGGER.info("Balloon ship assembled (id={}), polling for EurekaShipControl.balloons...", ship.getId());
            pollBalloonCount(helper, level, shipChunk, new int[]{0}, 200, 3);
        });
    }

    private void pollBalloonCount(GameTestHelper helper, ServerLevel level, ChunkPos shipChunk,
            int[] ticksWaited, int maxTicks, int expectedCount) {
        helper.runAfterDelay(2, () -> {
            ticksWaited[0] += 2;

            LoadedServerShip loaded = VSGameUtilsKt.getLoadedShipManagingPos(level, shipChunk);
            if (loaded == null) {
                if (ticksWaited[0] >= maxTicks) {
                    helper.fail("Ship never appeared in VS2 loadedShips after " + maxTicks + " ticks");
                } else {
                    pollBalloonCount(helper, level, shipChunk, ticksWaited, maxTicks, expectedCount);
                }
                return;
            }

            EurekaShipControl control = loaded.getAttachment(EurekaShipControl.class);
            if (control != null && control.getBalloons() >= expectedCount) {
                LOGGER.info("EurekaShipControl.balloons = {} after {} ticks", control.getBalloons(), ticksWaited[0]);
                helper.succeed();
            } else if (ticksWaited[0] >= maxTicks) {
                String msg = control == null
                    ? "EurekaShipControl attachment is null after " + maxTicks + " ticks"
                    : "EurekaShipControl.balloons = " + control.getBalloons() + " (expected " + expectedCount + ") after " + maxTicks + " ticks";
                helper.fail(msg);
            } else {
                pollBalloonCount(helper, level, shipChunk, ticksWaited, maxTicks, expectedCount);
            }
        });
    }
}