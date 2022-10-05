package org.valkyrienskies.eureka.fabric;

import net.fabricmc.loader.api.FabricLoader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.valkyrienskies.dependency_downloader.DependencyDownloader;
import org.valkyrienskies.dependency_downloader.DependencyMatchResult;
import org.valkyrienskies.dependency_downloader.ModDependency;
import org.valkyrienskies.dependency_downloader.matchers.StandardMatchers;

public class AutoDependenciesFabric {

    public static void checkDependencies() {
        try {
            final List<ModDependency> dependencies = Stream.of(
                    StandardMatchers.Fabric16.FABRIC_KOTLIN,
                    StandardMatchers.Fabric16.CLOTH_CONFIG,
                    StandardMatchers.Fabric16.ARCHITECTURY_API,
                    StandardMatchers.Fabric16.MOD_MENU,
                    StandardMatchers.Fabric16.FABRIC_API,
                    StandardMatchers.fabricValkyrienSkiesDependency(">=2.0.0", "2.0.0+f5f1077025")
                )
                .filter(dep -> // remove any dependencies that are already loaded by fabric
                    FabricLoader.getInstance().getAllMods()
                        .stream()
                        .noneMatch(loadedMod ->
                            dep.getMatcher().matches(loadedMod.getRoot().getFileSystem()) == DependencyMatchResult.FULFILLED
                        ))
                .collect(Collectors.toList());

            System.setProperty("java.awt.headless", "false");
            new DependencyDownloader(FabricLoader.getInstance().getGameDir().resolve("mods"), dependencies).promptToDownload();
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

}
