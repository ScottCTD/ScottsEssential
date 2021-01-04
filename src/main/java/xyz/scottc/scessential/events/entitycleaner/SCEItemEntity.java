package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.atomic.AtomicBoolean;

public class SCEItemEntity {

    private final ItemEntity entity;
    private final ResourceLocation registryName;

    public SCEItemEntity(ItemEntity entity) {
        this.entity = entity;
        this.registryName = this.entity.getItem().getItem().getRegistryName();
    }

    public boolean isInWhitelist() {
        // first match the one without *
        if (!EntityCleaner.itemEntitiesWhitelist.contains(this.registryName.toString())) {
            AtomicBoolean isIn = new AtomicBoolean(false);
            EntityCleaner.itemEntitiesWhitelist.stream()
                    .filter(s -> s.contains("*"))
                    .forEach(s -> isIn.set(this.registryName.getNamespace().equals(s.substring(0, s.indexOf(":")))));
            return isIn.get();
        } else {
            return true;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
