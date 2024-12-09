package ru.velialcult.pursuitresources.providers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.velialcult.library.bukkit.utils.VersionUtil;
import ru.velialcult.library.core.VersionAdapter;

import java.util.HashMap;
import java.util.Map;

public class ProvidersManager {

    private final Map<String, Boolean> providers = new HashMap<>();
    private final Plugin plugin;

    public ProvidersManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadProvider("PlaceholderAPI", "2.11.5");
    }

    private void loadProvider(String pluginName, String minVersion) {
        boolean isPluginLoaded = false;
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            String version = plugin.getDescription().getVersion();

            if (VersionUtil.isVersionGreaterOrEqual(version, minVersion)) {
                this.plugin.getLogger().info(VersionAdapter.TextUtil().colorize("  &#00FF7F&lОтлично! &r&fПоддержка плагина &#FF8C00" + pluginName  + " &r&fуспешно активирована"));
                isPluginLoaded = true;
            } else {
                this.plugin.getLogger().info(VersionAdapter.TextUtil().colorize("  &#DC143CВнимание! &r&fДля поддержки плагина &#FF8C00" + pluginName + " &r&fнеобходима версия не ниже &#DC143C" + minVersion));
            }
        }
        providers.put(pluginName, isPluginLoaded);
    }

    public boolean usePlaceholderAPI() {
        return providers.getOrDefault("PlaceholderAPI", false);
    }

}
