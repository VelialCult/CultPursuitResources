package ru.velialcult.pursuitresources.menu;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.library.bukkit.utils.InventoryUtil;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.pursuitresources.CultPursuitResources;
import ru.velialcult.pursuitresources.menu.buttons.BackButton;
import ru.velialcult.pursuitresources.menu.buttons.ForwardButton;
import ru.velialcult.pursuitresources.service.EventService;
import ru.velialcult.seller.CultSeller;
import ru.velialcult.seller.file.TranslationsFile;
import ru.velialcult.seller.repository.file.UserCache;
import ru.velialcult.seller.repository.file.UserDataService;
import ru.velialcult.seller.shop.level.SellerLevelManager;
import ru.velialcult.seller.shop.menu.SellerMenu;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 12:59
 */

public class EventMenu {

    public static void generateInventory(Player player) {
        CultSeller sellerPlugin = CultSeller.getInstance();
        SellerLevelManager sellerLevelManager = sellerPlugin.getSellerLevelManager();
        UserDataService userDataService = sellerPlugin.getUserDataService();
        UserCache userCache = userDataService.getUser(player.getUniqueId());
        FileConfiguration fileConfiguration = CultPursuitResources.getInstance().getConfig();
        EventService eventService = CultPursuitResources.getInstance().getEventService();
        char defaultChar = fileConfiguration.getString("inventories.event-menu.items.default.symbol").charAt(0);

        List<Item> defaultItems;
        boolean active = eventService.getEventData().isEventActive();

        if (eventService.getEventData().isEventActive()) {

            defaultItems = eventService.getEventData().getEventItems().stream().map((shopItem) -> new AutoUpdateItem(20, () -> (s) -> {
                ItemStack itemStack = shopItem.getItemStack().clone();
                return VersionAdapter.getItemBuilder().setItem(itemStack)
                        .setDisplayName(VersionAdapter.TextUtil().setReplaces(fileConfiguration.getString("inventories.event-menu.items.default.displayName"),
                                new ReplaceData("{current-sell}", eventService.getEventPlayerSales(player.getUniqueId()).getSales(shopItem)),
                                new ReplaceData("{multiplier}", sellerLevelManager.getMultiplier(userCache.getLevel())),
                                new ReplaceData("{item}", itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : TranslationsFile.getTranslation(itemStack))))
                        .setLore(VersionAdapter.TextUtil().setReplaces(fileConfiguration.getStringList("inventories.event-menu.items.default.lore"),
                                new ReplaceData("{current-sell}", eventService.getEventPlayerSales(player.getUniqueId()).getSales(shopItem)),
                                new ReplaceData("{multiplier}", sellerLevelManager.getMultiplier(userCache.getLevel())),
                                new ReplaceData("{price}", shopItem.getPrice())))
                        .build();
            }) {
                public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
                    if (clickType == ClickType.LEFT) {
                        eventService.getSellManager().sell(shopItem, player, 1);
                    } else if (clickType == ClickType.RIGHT) {
                        eventService.getSellManager().sell(shopItem, player, 64);
                    } else if (clickType == ClickType.MIDDLE) {
                        eventService.getSellManager().sell(shopItem, player, PlayerUtil.getItemsAmount(player, shopItem.getItemStack()));
                    }

                }
            }).collect(Collectors.toList());
        } else {
            defaultItems = new ArrayList<>();
        }
        Map<Character, SuppliedItem> customItemList = InventoryUtil.createItems(fileConfiguration, "inventories.event-menu.items.", (event, path) -> {
                    List<String> commands = fileConfiguration.getStringList(path + ".actionOnClick");
                    commands.forEach(command -> {
                        String executeCommand;
                        if (command.startsWith("[message]")) {
                            executeCommand = command.replace("[message]", "");
                            VersionAdapter.MessageUtils().sendMessage(player, executeCommand);
                        }
                        if (command.startsWith("[execute]")) {
                            executeCommand = command.replace("[execute]", "");
                            Bukkit.dispatchCommand(player, executeCommand);
                        }
                        if (command.equals("[close]")) {
                            SellerMenu.generateInventory(player);
                        }
                        if (command.startsWith("[console]")) {
                            executeCommand = command.replace("[console]", "");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executeCommand.replace("{player}", player.getName()));
                        }
                    });

                }, new ReplaceData("{current}", sellerLevelManager.getSellerLevelByLong(userCache.getLevel()).displayName()),
                new ReplaceData("{need-items}", sellerLevelManager.getNextLevelReceived(player)),
                new ReplaceData("{multiple}", sellerLevelManager.getMultiplier(userCache.getLevel())));

        String[] structure = fileConfiguration.getStringList("inventories.event-menu.structure").toArray(new String[0]);
        PagedGui.Builder<Item> builder = PagedGui.items().setStructure(structure).setContent(defaultItems)
                .addIngredient(defaultChar, Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('b', new BackButton())
                .addIngredient('n', new ForwardButton());

        InventoryUtil.setItems(builder, customItemList);
        Gui gui = builder.build();

        if (!active) {
            int slot = fileConfiguration.getInt("inventories.event-menu.items.not-active.slot");
            gui.setItem(slot, new SuppliedItem(() -> new ItemBuilder(InventoryUtil.createItem(player, fileConfiguration, "inventories.event-menu.items.not-active")), (click) -> true));
        }

        Window window = Window.single().setViewer(player)
                .setTitle(VersionAdapter.TextUtil().colorize(fileConfiguration.getString("inventories.event-menu.title")))
                .setGui(gui)
                .build();
        window.open();
    }
}
