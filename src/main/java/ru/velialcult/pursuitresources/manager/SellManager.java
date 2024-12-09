package ru.velialcult.pursuitresources.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.pursuitresources.data.EventSalesData;
import ru.velialcult.pursuitresources.file.EventConfig;
import ru.velialcult.pursuitresources.service.EventService;
import ru.velialcult.seller.CultSeller;
import ru.velialcult.seller.booster.BoosterManager;
import ru.velialcult.seller.file.TranslationsFile;
import ru.velialcult.seller.providers.vault.VaultProvider;
import ru.velialcult.seller.repository.file.UserCache;
import ru.velialcult.seller.repository.file.UserDataService;
import ru.velialcult.seller.shop.item.ShopItem;
import ru.velialcult.seller.shop.item.ShopItemType;
import ru.velialcult.seller.shop.level.SellerLevelManager;

/**
 * ✨ Этот код был написан AveNilsson
 * 📅 Дата создания: 08.12.2024
 * ⏰ Время создания: 13:20
 */

public class SellManager {

    private final EventConfig eventConfig;
    private final EventService eventService;
    private final VaultProvider vaultProvider;
    private final UserDataService userDataService;
    private final SellerLevelManager sellerLevelManager;
    private final BoosterManager boosterManager;

    public SellManager(EventConfig eventConfig, EventService eventService) {
        this.eventConfig = eventConfig;
        this.eventService = eventService;

        CultSeller cultSeller = CultSeller.getInstance();
        this.vaultProvider = cultSeller.getProvidersManager().getVaultProvider();
        this.userDataService = cultSeller.getUserDataService();
        this.sellerLevelManager = cultSeller.getSellerLevelManager();
        this.boosterManager = cultSeller.getBoosterManager();
    }

    public void sell(ShopItem shopItem, Player player, int amount) {
        ItemStack itemStack = shopItem.getItemStack();
        UserCache userCache = userDataService.getUser(player.getUniqueId());
        int count = PlayerUtil.getItemsAmount(player, itemStack);
        if (count < amount) {
            VersionAdapter.MessageUtils().sendMessage(player, eventConfig.getFileOperations().getString("messages.sell.dont-have-items"));
        } else {

            if (shopItem.getShopItemType() == ShopItemType.EVENT && eventService.getEventData().isEventActive() && eventService.getEventData().getEventItems().contains(shopItem)) {
                EventSalesData eventPlayerSales = eventService.getEventPlayerSales(player.getUniqueId());
                eventPlayerSales.recordSale(shopItem, amount);
            }

            double multiplier = this.sellerLevelManager.getMultiplier(userCache.getLevel()) + boosterManager.getPriceMultiplier(player);
            double price = (shopItem.getPrice() * multiplier) * amount;
            ItemStack newItemsStack = itemStack.clone();
            newItemsStack.setAmount(amount);
            PlayerUtil.removeItems(player, newItemsStack);
            vaultProvider.getEconomy().withdrawPlayer(player, price);
            VersionAdapter.MessageUtils().sendMessage(player, eventConfig.getFileOperations().getString("messages.sell.sell",
                    new ReplaceData("{amount}", amount),
                    new ReplaceData("{item}", itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : TranslationsFile.getTranslation(itemStack)),
                    new ReplaceData("{count}", amount), new ReplaceData("{price}", price)));
            userCache.incrementSells(amount);
        }
    }
}
