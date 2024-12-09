package ru.velialcult.pursuitresources.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pursuitresources.CultPursuitResources;
import ru.velialcult.pursuitresources.data.EventData;
import ru.velialcult.pursuitresources.data.EventSalesData;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.manager.SellManager;
import ru.velialcult.pursuitresources.reward.RewardManager;
import ru.velialcult.pursuitresources.runnable.EventRunnable;
import ru.velialcult.pursuitresources.top.EventTopManager;
import ru.velialcult.seller.CultSeller;
import ru.velialcult.seller.shop.ShopStorage;
import ru.velialcult.seller.shop.item.ShopItemSelector;
import ru.velialcult.seller.shop.item.ShopItemType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class EventService {

    private final EventData eventData;
    private final EventConfig eventConfig;
    private final EventTopManager eventTopManager;
    private final RewardManager rewardManager;
    private final ShopItemSelector shopItemSelector;
    private final SellManager sellManager;

    public EventService(CultPursuitResources plugin, EventConfig eventConfig) {
        this.eventData = new EventData();
        this.eventConfig = eventConfig;
        EventRunnable eventRunnable = new EventRunnable(plugin, eventConfig, this);
        eventRunnable.run();
        this.eventTopManager = new EventTopManager(this, eventConfig);
        this.rewardManager = new RewardManager(eventConfig.getRewards());
        this.sellManager = new SellManager(eventConfig, this);

        ShopStorage shopStorage = CultSeller.getInstance().getShopStorage();
        this.shopItemSelector = shopStorage.getItemSelector();
    }

    public void startEvent() {
        if (!eventData.isEventActive()) {
            eventData.setEventActive(true);
            eventData.getEventItems().addAll(shopItemSelector.selectItems(eventConfig.getMaxEventItems(), ShopItemType.EVENT));
            eventData.setTimeStartEvent(LocalDateTime.now());
            for (Player player : Bukkit.getOnlinePlayers()) {
                VersionAdapter.MessageUtils().sendMessage(player, eventConfig.getFileOperations().getList("messages.event.start"));
            }
        }
    }

    public void endEvent() {
        if (eventData.isEventActive()) {
            eventData.setEventActive(false);
            rewardManager.giveRewards(eventTopManager.getTopPlayers());

            for (Player player : Bukkit.getOnlinePlayers()) {
                VersionAdapter.MessageUtils().sendMessage(player, eventConfig.getFileOperations().getList("messages.event.end",
                        new ReplaceData("{player-top}", eventTopManager.getPlayerRank(player.getUniqueId())),
                        new ReplaceData("{top}", String.join("\n", eventTopManager.getTopThreePlayersFormatted()))));
            }

            eventData.clear();
        }
    }

    public String getTimeToNextEvent() {

        if (eventData.isEventActive()) {
            return "Проходит";
        }

        LocalTime now = LocalDateTime.now().toLocalTime();
        LocalTime nearestFutureEvent = findNearestFutureTime(now, eventConfig.getTimeList());

        if (nearestFutureEvent != null) {
            Duration duration = Duration.between(now, nearestFutureEvent);
            return TimeUtil.getTime(duration.toSeconds());
        }

        return "Завтра";
    }

    private LocalTime findNearestFutureTime(LocalTime currentTime, List<LocalTime> times) {
        LocalTime nearestTime = null;

        for (LocalTime time : times) {
            if (time.isAfter(currentTime)) {
                if (nearestTime == null || time.isBefore(nearestTime)) {
                    nearestTime = time;
                }
            }
        }

        return nearestTime;
    }

    public EventData getEventData() {
        return eventData;
    }

    public EventSalesData getEventPlayerSales(UUID uuid) {
        return eventData.getPlayerSales(uuid);
    }

    public EventTopManager getEventTopManager() {
        return eventTopManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public SellManager getSellManager() {
        return sellManager;
    }
}
