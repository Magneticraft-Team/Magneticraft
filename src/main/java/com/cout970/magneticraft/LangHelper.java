package com.cout970.magneticraft;

import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.fluid.IFluid;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.item.IItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LangHelper {

    public static List<String> unloc = new ArrayList<>();
    public static List<String> name = new ArrayList<>();

    public static void addName(Object obj, String name) {
        if (obj == null) return;
        if (name == null) return;
        if (obj instanceof IItemStack) {
            LangHelper.put(((IItemStack) obj).getUnlocalizedName(), name);
        } else if (obj instanceof IBlock) {
            LangHelper.put(((IBlock) obj).getUnlocalizedName(), name);
        } else if (obj instanceof IItem) {
            LangHelper.put(((IItem) obj).getUnlocalizedName(), name);
        } else if (obj instanceof IFluid) {
            LangHelper.put(((IFluid) obj).getUnlocalizedName(), name);
        } else if (obj instanceof String) {
            LangHelper.put((String) obj, name);
        }
    }

    public static void put(String a, String b) {
        unloc.add(a);
        name.add(b);
    }

    public static void setupLangFile() {
        File f = new File(Magneticraft.DEV_HOME + "/src/main/resources/assets/magneticraft/lang/en_US.lang");
        Writer w;
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
            for (String s : unloc) {
                if (s.contains("fluid.")) {
                    w.write(s + "=" + name.get(unloc.indexOf(s)) + "\n");
                } else {
                    w.write(s + ".name=" + name.get(unloc.indexOf(s)) + "\n");
                }
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerNames() {

    }
}
