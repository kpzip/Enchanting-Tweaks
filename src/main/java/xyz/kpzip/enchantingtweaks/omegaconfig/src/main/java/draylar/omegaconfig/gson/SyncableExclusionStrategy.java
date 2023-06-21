package xyz.kpzip.enchantingtweaks.omegaconfig.src.main.java.draylar.omegaconfig.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import xyz.kpzip.enchantingtweaks.omegaconfig.src.main.java.draylar.omegaconfig.api.Syncing;

public class SyncableExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotations().stream().noneMatch(annotation -> annotation instanceof Syncing);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
