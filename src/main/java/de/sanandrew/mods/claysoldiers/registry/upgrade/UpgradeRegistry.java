/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.claysoldiers.registry.upgrade;

import com.google.common.collect.ImmutableList;
import de.sanandrew.mods.claysoldiers.api.CsmConstants;
import de.sanandrew.mods.claysoldiers.api.soldier.upgrade.ISoldierUpgrade;
import de.sanandrew.mods.claysoldiers.api.soldier.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.claysoldiers.registry.upgrade.enhancement.UpgradeFlint;
import de.sanandrew.mods.claysoldiers.registry.upgrade.hand.UpgradeArrow;
import de.sanandrew.mods.claysoldiers.registry.upgrade.hand.UpgradeBlazeRod;
import de.sanandrew.mods.claysoldiers.registry.upgrade.hand.UpgradeShearBlade;
import de.sanandrew.mods.claysoldiers.registry.upgrade.hand.UpgradeStick;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeEgg;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeFeather;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeFood;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeGlowstone;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeGoggles;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeLeather;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeRabbitHide;
import de.sanandrew.mods.claysoldiers.registry.upgrade.misc.UpgradeSugar;
import de.sanandrew.mods.claysoldiers.util.HashItemStack;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UpgradeRegistry
        implements IUpgradeRegistry
{
    public static final UpgradeRegistry INSTANCE = new UpgradeRegistry();

    private final List<ISoldierUpgrade> upgrades;
    private final Map<UUID, ISoldierUpgrade> uuidUpgradeMap;
    private final Map<ISoldierUpgrade, UUID> upgradeUuidMap;
    private final Map<HashItemStack, ISoldierUpgrade> stackUpgradeMap;

    private UpgradeRegistry() {
        this.uuidUpgradeMap = new HashMap<>();
        this.upgradeUuidMap = new HashMap<>();
        this.stackUpgradeMap = new HashMap<>();
        this.upgrades = new ArrayList<>();
    }

    @Override
    public boolean registerUpgrade(UUID id, ISoldierUpgrade upgradeInst) {
        if( id == null || upgradeInst == null ) {
            CsmConstants.LOG.log(Level.WARN, String.format("Upgrade ID and instance cannot be null nor empty for ID %s!", id));
            return false;

        }

        ItemStack[] upgItems = upgradeInst.getStacks();
        if( upgItems == null || upgItems.length < 1 || Arrays.stream(upgItems).anyMatch(itm -> !ItemStackUtils.isValid(itm)) ) {
            CsmConstants.LOG.log(Level.WARN, String.format("Upgrade items are invalid for ID %s!", id));
            return false;
        }

        if( this.uuidUpgradeMap.containsKey(id) ) {
            CsmConstants.LOG.log(Level.WARN, String.format("Duplicate Upgrade ID %s!", id));
            return false;
        }

        if( this.upgradeUuidMap.containsKey(upgradeInst) ) {
            CsmConstants.LOG.log(Level.WARN, String.format("Duplicate Upgrade instances for %s!", id));
            return false;
        }

        HashItemStack hStacks[] = Arrays.stream(upgItems).map(HashItemStack::new).toArray(HashItemStack[]::new);
        for( HashItemStack existingItm : this.stackUpgradeMap.keySet() ) {
            if( Arrays.stream(hStacks).anyMatch(existingItm::equals) ) {
                CsmConstants.LOG.log(Level.WARN, String.format("Duplicate Upgrade Item %s for ID %s!", existingItm.getStack(), id));
                return false;
            }
        }

        this.uuidUpgradeMap.put(id, upgradeInst);
        this.upgradeUuidMap.put(upgradeInst, id);
        Arrays.stream(hStacks).forEach(stk -> stackUpgradeMap.put(stk, upgradeInst));
        this.upgrades.add(upgradeInst);

        return true;
    }

    @Nullable
    @Override
    public ISoldierUpgrade getUpgrade(UUID id) {
        return this.uuidUpgradeMap.get(id);
    }

    @Nullable
    @Override
    public UUID getId(ISoldierUpgrade upgrade) {
        return this.upgradeUuidMap.get(upgrade);
    }

    @Nullable
    @Override
    public ISoldierUpgrade getUpgrade(ItemStack stack) {
        return MiscUtils.defIfNull(this.stackUpgradeMap.get(new HashItemStack(stack)), this.stackUpgradeMap.get(new HashItemStack(stack, true)));
    }

    @Override
    public List<ISoldierUpgrade> getUpgrades() {
        return ImmutableList.copyOf(this.upgrades);
    }

    public static void initialize(IUpgradeRegistry registry) {
        registry.registerUpgrade(MH_STICK, new UpgradeStick());
        registry.registerUpgrade(MH_ARROW, new UpgradeArrow());
        registry.registerUpgrade(MH_BLAZEROD, new UpgradeBlazeRod());
        registry.registerUpgrade(MOH_SHEARBLADE, new UpgradeShearBlade());

        registry.registerUpgrade(MC_FLINT, new UpgradeFlint());
        registry.registerUpgrade(MC_EGG, new UpgradeEgg());
        registry.registerUpgrade(MC_GLOWSTONE, new UpgradeGlowstone());
        registry.registerUpgrade(MC_FEATHER, new UpgradeFeather());
        registry.registerUpgrade(MC_FOOD, new UpgradeFood());
        registry.registerUpgrade(MC_GLASS, new UpgradeGoggles());
        registry.registerUpgrade(MC_LEATHER, new UpgradeLeather());
        registry.registerUpgrade(MC_RABBITHIDE, new UpgradeRabbitHide());
        registry.registerUpgrade(MC_SUGAR, new UpgradeSugar());
    }

    public static final UUID MH_STICK = UUID.fromString("31F0A3DB-F1A7-4418-9EA6-A9D0C900EB41");
    public static final UUID MH_ARROW = UUID.fromString("5CBFDDAB-B082-4DFF-A6DE-D207E068D9AD");
    public static final UUID MH_BLAZEROD = UUID.fromString("9EAF320D-1C8C-40F2-B8E4-6A4C18F9248E");
    public static final UUID MOH_SHEARBLADE = UUID.fromString("5CDCD4F9-1C94-485D-B043-2F9A779CF454");

    public static final UUID MC_FLINT = UUID.fromString("63342EEB-932B-4330-9B60-C5E21434A0B8");
    public static final UUID MC_EGG = UUID.fromString("4613D60F-B53C-4E75-99CA-0E2176B6D58D");
    public static final UUID MC_GLOWSTONE = UUID.fromString("6D1D540B-84DC-4009-BF29-134089104A3C");
    public static final UUID MC_FEATHER = UUID.fromString("453077F6-2930-49A1-A2EF-B1A9B0F8B55C");
    public static final UUID MC_FOOD = UUID.fromString("12A95822-000A-427A-9975-B492923162B7");
    public static final UUID MC_GLASS = UUID.fromString("F6C44798-5E23-4430-910E-E6CAE1305D58");
    public static final UUID MC_LEATHER = UUID.fromString("D5A7486E-B9D9-4298-B134-2FDCCD569036");
    public static final UUID MC_RABBITHIDE = UUID.fromString("ADD2D447-CDE2-43B1-AE9D-7EC301A9ECEA");
    public static final UUID MC_SUGAR = UUID.fromString("BF18CCDE-39A3-43D4-ABB1-322348AB0F0B");
}
