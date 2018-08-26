package com.cout970.magneticraft.api.registries.generation;

/**
 * Created by cout970 on 2017/07/12.
 */
public class OreGeneration {

    private final String oreDictName;
    private final boolean isEnabled;
    private final int veinsPerChunk;
    private final int blocksPerVein;
    private final int maxGenerationLevel;
    private final int minGenerationLevel;

    public OreGeneration(String oreDictName, boolean isEnabled, int veinsPerChunk, int blocksPerVein,
                         int maxGenerationLevel, int minGenerationLevel) {
        this.oreDictName = oreDictName;
        this.isEnabled = isEnabled;
        this.veinsPerChunk = veinsPerChunk;
        this.blocksPerVein = blocksPerVein;
        this.maxGenerationLevel = maxGenerationLevel;
        this.minGenerationLevel = minGenerationLevel;
    }

    public OreGeneration withOreDictName(String name) {
        return new OreGeneration(name, isEnabled, veinsPerChunk, blocksPerVein, maxGenerationLevel, minGenerationLevel);
    }

    public String getOreDictName() {
        return oreDictName;
    }

    public OreGeneration withEnable(boolean value) {
        return new OreGeneration(oreDictName, value, veinsPerChunk, blocksPerVein, maxGenerationLevel,
                                 minGenerationLevel);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public OreGeneration withVeinsPerChunk(int value) {
        return new OreGeneration(oreDictName, isEnabled, value, blocksPerVein, maxGenerationLevel, minGenerationLevel);
    }

    public int getVeinsPerChunk() {
        return veinsPerChunk;
    }

    public OreGeneration withBlocksPerVein(int value) {
        return new OreGeneration(oreDictName, isEnabled, veinsPerChunk, value, maxGenerationLevel, minGenerationLevel);
    }

    public int getBlocksPerVein() {
        return blocksPerVein;
    }

    public OreGeneration withMaxGenerationLevel(int value) {
        return new OreGeneration(oreDictName, isEnabled, veinsPerChunk, blocksPerVein, value, minGenerationLevel);
    }

    public int getMaxGenerationLevel() {
        return maxGenerationLevel;
    }

    public OreGeneration withMinGenerationLevel(int value) {
        return new OreGeneration(oreDictName, isEnabled, veinsPerChunk, blocksPerVein, maxGenerationLevel, value);
    }

    public int getMinGenerationLevel() {
        return minGenerationLevel;
    }
}
