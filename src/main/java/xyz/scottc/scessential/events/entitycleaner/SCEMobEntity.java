package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.atomic.AtomicBoolean;

public class SCEMobEntity {

    private final MobEntity entity;
    private final ResourceLocation registryName;

    public SCEMobEntity(MobEntity entity) {
        this.entity = entity;
        this.registryName = EntityType.getKey(entity.getType());
    }

    public boolean isInWhitelist() {
        if (!EntityCleaner.mobEntitiesWhitelist.contains(this.registryName.toString())) {
            AtomicBoolean isIn = new AtomicBoolean(false);
            EntityCleaner.mobEntitiesWhitelist.stream()
                    .filter(s -> s.contains("*"))
                    .forEach(s -> isIn.set(this.registryName.getNamespace().equals(s.substring(0, s.indexOf(":")))));
            return isIn.get();
        } else {
            return true;
        }
    }

    public MobEntity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
