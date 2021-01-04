package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.ResourceLocation;

public class SCEItemEntity {

    private final ItemEntity entity;
    private final ResourceLocation registryName;

    public SCEItemEntity(ItemEntity entity) {
        this.entity = entity;
        this.registryName = this.entity.getItem().getItem().getRegistryName();
    }

    public boolean isInWhitelist() {
        return EntityCleaner.itemEntitiesWhitelist.contains(this.registryName.toString());
    }

    public Entity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
