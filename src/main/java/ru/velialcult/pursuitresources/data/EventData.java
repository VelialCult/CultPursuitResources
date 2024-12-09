package ru.velialcult.pursuitresources.data;

import ru.velialcult.seller.shop.item.ShopItem;

import java.time.LocalDateTime;
import java.util.*;

public class EventData {

    private LocalDateTime timeStartEvent;
    private boolean isEventActive;
    private final Map<UUID, EventSalesData> playerSales;
    private final List<ShopItem> eventItems;

    public EventData() {
        this.isEventActive = false;
        this.playerSales = new HashMap<>();
        this.eventItems = new ArrayList<>();
    }

    public void clear() {
        playerSales.clear();
    }

    public EventSalesData getPlayerSales(UUID uuid) {
        if (!playerSales.containsKey(uuid)) {
            playerSales.put(uuid, new EventSalesData(uuid));
        }
        return playerSales.getOrDefault(uuid, new EventSalesData(uuid));
    }

    public void setTimeStartEvent(LocalDateTime timeStartEvent) {
        this.timeStartEvent = timeStartEvent;
    }

    public void setEventActive(boolean eventActive) {
        isEventActive = eventActive;
    }

    public boolean isEventActive() {
        return isEventActive;
    }

    public LocalDateTime getTimeStartEvent() {
        return timeStartEvent;
    }

    public Map<UUID, EventSalesData> getPlayerSales() {
        return playerSales;
    }

    public List<ShopItem> getEventItems() {
        return eventItems;
    }
}
