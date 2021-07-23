package com.katanamajesty;

import com.katanamajesty.events.EnchantEvents;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class Main extends JavaPlugin implements TabExecutor {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F_0-9]{6}"); // паттерн {#??????}
    private String ENABLE = getConfig().getString("enabled_msg");
    private String DISABLE = getConfig().getString("disabled_msg");
    private String NO_PERM = getConfig().getString("no_permissions");
    private final List<String> COMMAND_LIST = new LinkedList<>();

    @Override
    public void onEnable() {
        // Конфиг
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Ивенты & команды
        getServer().getPluginManager().registerEvents(new EnchantEvents(this), this);
        getCommand("tarucaenchants").setExecutor(this);
        getCommand("tarucaenchants").setTabCompleter(this);
        COMMAND_LIST.add("reload");
        // Конфиг
        getLogger().info(colorize(ENABLE));
    }

    @Override
    public void onDisable() {
        getLogger().info(colorize(DISABLE));
    }

    /**
     * @see Main#HEX_PATTERN
     * Заменяет паттерн цвета на цвет
     *
     * @param message необработанная строка с паттернами
     * @return возвращает обработанную строку без паттернов
     */
    public static String colorize(String message) {
        String result = message;

        Matcher hexMatcher = HEX_PATTERN.matcher(message);
        while (hexMatcher.find()) {
            String matchedStr = hexMatcher.group();
            String formattedStr = matchedStr.replaceAll("&", "");
            result = result.replace(matchedStr, ChatColor.of(formattedStr) + "");
        }

        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("reload".equals(args[0])) {
            if (sender.hasPermission("tarucaenchants.reload")) {
                reloadConfig();
                ENABLE = getConfig().getString("enabled_msg");
                DISABLE = getConfig().getString("disabled_msg");
                NO_PERM = getConfig().getString("no_permissions");
                String CFG_RELOADED = getConfig().getString("config_reloaded");
                sender.sendMessage(colorize(CFG_RELOADED));
                return true;
            } else {
                sender.sendMessage(colorize(NO_PERM));
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return COMMAND_LIST.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }
}
