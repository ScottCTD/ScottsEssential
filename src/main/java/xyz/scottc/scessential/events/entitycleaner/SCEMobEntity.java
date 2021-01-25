package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class SCEMobEntity {

    private final MobEntity entity;
    private final ResourceLocation registryName;

    public SCEMobEntity(MobEntity entity) {
        this.entity = entity;
        this.registryName = EntityType.getKey(entity.getType());
    }

    public boolean filtrate() {
        int index;
        if (EntityCleaner.mobEntitiesMatchMode) {
            // Whitelist
            for (String s : EntityCleaner.mobEntitiesWhitelist) {
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
            for (String s : EntityCleaner.mobEntitiesBlacklist) {
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

    public MobEntity getEntity() {
        return entity;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
