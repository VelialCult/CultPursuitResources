package ru.velialcult.pursuitresources;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.pursuitresources.command.CultPursuitResourcesCommand;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.providers.ProvidersManager;
import ru.velialcult.pursuitresources.providers.placeholderapi.PlaceholderApiProvider;
import ru.velialcult.pursuitresources.service.EventService;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.11.2024
 * ⏰ Время создания: 21:12
 */

public class CultPursuitResources extends JavaPlugin {

    private static CultPursuitResources instance;

    private EventService eventService;

    @Override
    public void onEnable() {
        instance = this;

        long mills = System.currentTimeMillis();

        try {

            this.saveDefaultConfig();

            getLogger().info(" ");
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fИдёт загрузка плагина &#FFA500CultPursuitResources " + getDescription().getVersion()));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fСпасибо вам от &#FFD700VelialCult&r&f, что выбрали наш плагин!"));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fВсю полезную информацию Вы сможете найти в нашем Discord сервере"));
            getLogger().info(VersionAdapter.TextUtil().colorize("  &fСсылка на Discord сервер: &#FFA500https://dsc.gg/velialcult"));
            getLogger().info(" ");

            ProvidersManager providersManager = new ProvidersManager(this);
            providersManager.load();

            Plugin plugin = Bukkit.getPluginManager().getPlugin("CultSeller");
            if (plugin == null) {
                getLogger().severe(VersionAdapter.TextUtil().colorize("  &#DC143CОшибка! &fПлагин &#32CD32CultSeller &fне найден, отключаю плагин!"));
                Bukkit.getPluginManager().disablePlugin(this);
            } else {
                getLogger().info(" ");
                getLogger().info(VersionAdapter.TextUtil().colorize("  &#00FF7F&lУспешно! &fПлагин &#32CD32CultSeller &fнайден, продолжаю загрузку!"));
                getLogger().info(" ");
            }

            EventConfig eventConfig = new EventConfig(this);

            eventService = new EventService(this, eventConfig);

            if (providersManager.usePlaceholderAPI()) {
                new PlaceholderApiProvider(eventService).register();
            }

            CultPursuitResourcesCommand command = new CultPursuitResourcesCommand(eventConfig, eventService);
            Bukkit.getPluginCommand("cultpursuitresources").setExecutor(command);
            Bukkit.getPluginCommand("cultpursuitresources").setTabCompleter(command);

            getLogger().info(VersionAdapter.TextUtil().colorize("  &#00FF7F&lОтлично! &fПлагин был загружен за &#FFA500" + (System.currentTimeMillis() - mills)  + "ms"));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static CultPursuitResources getInstance() {
        return instance;
    }

    public EventService getEventService() {
        return eventService;
    }
}
