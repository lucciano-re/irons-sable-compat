package com.yourname.ironssablecompat.mixin;

import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TeleportSpell.class)
public class TeleportSpellMixin {

    /**
     * Intercepts the computed teleport destination just before
     * the player.teleportTo() call and projects it out of any
     * Sable sub-level into real world coordinates.
     */
    @ModifyVariable(
        method = "onCast",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V",
            shift = At.Shift.BEFORE
        ),
        ordinal = 0
    )
    private Vec3 fixTeleportPosition(Vec3 destination, Level level, Player player) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, destination);
    }
}
