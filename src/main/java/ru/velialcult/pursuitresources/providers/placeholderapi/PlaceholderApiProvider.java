package ru.velialcult.pursuitresources.providers.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.pursuitresources.CultPursuitResources;
import ru.velialcult.pursuitresources.service.EventService;

public class PlaceholderApiProvider extends PlaceholderExpansion {

    private final EventService eventService;

    public PlaceholderApiProvider(EventService eventService) {
        this.eventService = eventService;
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.startsWith("next_event")) {
            return eventService.getTimeToNextEvent();
        }
        return "&cНезивестный placeholder";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cultpursuitresource";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VelialCult";
    }

    @Override
    public @NotNull String getVersion() {
        return CultPursuitResources.getInstance().getDescription().getVersion();
    }
}
