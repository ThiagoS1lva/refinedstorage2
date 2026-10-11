package com.refinedmods.refinedstorage.forge.datagen.advancement;

import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.content.Tags;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.MOD_ID;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.MOD_NAME;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class AdvancementProvider extends AdvancementSubProvider {
    public AdvancementProvider(final BootstrapContext<Advancement> output) {
        super(output);
    }

    @Override
    public void generate() {
        final var items = this.output.lookup(Registries.ITEM);

        final var root = Advancement.Builder.advancement()
            .rootDisplay(
                Blocks.INSTANCE.getCreativeController().getDefault().asItem(),
                MOD_NAME,
                createTranslation("advancements", "root.description"),
                createIdentifier("gui/advancements"),
                AdvancementType.TASK,
                false,
                true,
                false
            )
            .addCriterion("controller_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.CONTROLLERS).build()
            ))
            .save(this.output, MOD_ID + ":root");

        final var connecting = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getCable().getDefault().asItem(),
                createTranslation("advancements", "connecting"),
                createTranslation("advancements", "connecting.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("cable_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.CABLES).build()
            ))
            .save(this.output, MOD_ID + ":connecting");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getRelay().getDefault().asItem(),
                createTranslation("advancements", "conditional_connecting"),
                createTranslation("advancements", "conditional_connecting.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(connecting)
            .addCriterion("relay_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.RELAYS).build()
            ))
            .save(this.output, MOD_ID + ":conditional_connecting");

        final var drives = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getDiskDrive().asItem(),
                createTranslation("advancements", "drives"),
                createTranslation("advancements", "drives.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("disk_drive_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Blocks.INSTANCE.getDiskDrive()
            ))
            .save(this.output, MOD_ID + ":drives");

        final var storingItems = Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getItemStorageDisk(ItemStorageVariant.ONE_K),
                createTranslation("advancements", "storing_items"),
                createTranslation("advancements", "storing_items.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(drives)
            .addCriterion("storage_disk_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.STORAGE_DISKS).build()
            ))
            .save(this.output, MOD_ID + ":storing_items");

        Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getFluidStorageDisk(FluidStorageVariant.SIXTY_FOUR_B),
                createTranslation("advancements", "storing_fluids"),
                createTranslation("advancements", "storing_fluids.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(drives)
            .addCriterion("fluid_storage_disk_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.FLUID_STORAGE_DISKS).build()
            ))
            .save(this.output, MOD_ID + ":storing_fluids");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getDiskInterface().getDefault().asItem(),
                createTranslation("advancements", "interfacing_with_disks"),
                createTranslation("advancements", "interfacing_with_disks.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(drives)
            .addCriterion("disk_interface_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.DISK_INTERFACES).build()
            ))
            .save(this.output, MOD_ID + ":interfacing_with_disks");

        final var viewingYourStorage = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getGrid().getDefault().asItem(),
                createTranslation("advancements", "viewing_your_storage"),
                createTranslation("advancements", "viewing_your_storage.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(storingItems)
            .addCriterion("grid_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.GRIDS).build()
            ))
            .save(this.output, MOD_ID + ":viewing_your_storage");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getCraftingGrid().getDefault().asItem(),
                createTranslation("advancements", "upgrading_your_grid"),
                createTranslation("advancements", "upgrading_your_grid.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(viewingYourStorage)
            .addCriterion("crafting_grid_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.CRAFTING_GRIDS).build()
            ))
            .save(this.output, MOD_ID + ":upgrading_your_grid");

        Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getPortableGrid(),
                createTranslation("advancements", "portable_storage"),
                createTranslation("advancements", "portable_storage.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(viewingYourStorage)
            .addCriterion("portable_grid_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.INSTANCE.getPortableGrid()
            ))
            .save(this.output, MOD_ID + ":portable_storage");

        final var exporting = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getExporter().getDefault().asItem(),
                createTranslation("advancements", "exporting"),
                createTranslation("advancements", "exporting.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("exporter_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.EXPORTERS).build()
            ))
            .save(this.output, MOD_ID + ":exporting");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getConstructor().getDefault().asItem(),
                createTranslation("advancements", "construction"),
                createTranslation("advancements", "construction.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(exporting)
            .addCriterion("constructor_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.CONSTRUCTORS).build()
            ))
            .save(this.output, MOD_ID + ":construction");

        final var importing = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getImporter().getDefault().asItem(),
                createTranslation("advancements", "importing"),
                createTranslation("advancements", "importing.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("importer_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.IMPORTERS).build()
            ))
            .save(this.output, MOD_ID + ":importing");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getDestructor().getDefault().asItem(),
                createTranslation("advancements", "destruction"),
                createTranslation("advancements", "destruction.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(importing)
            .addCriterion("destructor_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.DESTRUCTORS).build()
            ))
            .save(this.output, MOD_ID + ":destruction");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getDetector().getDefault().asItem(),
                createTranslation("advancements", "detecting"),
                createTranslation("advancements", "detecting.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("detector_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.DETECTORS).build()
            ))
            .save(this.output, MOD_ID + ":detecting");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getExternalStorage().getDefault().asItem(),
                createTranslation("advancements", "storing_externally"),
                createTranslation("advancements", "storing_externally.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("external_storage_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.EXTERNAL_STORAGES).build()
            ))
            .save(this.output, MOD_ID + ":storing_externally");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getStorageMonitor().asItem(),
                createTranslation("advancements", "better_than_a_barrel"),
                createTranslation("advancements", "better_than_a_barrel.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("storage_monitor_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Blocks.INSTANCE.getStorageMonitor()
            ))
            .save(this.output, MOD_ID + ":better_than_a_barrel");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getInterface().asItem(),
                createTranslation("advancements", "interface_to_the_world"),
                createTranslation("advancements", "interface_to_the_world.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("interface_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Blocks.INSTANCE.getInterface()
            ))
            .save(this.output, MOD_ID + ":interface_to_the_world");

        final var wireless = Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getWirelessGrid(),
                createTranslation("advancements", "wireless"),
                createTranslation("advancements", "wireless.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("wireless_grid_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Items.INSTANCE.getWirelessGrid()).build(),
                ItemPredicate.Builder.item().of(items, Tags.WIRELESS_TRANSMITTERS).build()
            ))
            .save(this.output, MOD_ID + ":wireless");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getNetworkTransmitter().getDefault().asItem(),
                createTranslation("advancements", "no_cables_required"),
                createTranslation("advancements", "no_cables_required.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(wireless)
            .addCriterion("network_transmitter_receiver_card_in_inventory",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(items, Tags.NETWORK_TRANSMITTERS).build(),
                    ItemPredicate.Builder.item().of(items, Tags.NETWORK_RECEIVERS).build(),
                    ItemPredicate.Builder.item().of(items, Items.INSTANCE.getNetworkCard()).build()
                ))
            .save(this.output, MOD_ID + ":no_cables_required");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getSecurityManager().getDefault().asItem(),
                createTranslation("advancements", "security"),
                createTranslation("advancements", "security.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("security_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.SECURITY_MANAGERS).build(),
                ItemPredicate.Builder.item()
                    .of(items, Items.INSTANCE.getSecurityCard(), Items.INSTANCE.getFallbackSecurityCard())
                    .build()
            ))
            .save(this.output, MOD_ID + ":security");

        Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getUpgrade(),
                createTranslation("advancements", "upgrading"),
                createTranslation("advancements", "upgrading.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("upgrade_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.INSTANCE.getUpgrade()
            ))
            .save(this.output, MOD_ID + ":upgrading");

        final var autocrafting = Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getAutocrafter().getDefault().asItem(),
                createTranslation("advancements", "autocrafting"),
                createTranslation("advancements", "autocrafting.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(root)
            .addCriterion("autocrafter_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.PATTERN_GRIDS).build(),
                ItemPredicate.Builder.item().of(items, Tags.AUTOCRAFTERS).build(),
                ItemPredicate.Builder.item().of(items, Items.INSTANCE.getPattern()).build()
            ))
            .save(this.output, MOD_ID + ":autocrafting");

        Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getAutocraftingUpgrade(),
                createTranslation("advancements", "autocrafting_on_demand"),
                createTranslation("advancements", "autocrafting_on_demand.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(autocrafting)
            .addCriterion("autocrafting_upgrade_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                Items.INSTANCE.getAutocraftingUpgrade()
            ))
            .save(this.output, MOD_ID + ":autocrafting_on_demand");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getAutocrafterManager().getDefault().asItem(),
                createTranslation("advancements", "managing_patterns"),
                createTranslation("advancements", "managing_patterns.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(autocrafting)
            .addCriterion("autocrafter_manager_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.AUTOCRAFTER_MANAGERS).build()
            ))
            .save(this.output, MOD_ID + ":managing_patterns");

        Advancement.Builder.advancement()
            .display(
                Blocks.INSTANCE.getAutocraftingMonitor().getDefault().asItem(),
                createTranslation("advancements", "monitoring"),
                createTranslation("advancements", "monitoring.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(autocrafting)
            .addCriterion("autocrafting_monitor_in_inventory", InventoryChangeTrigger.TriggerInstance.hasItems(
                ItemPredicate.Builder.item().of(items, Tags.AUTOCRAFTING_MONITORS).build()
            ))
            .save(this.output, MOD_ID + ":monitoring");

        Advancement.Builder.advancement()
            .display(
                Items.INSTANCE.getWirelessAutocraftingMonitor(),
                createTranslation("advancements", "wireless_monitoring"),
                createTranslation("advancements", "wireless_monitoring.description"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .parent(wireless)
            .addCriterion("wireless_autocrafting_monitor_in_inventory",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    Items.INSTANCE.getWirelessAutocraftingMonitor()
                ))
            .save(this.output, MOD_ID + ":wireless_monitoring");
    }
}
