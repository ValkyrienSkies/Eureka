package org.valkyrienskies.eureka.forge.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.eureka.EurekaBlocks;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity;
import org.valkyrienskies.eureka.ship.EurekaShipControl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@GameTestHolder(EurekaMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class EurekaShipTests {

    private static final Logger LOGGER = LoggerFactory.getLogger("EurekaShipTests");

    /**
     * Simulates a player opening the helm GUI and clicking "Assemble".
     * Returns the new ServerShip, or null if no new ship was created.
     */
    private static ServerShip simulateHelmAssemble(GameTestHelper helper, ServerLevel level, BlockPos helmRelPos) {
        BlockPos absPos = helper.absolutePos(helmRelPos);
        ShipHelmBlockEntity blockEntity = (ShipHelmBlockEntity) level.getBlockEntity(absPos);
        if (blockEntity == null) return null;

        Set<Long> beforeIds = new HashSet<>();
        for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
            beforeIds.add(ship.getId());
        }

        FakePlayer fakePlayer = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "[GameTest]"));
        blockEntity.assemble(fakePlayer);

        for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
            if (!beforeIds.contains(ship.getId())) {
                return (ServerShip) ship;
            }
        }
        return null;
    }

    @GameTest(template = "empty_platform", batch = "shipAssemblyCreatesShip")
    public void shipAssemblyCreatesShip(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
        helper.assertBlockPresent(EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get(), helmPos);

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null — no ship created");
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

            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null for helm block");
                return;
            }

            LOGGER.info("Ship assembled via helm interaction (id={})", ship.getId());
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

            // BFS from helm will collect the adjacent balloon too
            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null");
                return;
            }

            LOGGER.info("Ship assembled via helm with balloon (id={})", ship.getId());
            helper.assertBlockPresent(Blocks.AIR, helmPos);
            helper.assertBlockPresent(Blocks.AIR, balloonPos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_platform", batch = "assembleEmptyBlocksFails")
    public void assembleEmptyBlocksFails(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            // No helm placed — there is no ShipHelmBlockEntity to trigger assembly
            BlockPos airPos = new BlockPos(1, 2, 1);
            BlockPos absPos = helper.absolutePos(airPos);

            ShipHelmBlockEntity blockEntity = (ShipHelmBlockEntity) level.getBlockEntity(absPos);
            if (blockEntity != null) {
                helper.fail("Expected no helm block entity at air position, found one");
                return;
            }
            // No helm → no assembly possible; this is the expected failure case
            helper.succeed();
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

            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null");
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
                ServerShip ship = simulateHelmAssemble(helper, level, pos);
                if (ship == null) {
                    helper.fail("Helm assembly returned null for ship " + (shipsCreated + 1));
                    return;
                }
                shipsCreated++;
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
     * After a player assembles a ship via the helm, EurekaShipControl.helms should reach 1
     * once the ship appears in VS2's loadedShips and onPlace callbacks have fired.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 400, batch = "helmCountTrackedAfterAssembly")
    public void helmCountTrackedAfterAssembly(GameTestHelper helper) {
        BlockPos helmPos = new BlockPos(1, 1, 1);
        helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null");
                return;
            }

            ChunkPos shipChunk = new ChunkPos(
                ship.getChunkClaim().getXMiddle(),
                ship.getChunkClaim().getZMiddle()
            );
            LOGGER.info("Helm ship assembled via player (id={}), polling for EurekaShipControl.helms...", ship.getId());
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
     * After a player assembles a ship with a helm + 3 balloons via the helm GUI,
     * EurekaShipControl.balloons should reach 3 once the ship appears in VS2's loadedShips.
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

            // BFS from helm collects all 4 adjacent blocks
            ServerShip ship = simulateHelmAssemble(helper, level, helmPos);
            if (ship == null) {
                helper.fail("Helm assembly returned null");
                return;
            }

            ChunkPos shipChunk = new ChunkPos(
                ship.getChunkClaim().getXMiddle(),
                ship.getChunkClaim().getZMiddle()
            );
            LOGGER.info("Balloon ship assembled via player (id={}), polling for EurekaShipControl.balloons...", ship.getId());
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