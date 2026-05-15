package com.lucciano.ironssablecompat.mixin;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import io.redspace.ironsspellbooks.spells.ender.PortalSpell;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PortalSpell.class)
public class PortalSpellMixin {

    @ModifyArgs(
        method = "handleEntityPortal",
        at = @At(
            value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/spells/ender/PortalSpell;setupPortalEntity(Lnet/minecraft/world/level/Level;Lio/redspace/ironsspellbooks/entity/spells/portal/PortalData;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;F)Lio/redspace/ironsspellbooks/entity/spells/portal/PortalEntity;"
        )
    )
    private void fixSetupPortalEntityArgs(Args args) {
        Level level = args.get(0);
        Vec3 spawnPos = args.get(3);
        
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, spawnPos);
        Vec3 dest = SableCompanion.INSTANCE.projectOutOfSubLevel(level, spawnPos);
        
        if (subLevel != null) {
            // CRITICAL: Use transformPositionInverse to correctly map RealWorld to SubLevel extreme coordinates!
            // This physically spawns the portal IN the sub-level so it sticks to the ship.
            dest = subLevel.logicalPose().transformPositionInverse(dest);
        }
        
        args.set(3, dest);
    }
    
    // We intentionally DO NOT modify PortalPos.of(). It should save the Real-World coordinates
    // so our PortalEntityMixin can correctly intercept and transform them dynamically.
}
