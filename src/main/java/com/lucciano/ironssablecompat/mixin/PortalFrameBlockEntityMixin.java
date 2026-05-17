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
        UUID uuid = entity.getUUID();
        if (uuid != null) {
            PortalData portalData = PortalManager.INSTANCE.getPortalData(uuid);
            if (portalData != null) {
                boolean isPrimary = uuid.equals(portalData.portalEntityId1);
                PortalPos currentPos = isPrimary ? portalData.globalPos1 : portalData.globalPos2;
                
                // getPortalLocation returns the BlockPos in the sub-level (extreme coordinate)
                // Add 0.1 to Y so when the player is teleported, they don't clip into the moving floor!
                Vec3 actualLoc = entity.getPortalLocation().add(0, 0.1, 0);
                
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

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "lambda$teleport$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"
        )
    )
    private static boolean redirectFrameTeleport(net.minecraft.world.entity.Entity instance, net.minecraft.server.level.ServerLevel level, double x, double y, double z, java.util.Set<net.minecraft.world.entity.RelativeMovement> relativeMovements, float yaw, float pitch) {
        // By calling the simple teleportTo, Sable's physics mixins can properly intercept it
        // and attach the player to the sub-level!
        instance.teleportTo(x, y, z);
        instance.setYRot(yaw);
        instance.setXRot(pitch);
        return true;
    }
}
