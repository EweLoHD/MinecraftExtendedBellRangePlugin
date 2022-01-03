package lu.ewelohd.minecraftplugin.extendedbellrange;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BellBreakListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public BellBreakListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onBellBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (b.getType() == Material.BELL) {
            ConfigurationSection bellSection = ExtendedBellRange.getConfigSectionByBell(config, b);
            if (bellSection != null) {
                bellSection.getParent().set(bellSection.getName(), null);

                plugin.saveConfig();

                event.getPlayer().sendMessage(ExtendedBellRange.PREFIX + "§r§f Removed §bBell§7 (" + b.getX() + ", " + b.getY() + ", " + b.getZ() + ")§r§f.§r");
            }
        }
    }
}
