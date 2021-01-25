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

    /**
     * @return true if the entity need to be cleaned.
     */
    public boolean filtrate() {
        int index;
        if (EntityCleaner.itemEntitiesMatchMode) {
            // Whitelist
            for (String s : EntityCleaner.itemEntitiesWhitelist) {
                if (s.equals(this.registryName.toString())) {
                    return false;
                } else if ((index = s.indexOf('*')) != -1) {
                    s = s.substring(0, index - 1);
                    if (this.registryName.getNamespace().equals(s)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // Blacklist
            for (String s : EntityCleaner.itemEntitiesBlacklist) {
                if (s.equals(this.registryName.toString())) {
                    return true;
                } else if ((index = s.indexOf('*')) != -1) {
                    s = s.substring(0, index - 1);
                    if (this.registryName.getNamespace().equals(s)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
