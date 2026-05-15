package com.lucciano.ironssablecompat.mixin;

import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlockEntity;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PortalFrameBlockEntity.class)
public class PortalFrameBlockEntityMixin {

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void onServerTick(Level level, BlockPos pos, BlockState blockState, PortalFrameBlockEntity entity, CallbackInfo ci) {
        if (level.getGameTime() % 20L == 0L) {
            UUID uuid = entity.getUUID();
            if (uuid != null) {
                PortalData portalData = PortalManager.INSTANCE.getPortalData(uuid);
                if (portalData != null) {
                    boolean isPrimary = uuid.equals(portalData.portalEntityId1);
                    PortalPos currentPos = isPrimary ? portalData.globalPos1 : portalData.globalPos2;
                    
                    Vec3 actualLoc = entity.getPortalLocation();
                    
                    if (currentPos == null || currentPos.pos().distanceToSqr(actualLoc) > 0.01) {
                        PortalPos newPos = PortalPos.of(level.dimension(), actualLoc, currentPos != null ? currentPos.rotation() : 0f);
                        if (isPrimary) {
                            portalData.globalPos1 = newPos;
                        } else {
                            portalData.globalPos2 = newPos;
                        }
                    }
                }
            }
        }
    }
}
