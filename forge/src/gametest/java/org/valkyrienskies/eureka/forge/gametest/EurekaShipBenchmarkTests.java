package org.valkyrienskies.eureka.forge.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.eureka.EurekaBlocks;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity;

import java.util.UUID;

/**
 * Performance benchmarks for Eureka ships.
 * These tests don't assert correctness — they measure and log timing data
 * so regressions in spawn throughput or tick performance are visible in CI output.
 *
 * Each ship is assembled by simulating a player opening the helm GUI and clicking "Assemble",
 * which goes through {@link ShipHelmBlockEntity#assemble} — the same path as real gameplay.
 */
@GameTestHolder(EurekaMod.MOD_ID)
@PrefixGameTestTemplate(false)
public class EurekaShipBenchmarkTests {

    private static final Logger LOGGER = LoggerFactory.getLogger("EurekaShipBenchmark");
    private static final int SHIP_COUNT = 100;
    private static final int TICK_COUNT = 50;

    /**
     * Spawns SHIP_COUNT single-helm ships via player helm interaction,
     * then measures tick performance over TICK_COUNT ticks.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 60000, batch = "benchmarkSpawnShipsAndTick")
    public void benchmarkSpawnShipsAndTick(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            BlockPos helmPos = new BlockPos(1, 1, 1);
            BlockPos absPos = helper.absolutePos(helmPos);
            FakePlayer fakePlayer = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "[GameTest]"));

            long spawnStartNanos = System.nanoTime();
            int shipsSpawned = 0;

            for (int i = 0; i < SHIP_COUNT; i++) {
                helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
                ShipHelmBlockEntity blockEntity = (ShipHelmBlockEntity) level.getBlockEntity(absPos);
                if (blockEntity == null) {
                    helper.fail("No ShipHelmBlockEntity at ship " + (i + 1));
                    return;
                }
                blockEntity.assemble(fakePlayer);
                if (!level.getBlockState(absPos).isAir()) {
                    helper.fail("Assembly did not remove helm block at ship " + (i + 1));
                    return;
                }
                shipsSpawned++;
            }

            long spawnElapsedNanos = System.nanoTime() - spawnStartNanos;
            double spawnMs = spawnElapsedNanos / 1_000_000.0;

            LOGGER.info("========================================");
            LOGGER.info("EUREKA BENCHMARK: Ship Spawning");
            LOGGER.info("========================================");
            LOGGER.info("Ships spawned:    {}", shipsSpawned);
            LOGGER.info("Total spawn time: {}", String.format("%.2f ms", spawnMs));
            LOGGER.info("Avg per ship:     {}", String.format("%.2f ms", spawnMs / shipsSpawned));
            LOGGER.info("Throughput:       {}", String.format("%.1f ships/sec", shipsSpawned / (spawnMs / 1000.0)));
            LOGGER.info("========================================");

            final int finalShipsSpawned = shipsSpawned;
            final double finalSpawnMs = spawnMs;
            long tickStartNanos = System.nanoTime();

            helper.runAfterDelay(TICK_COUNT, () -> {
                long tickElapsedNanos = System.nanoTime() - tickStartNanos;
                double tickMs = tickElapsedNanos / 1_000_000.0;

                LOGGER.info("========================================");
                LOGGER.info("EUREKA BENCHMARK: Tick Performance");
                LOGGER.info("========================================");
                LOGGER.info("Ticks elapsed:    {}", TICK_COUNT);
                LOGGER.info("Ships loaded:     {}", finalShipsSpawned);
                LOGGER.info("Total tick time:  {}", String.format("%.2f ms", tickMs));
                LOGGER.info("Avg per tick:     {}", String.format("%.2f ms", tickMs / TICK_COUNT));
                LOGGER.info("Effective TPS:    {}", String.format("%.1f", TICK_COUNT / (tickMs / 1000.0)));
                LOGGER.info("========================================");
                LOGGER.info("EUREKA BENCHMARK: Summary");
                LOGGER.info("========================================");
                LOGGER.info("Spawn {} ships:   {}", finalShipsSpawned, String.format("%.2f ms", finalSpawnMs));
                LOGGER.info("Run {} ticks:     {}", TICK_COUNT, String.format("%.2f ms", tickMs));
                LOGGER.info("Total benchmark:  {}", String.format("%.2f ms", finalSpawnMs + tickMs));
                LOGGER.info("========================================");

                helper.succeed();
            });
        });
    }

    /**
     * Baseline: measures TICK_COUNT ticks with zero ships loaded.
     * Compare against benchmarkSpawnShipsAndTick to quantify the per-ship overhead.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 60000, batch = "benchmarkBaseline100Ticks")
    public void benchmarkBaseline100Ticks(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            LOGGER.info("========================================");
            LOGGER.info("EUREKA BASELINE: Starting 0-ship tick measurement");
            LOGGER.info("========================================");

            long tickStartNanos = System.nanoTime();

            helper.runAfterDelay(TICK_COUNT, () -> {
                long tickElapsedNanos = System.nanoTime() - tickStartNanos;
                double tickMs = tickElapsedNanos / 1_000_000.0;

                LOGGER.info("========================================");
                LOGGER.info("EUREKA BASELINE: Tick Performance (0 ships)");
                LOGGER.info("========================================");
                LOGGER.info("Ticks elapsed:    {}", TICK_COUNT);
                LOGGER.info("Ships loaded:     0");
                LOGGER.info("Total tick time:  {}", String.format("%.2f ms", tickMs));
                LOGGER.info("Avg per tick:     {}", String.format("%.2f ms", tickMs / TICK_COUNT));
                LOGGER.info("Effective TPS:    {}", String.format("%.1f", TICK_COUNT / (tickMs / 1000.0)));
                LOGGER.info("========================================");

                helper.succeed();
            });
        });
    }

    /**
     * Spawns SHIP_COUNT ships via helm interaction, alternating between single-helm ships and
     * helm+balloon ships, to benchmark assembly with EurekaShipControl attachment callbacks.
     */
    @GameTest(template = "empty_platform", timeoutTicks = 60000, batch = "benchmarkSpawnEurekaBlockShips")
    public void benchmarkSpawnEurekaBlockShips(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            BlockPos helmPos = new BlockPos(1, 1, 1);
            BlockPos balloonPos = new BlockPos(1, 1, 2);
            BlockPos absHelmPos = helper.absolutePos(helmPos);
            FakePlayer fakePlayer = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "[GameTest]"));

            long spawnStartNanos = System.nanoTime();
            int shipsSpawned = 0;

            for (int i = 0; i < SHIP_COUNT; i++) {
                helper.setBlock(helmPos, EurekaBlocks.INSTANCE.getOAK_SHIP_HELM().get().defaultBlockState());
                if (i % 2 == 1) {
                    // Odd ships: helm + balloon (BFS collects both)
                    helper.setBlock(balloonPos, EurekaBlocks.INSTANCE.getBALLOON().get().defaultBlockState());
                }

                ShipHelmBlockEntity blockEntity = (ShipHelmBlockEntity) level.getBlockEntity(absHelmPos);
                if (blockEntity == null) {
                    helper.fail("No ShipHelmBlockEntity at ship " + (i + 1));
                    return;
                }
                blockEntity.assemble(fakePlayer);
                if (!level.getBlockState(absHelmPos).isAir()) {
                    helper.fail("Assembly did not remove helm block at ship " + (i + 1));
                    return;
                }
                shipsSpawned++;
            }

            long spawnElapsedNanos = System.nanoTime() - spawnStartNanos;
            double spawnMs = spawnElapsedNanos / 1_000_000.0;

            LOGGER.info("========================================");
            LOGGER.info("EUREKA BENCHMARK: Eureka Block Ship Spawning");
            LOGGER.info("========================================");
            LOGGER.info("Ships spawned:    {}", shipsSpawned);
            LOGGER.info("Total spawn time: {}", String.format("%.2f ms", spawnMs));
            LOGGER.info("Avg per ship:     {}", String.format("%.2f ms", spawnMs / shipsSpawned));
            LOGGER.info("Throughput:       {}", String.format("%.1f ships/sec", shipsSpawned / (spawnMs / 1000.0)));
            LOGGER.info("========================================");

            helper.succeed();
        });
    }
}