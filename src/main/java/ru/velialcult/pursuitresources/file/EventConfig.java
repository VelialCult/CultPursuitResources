package ru.velialcult.pursuitresources.file;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.bukkit.file.FileOperations;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pursuitresources.CultPursuitResources;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.11.2024
 * ⏰ Время создания: 21:38
 */

public class EventConfig {

    private final FileConfiguration config;
    private final CultPursuitResources plugin;

    private final List<LocalTime> timeList = new ArrayList<>();
    private final Map<String, List<String>> rewards;
    private final long eventTime;
    private final long maxEventItems;

    private FileOperations fileOperations;
    private final Map<String, String> messages;

    public EventConfig(CultPursuitResources plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.maxEventItems = this.config.getLong("settings.items.maxEventItems");
        this.eventTime = TimeUtil.parseStringToTime(config.getString("settings.event.time"));
        this.timeList.addAll(convertToTimeList(config, "settings.event.start-times"));
        this.rewards = loadRewardsFromConfig();
        this.messages = new HashMap<>();
        loadMessages();
    }

    public List<LocalTime> getTimeList() {
        return timeList;
    }

    private List<LocalTime> convertToTimeList(FileConfiguration config, String path) {
        try {
            List<String> timeStrings = config.getStringList(path);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return timeStrings.stream()
                    .map(time -> LocalTime.parse(time, formatter))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            this.plugin.getLogger().warning("Произошла ошибка при загрузки времени: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, List<String>> loadRewardsFromConfig() {
        return Objects.requireNonNull(this.config.getConfigurationSection("settings.event.rewards"))
                .getKeys(false)
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> this.config.getStringList("settings.event.rewards." + key + ".commands")));
    }

    private void loadMessages() {
        messages.clear();
        ConfigurationSection section = config.getConfigurationSection("messages");
        if (section != null) {
            (Objects.requireNonNull(section)).getKeys(true).forEach((path) -> {
                String fullPath = "messages." + path;
                if (config.isList(fullPath)) {
                    List<String> lines = config.getStringList(fullPath);
                    messages.put(fullPath, String.join("\n", lines));
                } else {
                    String value = config.getString(fullPath);
                    messages.put(fullPath, value);
                }

            });
        }

        this.fileOperations = new FileOperations(messages);
    }

    public Map<String, List<String>> getRewards() {
        return rewards;
    }

    public long getEventTime() {
        return eventTime;
    }

    public long getMaxEventItems() {
        return maxEventItems;
    }

    public FileOperations getFileOperations() {
        return fileOperations;
    }
}
