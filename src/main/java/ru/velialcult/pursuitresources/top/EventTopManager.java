package ru.velialcult.pursuitresources.top;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.velialcult.pursuitresources.data.EventData;
import ru.velialcult.pursuitresources.data.EventSalesData;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.service.EventService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 12:02
 */

public class EventTopManager {

    private final EventConfig eventConfig;
    private final EventData eventData;

    public EventTopManager(EventService eventService, EventConfig eventConfig) {
        this.eventConfig = eventConfig;
        this.eventData = eventService.getEventData();
    }

    public List<Map.Entry<Integer, EventSalesData>> getTopPlayers() {
        List<EventSalesData> sortedSales = this.eventData.getPlayerSales().values().stream()
                .sorted(Comparator.comparingLong(eventPlayerSales -> ((EventSalesData) eventPlayerSales).getSales().values()
                                .stream()
                                .mapToLong(Long::longValue).sum())
                        .reversed())
                .collect(Collectors.toList());
        List<Map.Entry<Integer, EventSalesData>> topPlayers = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sortedSales.size()); ++i) {
            topPlayers.add(new AbstractMap.SimpleEntry<>(i + 1, sortedSales.get(i)));
        }
        return topPlayers;
    }

    public int getPlayerRank(UUID playerId) {
        List<EventSalesData> sortedSales = eventData.getPlayerSales().values().stream()
                .sorted(Comparator.comparingLong(eventPlayerSales -> ((EventSalesData) eventPlayerSales).getSales().values().stream().mapToLong(Long::longValue).sum()).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < sortedSales.size(); ++i) {
            if (sortedSales.get(i).getPlayerId().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    public List<String> getTopThreePlayersFormatted() {
        String topFormat = eventConfig.getFileOperations().getString("messages.event.top-format");
        List<String> top = new ArrayList<>();
        List<Map.Entry<Integer, EventSalesData>> topThreePlayers = this.getTopPlayers();
        for (int i = 0; i < 3; ++i) {
            if (i < topThreePlayers.size()) {
                Map.Entry<Integer, EventSalesData> entry = topThreePlayers.get(i);
                top.add(topFormat.replace("{number}", String.valueOf(entry.getKey())).replace("{player}", Bukkit.getPlayer(entry.getValue().getPlayerId()).getName()).replace("{sales}", String.valueOf(entry.getValue().getTotalSales())));
            } else {
                top.add("Пусто");
            }
        }
        return top;
    }
}
