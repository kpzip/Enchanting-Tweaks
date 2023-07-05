package xyz.kpzip.enchantingtweaks.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
	
	@Shadow
	private static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
	
	@Shadow
	private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) { return true; }
	
	//TODO Overwrite: Maintain this for every update in case the original changes
	@Overwrite
	private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        int j = 1 + 2*i;
        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
        ItemStack itemStack = shooter.getProjectileType(crossbow);
        ItemStack itemStack2 = itemStack.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemStack = itemStack2.copy();
            }
            if (itemStack.isEmpty() && bl) {
                itemStack = new ItemStack(Items.ARROW);
                itemStack2 = itemStack.copy();
            }
            if (loadProjectile(shooter, crossbow, itemStack, k > 0, bl)) continue;
            return false;
        }
        return true;
    }
	
	@Shadow
	private static List<ItemStack> getProjectiles(ItemStack stack) { return new ArrayList<ItemStack>(); }
	
	@Shadow
	private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {}
	
	@Shadow
	private static void postShoot(World world, LivingEntity entity, ItemStack stack) {}
	
	//TODO Overwrite: Maintain this for every update in case the original changes
	@Overwrite
	public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        List<ItemStack> list = getProjectiles(stack);
        float[] fs = getSounds(entity.getRandom(), list.size());
        for (int i = 0; i < list.size()/* && i <= 19*/; ++i) {
        	
            boolean creative = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
            
            ItemStack itemStack = list.get(i);
            if (itemStack.isEmpty()) continue;
            
            shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, getArrowSpread(i, Math.max(list.size(), 19)));
            /*
            if (i == 0) {
                shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, 0.0f);
                continue;
            }
            if (i == 1) {
                shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, -10.0f);
                continue;
            }
            if (i != 2) continue;
            shoot(world, entity, hand, stack, itemStack, fs[i], creative, speed, divergence, 10.0f);
            */
            
            
        }
        postShoot(world, entity, stack);
    }
	
	@Unique
	private static float getArrowSpread(int arrowNum, int maxArrows) {
		
		if (arrowNum == 0) return 0.0f;
		
		float maxSpread = (float) (-25.998795268f * Math.exp(-0.55d * maxArrows) + 15.0f);
		float unitSpread = maxSpread/((maxArrows - 1)/2);
		float spread = ((arrowNum)/2) * unitSpread;
		
		if (arrowNum%2 == 1) return -spread; else return spread;
	}
	
	//TODO Overwrite: Maintain this for every update in case the original changes (marked as @unique since the parameters needed to be changed)
	@Unique
	private static float[] getSounds(Random random, int len) {
        boolean bl = random.nextBoolean();
        float[] pitches = new float[len];
        pitches[0] = 1.0f;
        for (int i = 1; i < pitches.length; ++i) {
        	getSoundPitch(bl, random);
        	bl = !bl;
        }
        return pitches;
    }
	
	
	@Shadow
    private static float getSoundPitch(boolean flag, Random random) {return 0.0f;}


}
