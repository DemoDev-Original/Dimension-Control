package com.dimensioncontrol;

import com.dimensioncontrol.command.DimensionControlCommand;
import com.dimensioncontrol.config.DimensionConfig;
import com.dimensioncontrol.permission.PermissionManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionControl implements ModInitializer {
    public static final String MOD_ID = "dimensioncontrol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static DimensionConfig config;
    private static PermissionManager permissionManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Dimension Control");
        
        config = DimensionConfig.load();
        permissionManager = new PermissionManager();
        
        DimensionControlCommand.register();
        
        LOGGER.info("Dimension Control initialized successfully");
    }
    
    public static DimensionConfig getConfig() {
        return config;
    }
    
    public static PermissionManager getPermissionManager() {
        return permissionManager;
    }
}