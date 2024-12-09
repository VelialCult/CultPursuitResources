package ru.velialcult.pursuitresources.runnable;

import org.bukkit.scheduler.BukkitRunnable;
import ru.velialcult.pursuitresources.CultPursuitResources;
import ru.velialcult.pursuitresources.data.EventData;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.service.EventService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 11:59
 */

public class EventRunnable extends BukkitRunnable {

    private final CultPursuitResources plugin;
    private final EventConfig eventConfig;
    private final EventService eventService;
    private final EventData eventData;

    public EventRunnable(CultPursuitResources plugin, EventConfig eventConfig, EventService eventService) {
        this.plugin = plugin;
        this.eventConfig = eventConfig;
        this.eventService = eventService;
        this.eventData = eventService.getEventData();
    }

    @Override
    public void run() {
        try {
            new BukkitRunnable() {
                public void run() {
                    LocalDateTime now = LocalDateTime.now();
                    for (LocalTime time : eventConfig.getTimeList()) {
                        LocalDateTime dateTime = LocalDate.now().atTime(time);
                        if (dateTime.isBefore(now) && dateTime.plusSeconds(1L).isAfter(now)) {
                            eventService.startEvent();
                        }
                    }

                    if (eventData.getTimeStartEvent() != null && eventData.isEventActive() && LocalDateTime.now().isAfter(eventData.getTimeStartEvent().plusSeconds(eventConfig.getEventTime()))) {
                        eventService.endEvent();
                    }
                }
            }.runTaskTimer(plugin, 1L, 20L);
        } catch (Exception e) {
            plugin.getLogger().severe("Произошла ошибка при работе события: " + e.getMessage());
        }
    }
}
