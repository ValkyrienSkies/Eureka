package org.valkyrienskies.eureka.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.valkyrienskies.eureka.fabric.AutoDependenciesFabric;

/**
 * For now, just using this class as an abusive early entrypoint to run the updater
 */
public class ValkyrienPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        AutoDependenciesFabric.runUpdater();
    }
}
