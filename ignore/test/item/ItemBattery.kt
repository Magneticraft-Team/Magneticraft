package item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.registry.ITEM_ENERGY_CONSUMER
import com.cout970.magneticraft.registry.ITEM_ENERGY_PROVIDER
import com.cout970.magneticraft.registry.ITEM_ENERGY_STORAGE
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2016/09/20.
 */
object ItemBattery : ItemStorage("battery_item") {

    override fun getCapacityInternal(stack: ItemStack?): Double = Config.itemBatteryCapacity

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider {
        return object : ICapabilityProvider {

            override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
                if (capability == ITEM_ENERGY_STORAGE) return DefaultItemEnergyStorage(stack, ItemBattery) as T
                if (capability == ITEM_ENERGY_CONSUMER) return DefaultItemEnergyConsumer(stack, ItemBattery) as T
                if (capability == ITEM_ENERGY_PROVIDER) return DefaultItemEnergyProvider(stack, ItemBattery) as T
                return null
            }

            override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
                return capability == ITEM_ENERGY_CONSUMER || capability == ITEM_ENERGY_STORAGE || capability == ITEM_ENERGY_PROVIDER
            }
        }
    }
}