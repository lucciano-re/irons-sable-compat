package com.yourname.ironssablecompat.mixin;

import dev.ryanhcode.sable.companion.SableCompanion;
import io.redspace.ironsspellbooks.spells.ender.RecallSpell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

@Mixin(RecallSpell.class)
public class RecallSpellMixin {

    @ModifyArgs(
        method = "onCast",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V"
        )
    )
    private void fixRecallDestination(Args args) {
        // The entity being teleported is the instance, but we need the level.
        // We can get it from the target entity reference via the surrounding locals.
        // args[0..2] = x, y, z on Entity.teleportTo
        // NOTE: RecallSpell.onCast has `entity` in scope — use @Redirect if you
        // need the level reference, since @ModifyArgs on teleportTo won't have it.
    }
}
