package com.cout970.magneticraft.util;

public class FractalLib {

    public static long hash64shift(long var0) {
        var0 = ~var0 + (var0 << 21);
        var0 ^= var0 >>> 24;
        var0 = var0 + (var0 << 3) + (var0 << 8);
        var0 ^= var0 >>> 14;
        var0 = var0 + (var0 << 2) + (var0 << 4);
        var0 ^= var0 >>> 28;
        var0 += var0 << 31;
        return var0;
    }

    public static double hashFloat(long var0) {
        long var2 = hash64shift(var0);
        return Double.longBitsToDouble(4607182418800017408L | var2 & 4503599627370495L) - 1.0D; // what the fuck?
    }

    public static double noise1D(long var0, double var2, float var4, int var5) {
        double var6 = 0.5D;
        double var8 = (double) (1 << var5);

        for (int var10 = 0; var10 < var5; ++var10) {
            double var11 = var2 * var8;
            long var13 = (long) Math.floor(var11);
            double var15 = hashFloat(var0 + var13);
            double var17 = hashFloat(var0 + var13 + 1L);
            var11 -= Math.floor(var11);
            double var19 = 0.5D + 0.5D * Math.cos(Math.PI * var11);
            var19 = var19 * var15 + (1.0D - var19) * var17;
            var6 = (double) (1.0F - var4) * var6 + (double) var4 * var19;
            var8 *= 0.5D;
        }

        return var6;
    }

    public static double perturbOld(long var0, float var2, float var3, int var4) {
        double var6 = 0.0D;
        double var8 = 1.0D;
        double var10 = 1.0D;

        for (int var5 = 0; var5 < var4; ++var5) {
            long var12 = (long) Math.floor((double) var2 * var10);
            long var14 = hash64shift(var0 + var12);
            double var16 = Double.longBitsToDouble(4607182418800017408L | var14 & 4503599627370495L) - 1.0D;
            var6 += var8 * var16 * Math.sin((Math.PI * 2D) * (double) var2 * var10);
            var10 *= 2.0D;
            var8 *= (double) var3;
        }

        return var6;
    }
}
