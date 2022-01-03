package lu.ewelohd.minecraftplugin.extendedbellrange;

import io.papermc.paper.event.block.BellRingEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class BellListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public BellListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onBellRing(BellRingEvent event) {
        ConfigurationSection bellSection = ExtendedBellRange.getConfigSectionByBell(config, event.getBlock());

        if (bellSection != null) {
            playBellSounds(event.getBlock(), bellSection.getInt("volume"), (float) bellSection.getDouble("pitch"));
            event.setCancelled(true);
        }
    }

    private void playBellSounds(Block b, int volume, float pitch) {
        Collection<Entity> nearbyEntities = b.getWorld().getNearbyEntities(b.getLocation(), volume, volume, volume);

        new BukkitRunnable() {
            @Override
            public void run() {

                for(Entity entity : nearbyEntities) {
                    if (entity instanceof Player player) {
                        player.playSound(b.getLocation(), Sound.BLOCK_BELL_USE, (float) volume * 0.005f, pitch);
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}
