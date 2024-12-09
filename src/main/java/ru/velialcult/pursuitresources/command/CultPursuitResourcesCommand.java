package ru.velialcult.pursuitresources.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.menu.EventMenu;
import ru.velialcult.pursuitresources.service.EventService;

import java.util.List;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 17:56
 */

public class CultPursuitResourcesCommand implements CommandExecutor, TabCompleter {

    private final EventConfig eventConfig;
    private final EventService eventService;

    public CultPursuitResourcesCommand(EventConfig eventConfig, EventService eventService) {
        this.eventConfig = eventConfig;
        this.eventService = eventService;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length == 0) {
            if (PlayerUtil.senderIsPlayer(commandSender)) {
                EventMenu.generateInventory( (Player) commandSender);
            }
        } else if (args.length == 1) {

            String cmd = args[0].toUpperCase();

            switch (cmd) {

                case "FORCE": {

                    if (!commandSender.hasPermission("cultpursuitresources.admin")) {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getString("messages.no-permission",
                                new ReplaceData("{value}", "cultpursuitresources.admin")));
                        return true;
                    }

                    if (eventService.getEventData().isEventActive()) {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getList("messages.commands.force.already-active"));
                        return true;
                    }

                    eventService.startEvent();
                    VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getList("messages.commands.force.force"));
                    break;
                }

                case "END": {

                    if (!commandSender.hasPermission("cultpursuitresources.admin")) {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getString("messages.no-permission",
                                new ReplaceData("{value}", "cultpursuitresources.admin")));
                        return true;
                    }

                    if (!eventService.getEventData().isEventActive()) {
                        VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getList("messages.commands.end.no-active"));
                        return true;
                    }

                    eventService.endEvent();
                    VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getList("messages.commands.end.end"));
                    break;
                }

                default: {
                    VersionAdapter.MessageUtils().sendMessage(commandSender, eventConfig.getFileOperations().getList("messages.commands.help"));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("cultpursuitresources.admin")) {
            if (args.length == 1) {
                return List.of("force", "end");
            }
        }
        return List.of();
    }
}
