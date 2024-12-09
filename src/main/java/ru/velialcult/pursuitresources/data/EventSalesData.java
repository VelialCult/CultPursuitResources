package ru.velialcult.pursuitresources.data;

import ru.velialcult.seller.shop.item.ShopItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventSalesData {

    private final UUID playerId;
    private final Map<ShopItem, Long> sales;

    public EventSalesData(UUID playerId) {
        this.playerId = playerId;
        this.sales = new HashMap<>();
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Map<ShopItem, Long> getSales() {
        return this.sales;
    }

    public void recordSale(ShopItem item, long amount) {
        this.sales.put(item, this.sales.getOrDefault(item, 0L) + amount);
    }

    public long getSales(ShopItem eventItem) {
        return this.sales.getOrDefault(eventItem, 0L);
    }

    public long getTotalSales() {
        return this.sales.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();
    }
}
