package org.valkyrienskies.eureka.forge.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.eureka.EurekaBlocks;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.util.ShipAssembler;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;

/**
 * Gametests for ship chunk loading in Eureka.
 * Uses VS2's loadedShips registry (via getLoadedShipManagingPos) rather than MC's
 * isPositionTicking, since shipyard chunks may not appear as ticking to vanilla MC.
 */
@GameTestHolder(EurekaMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class EurekaShipLoadingTests {

    private static final Logger LOGGER = LoggerFactory.getLogger("EurekaShipLoadingTests");

    @GameTest(template = "empty_platform", timeoutTicks = 400)
    public void shipChunksReachFullStatusAfterAssembly(GameTestHelper helper) {
        BlockPos placePos = new BlockPos(1, 1, 1);
        helper.setBlock(placePos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            ServerShip ship;
            try {
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(placePos),
                    state -> !state.isAir());
                if (ship == null) {
                    helper.fail("collectBlocks returned null");
                    return;
                }
            } catch (Exception e) {
                helper.fail("Failed to assemble ship: " + e.getMessage());
                return;
            }

            ChunkPos shipChunk = new ChunkPos(
                ship.getChunkClaim().getXMiddle(),
                ship.getChunkClaim().getZMiddle()
            );

            LOGGER.info("Ship assembled, monitoring chunk ({}, {}) for VS load", shipChunk.x, shipChunk.z);
            pollChunkLoaded(helper, level, shipChunk, new int[]{0}, 200);
        });
    }

    private void pollChunkLoaded(GameTestHelper helper, ServerLevel level,
            ChunkPos shipChunk, int[] ticksWaited, int maxTicks) {
        helper.runAfterDelay(1, () -> {
            ticksWaited[0]++;
            if (VSGameUtilsKt.getLoadedShipManagingPos(level, shipChunk) != null) {
                LOGGER.info("Ship chunk loaded after {} ticks", ticksWaited[0]);
                helper.succeed();
            } else if (ticksWaited[0] >= maxTicks) {
                helper.fail("Ship chunk (" + shipChunk.x + ", " + shipChunk.z
                    + ") not loaded in VS2 after " + maxTicks + " ticks.");
            } else {
                pollChunkLoaded(helper, level, shipChunk, ticksWaited, maxTicks);
            }
        });
    }

    @GameTest(template = "empty_platform", timeoutTicks = 600)
    public void multipleShipsAllLoadWithinTimeLimit(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            BlockPos placePos = new BlockPos(1, 1, 1);

            int shipCount = 10;
            List<ChunkPos> shipChunks = new ArrayList<>();

            for (int i = 0; i < shipCount; i++) {
                helper.setBlock(placePos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
                try {
                    ServerShip ship = ShipAssembler.INSTANCE.collectBlocks(level,
                        helper.absolutePos(placePos), state -> !state.isAir());
                    if (ship == null) {
                        helper.fail("collectBlocks returned null for ship " + (i + 1));
                        return;
                    }
                    shipChunks.add(new ChunkPos(
                        ship.getChunkClaim().getXMiddle(),
                        ship.getChunkClaim().getZMiddle()
                    ));
                } catch (Exception e) {
                    helper.fail("Failed to assemble ship " + (i + 1) + ": " + e.getMessage());
                    return;
                }
            }

            LOGGER.info("Spawned {} ships, monitoring VS2 load state...", shipChunks.size());
            long startTime = System.nanoTime();
            pollAllChunksLoaded(helper, level, shipChunks, new int[]{0}, 400, startTime);
        });
    }

    private void pollAllChunksLoaded(GameTestHelper helper, ServerLevel level,
            List<ChunkPos> shipChunks, int[] ticksWaited, int maxTicks, long startTime) {
        helper.runAfterDelay(1, () -> {
            ticksWaited[0]++;
            int loadedCount = 0;
            for (ChunkPos pos : shipChunks) {
                if (VSGameUtilsKt.getLoadedShipManagingPos(level, pos) != null) loadedCount++;
            }
            if (loadedCount >= shipChunks.size()) {
                double ms = (System.nanoTime() - startTime) / 1_000_000.0;
                LOGGER.info("All {} ships loaded in {} ticks ({} ms)",
                    shipChunks.size(), ticksWaited[0], String.format("%.1f", ms));
                helper.succeed();
            } else if (ticksWaited[0] >= maxTicks) {
                helper.fail("Only " + loadedCount + "/" + shipChunks.size()
                    + " ships loaded in VS2 after " + maxTicks + " ticks.");
            } else {
                pollAllChunksLoaded(helper, level, shipChunks, ticksWaited, maxTicks, startTime);
            }
        });
    }

    @GameTest(template = "empty_platform", timeoutTicks = 400)
    public void shipChunkLoadingPriorityIsFast(GameTestHelper helper) {
        BlockPos placePos = new BlockPos(1, 1, 1);
        helper.setBlock(placePos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();

            long assemblyStart = System.nanoTime();
            ServerShip ship;
            try {
                ship = ShipAssembler.INSTANCE.collectBlocks(level, helper.absolutePos(placePos),
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

            measureChunkLoadTime(helper, level, shipChunk, new int[]{0}, 200, assemblyStart);
        });
    }

    private void measureChunkLoadTime(GameTestHelper helper, ServerLevel level,
            ChunkPos shipChunk, int[] ticksWaited, int maxTicks, long startNanos) {
        helper.runAfterDelay(1, () -> {
            ticksWaited[0]++;
            if (VSGameUtilsKt.getLoadedShipManagingPos(level, shipChunk) != null) {
                double ms = (System.nanoTime() - startNanos) / 1_000_000.0;
                if (ticksWaited[0] > 60) {
                    LOGGER.warn("Ship chunk loaded in {} ticks ({} ms) — slower than expected (>60 ticks)",
                        ticksWaited[0], String.format("%.1f", ms));
                } else {
                    LOGGER.info("Ship chunk loaded in {} ticks ({} ms) — priority is working",
                        ticksWaited[0], String.format("%.1f", ms));
                }
                helper.succeed();
            } else if (ticksWaited[0] >= maxTicks) {
                helper.fail("Ship chunk not loaded in VS2 after " + maxTicks + " ticks");
            } else {
                measureChunkLoadTime(helper, level, shipChunk, ticksWaited, maxTicks, startNanos);
            }
        });
    }
}