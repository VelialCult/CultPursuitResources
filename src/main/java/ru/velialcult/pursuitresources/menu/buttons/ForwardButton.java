package ru.velialcult.pursuitresources.menu.buttons;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.seller.CultSeller;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class ForwardButton extends PageItem {
    public ForwardButton() {
        super(true);
    }

    public ItemProvider getItemProvider(PagedGui<?> gui) {
        FileConfiguration fileConfiguration = CultSeller.getInstance().getInventoriesFile().getConfig();
        String materialName = fileConfiguration.getString("inventories.buttons.forward-button.item.material");
        String url = fileConfiguration.getString("inventories.buttons.forward-button.item.head");
        String displayName = VersionAdapter.TextUtil().colorize(fileConfiguration.getString("inventories.buttons.forward-button.displayName"));
        List<String> lore = VersionAdapter.TextUtil().colorize(fileConfiguration.getStringList("inventories.buttons.forward-button.lore"));
        ItemStack itemStack;
        if (materialName.equalsIgnoreCase("head")) {
            itemStack = VersionAdapter.getSkullBuilder().setDisplayName(displayName).setLore(lore).setTexture(url).build();
        } else {
            itemStack = VersionAdapter.getItemBuilder().setType(materialName).setDisplayName(displayName).setLore(lore).build();
        }

        return new ItemBuilder(itemStack);
    }
}
