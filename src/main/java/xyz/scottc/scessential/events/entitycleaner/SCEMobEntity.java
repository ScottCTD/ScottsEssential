package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class SCEMobEntity {

    private final MobEntity entity;
    private final ResourceLocation identifier;

    public SCEMobEntity(MobEntity entity) {
        this.entity = entity;
        this.identifier = EntityType.getKey(entity.getType());
    }

    public boolean isInWhitelist() {
        return EntityCleaner.mobEntitiesWhitelist.contains(this.identifier.toString());
    }

    public MobEntity getEntity() {
        return entity;
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }
}
