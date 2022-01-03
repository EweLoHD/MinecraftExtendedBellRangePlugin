package lu.ewelohd.minecraftplugin.extendedbellrange;

import lu.ewelohd.minecraftplugin.extendedbellrange.commands.BellCommand;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class ExtendedBellRange extends JavaPlugin {

    public static String PREFIX = "§7[§bBell Range Extender§7]§r";

    @Override
    public void onEnable() {
        getConfig().addDefault("defaultVolume", 50);
        getConfig().addDefault("defaultPitch", 1.0f);
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new BellListener(this), this);
        getServer().getPluginManager().registerEvents(new BellBreakListener(this), this);

        getCommand("bell").setExecutor(new BellCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ConfigurationSection getConfigSectionByBell(FileConfiguration config, Block block) {
        return getConfigSectionByBell(config, block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public static ConfigurationSection getConfigSectionByBell(FileConfiguration config, String world, int x, int y, int z) {
        if (config.getConfigurationSection("bells") == null) {
            return null;
        }

        Set<String> keys = config.getConfigurationSection("bells").getKeys(false);
        for (String key : keys) {
            try {
                ConfigurationSection locationSection = config.getConfigurationSection("bells")
                        .getConfigurationSection(key)
                        .getConfigurationSection("location");

                if (world.equalsIgnoreCase(locationSection.getString("world")) && x == locationSection.getInt("x") && y == locationSection.getInt("y") && z == locationSection.getInt("z")) {
                    return config.getConfigurationSection("bells").getConfigurationSection(key);
                }
            } catch (NullPointerException ignore) {

            }
        }

        return null;
    }
}
