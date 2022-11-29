package org.valkyrienskies.eureka.forge;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.valkyrienskies.dependency_downloader.ValkyrienDependencyDownloader;

public class AutoDependenciesForge {
    public static void runUpdater() {
        boolean isServer = FMLEnvironment.dist.isDedicatedServer();

        ValkyrienDependencyDownloader.start(
            FMLPaths.MODSDIR.get(),
            FMLLoader.getLoadingModList().getModFileById("vs_eureka").getFile().getFilePath(),
            isServer
        );
    }
}
