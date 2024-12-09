package ru.velialcult.pursuitresources.reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.velialcult.pursuitresources.data.EventSalesData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 12:06
 */

public class RewardManager {

    private final Map<String, List<String>> rewards;

    public RewardManager(Map<String, List<String>> rewards) {
        this.rewards = rewards;
    }

    public void giveRewards(List<Map.Entry<Integer, EventSalesData>> topPlayers) {
        for (Map.Entry<Integer, EventSalesData> entry : topPlayers) {
            int rank = entry.getKey();
            UUID playerId = entry.getValue().getPlayerId();
            Player player = Bukkit.getPlayer(playerId);
            for (Map.Entry<String, List<String>> rewardEntry : rewards.entrySet()) {
                String key = rewardEntry.getKey();
                if (key.contains("-")) {
                    String[] ranks = key.split("-");
                    int minRank = Integer.parseInt(ranks[0]);
                    int maxRank = Integer.parseInt(ranks[1]);
                    if (rank >= minRank && rank <= maxRank) {
                        for (String command : rewardEntry.getValue()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                        }
                    }
                } else {
                    int minRank = Integer.parseInt(key);
                    if (rank == minRank) {
                        for (String command : rewardEntry.getValue()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                        }
                    }
                }
            }
        }
    }
}
