package lu.ewelohd.minecraftplugin.extendedbellrange.commands;

import lu.ewelohd.minecraftplugin.extendedbellrange.ExtendedBellRange;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class BellCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private final int defaultVolume;
    private final float defaultPitch;

    public BellCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.defaultVolume = config.getInt("defaultVolume");
        this.defaultPitch = (float) config.getDouble("defaultPitch");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("add")) {
                return onBellAddCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if (args[0].equalsIgnoreCase("edit")) {
                return onBellEditCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if (args[0].equalsIgnoreCase("remove")) {
                return onBellRemoveCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if (args[0].equalsIgnoreCase("list")) {
                return onBellListCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            } else {
                return onBellHelpCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return false;
    }

    private boolean onBellAddCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player p) {
            Block b = p.getTargetBlock(6);
            if (b != null && b.getType() == Material.BELL) {
                if (ExtendedBellRange.getConfigSectionByBell(config, b) == null) {
                    int key = getNextKey();

                    ConfigurationSection bellSection = config.createSection("bells." + key);
                    ConfigurationSection locationSection = bellSection.createSection("location");

                    locationSection.set("world", b.getWorld().getName());
                    locationSection.set("x", b.getX());
                    locationSection.set("y", b.getY());
                    locationSection.set("z", b.getZ());

                    int volume = (args.length > 0 && isInteger(args[0])) ? Integer.parseInt(args[0]) : defaultVolume;
                    float pitch = (args.length > 1 && isFloat(args[1])) ? Float.parseFloat(args[1]) : defaultPitch;

                    bellSection.set("volume", volume);
                    bellSection.set("pitch", pitch);

                    plugin.saveConfig();

                    sender.sendMessage(ExtendedBellRange.PREFIX + "§r§f Extended Range for §bBell§7 (" + b.getX() + ", " + b.getY() + ", " + b.getZ() + ")§r§f.§r");
                } else {
                    sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This Bell was already added!");
                }
            } else {
                sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c You need to target a Bell!");
            }
        } else {
            sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This command can only be executed by players!");
        }
        return true;
    }

    private boolean onBellEditCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player p) {
            Block b = p.getTargetBlock(6);
            if (b != null && b.getType() == Material.BELL) {
                ConfigurationSection bellSection = ExtendedBellRange.getConfigSectionByBell(config, b);
                if (bellSection != null) {
                    int volume = (args.length > 0 && isInteger(args[0])) ? Integer.parseInt(args[0]) : bellSection.getInt("volume");
                    float pitch = (args.length > 1 && isFloat(args[1])) ? Float.parseFloat(args[1]) : (float) bellSection.getDouble("pitch");

                    bellSection.set("volume", volume);
                    bellSection.set("pitch", pitch);

                    plugin.saveConfig();

                    sender.sendMessage(ExtendedBellRange.PREFIX + "§r§f Edited §bBell§7 (" + b.getX() + ", " + b.getY() + ", " + b.getZ() + ")§r§f.§r");
                } else {
                    sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This Bell was not added yet!");
                }
            } else {
                sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c You need to target a Bell!");
            }
        } else {
            sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This command can only be executed by players!");
        }
        return true;
    }

    private boolean onBellRemoveCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player p) {
            Block b = p.getTargetBlock(6);
            if (b != null && b.getType() == Material.BELL) {
                ConfigurationSection bellSection = ExtendedBellRange.getConfigSectionByBell(config, b);
                if (bellSection != null) {
                    bellSection.getParent().set(bellSection.getName(), null);

                    plugin.saveConfig();

                    sender.sendMessage(ExtendedBellRange.PREFIX + "§r§f Removed §bBell§7 (" + b.getX() + ", " + b.getY() + ", " + b.getZ() + ")§r§f.§r");
                } else {
                    sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This Bell already got removed or was not added yet!");
                }
            } else {
                sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c You need to target a Bell!");
            }
        } else {
            sender.sendMessage(ExtendedBellRange.PREFIX + " §c§lError:§r§c This command can only be executed by players!");
        }
        return true;
    }

    private boolean onBellListCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (config.getConfigurationSection("bells") == null || config.getConfigurationSection("bells").getKeys(false).isEmpty()) {
            sender.sendMessage(ExtendedBellRange.PREFIX + "§r§f You have not added any §bBells§r§f yet!§r");
        } else {
            sender.sendMessage(ExtendedBellRange.PREFIX + "§r§f Listing all §bBells§r§f:§r");


            Set<String> keys = config.getConfigurationSection("bells").getKeys(false);
            for (String key : keys) {
                try {
                    ConfigurationSection bellSection = config.getConfigurationSection("bells").getConfigurationSection(key);
                    ConfigurationSection locationSection = bellSection.getConfigurationSection("location");

                    String world = locationSection.getString("world");
                    int x = locationSection.getInt("x");
                    int y = locationSection.getInt("y");
                    int z = locationSection.getInt("z");

                    int volume = bellSection.getInt("volume");
                    double pitch = bellSection.getDouble("pitch");

                    sender.sendMessage("§f - §bId:§f " + key + "§7,§b Location:§7 [§f" + world + ", " + x + ", " + y + ", " + z + "§7],§b Volume:§f " + volume + "§7,§b Pitch:§f " + pitch);
                } catch (NullPointerException ignore) {

                }
            }
        }

        return true;
    }

    private boolean onBellHelpCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        return false;
    }

    private int getNextKey() {
        try {
            Set<String> keys = config.getConfigurationSection("bells").getKeys(false);
            if (keys.isEmpty()) {
                return 0;
            } else {
                int i = 0;
                Iterator<String> itr = keys.iterator();
                while (itr.hasNext()) {
                    try {
                        i = Math.max(i, Integer.parseInt(itr.next()));
                    } catch (NumberFormatException ignored) {

                    }
                }
                return i + 1;
            }
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
