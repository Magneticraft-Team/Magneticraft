package com.cout970.magneticraft.systems.gui

/**
 * Created by cout970 on 08/07/2016.
 */

const val DATA_ID_VOLTAGE_0 = 1
const val DATA_ID_BURNING_TIME = 2 // used in furnaces and generators, don't mix
const val DATA_ID_MAX_BURNING_TIME = 3
const val DATA_ID_MACHINE_HEAT = 4
const val DATA_ID_MACHINE_PRODUCTION = 5
const val DATA_ID_FLUID_AMOUNT = 6  // Used by tanks
const val DATA_ID_FLUID_NAME = 7    // Used by tanks
const val DATA_ID_STORAGE = 8
const val DATA_ID_CHARGE_RATE = 9
const val DATA_ID_MACHINE_WORKING = 10
const val DATA_ID_ITEM_CHARGE_RATE = 11
const val DATA_ID_MAX_FREEZING_TIME = 12
const val DATA_ID_FREEZING_TIME = 13
const val DATA_ID_VOLTAGE_1 = 14
const val DATA_ID_VOLTAGE_2 = 15
const val DATA_ID_VOLTAGE_3 = 16
const val DATA_ID_MONITOR_CURSOR_LINE = 17
const val DATA_ID_MONITOR_CURSOR_COLUMN = 18
const val DATA_ID_MONITOR_BUFFER = 19
const val DATA_ID_KEYBOARD_KEY_STATES = 20
const val DATA_ID_KEYBOARD_EVENT_KEY = 21
const val DATA_ID_KEYBOARD_EVENT_CODE = 22
const val DATA_ID_COMPUTER_BUTTON = 25
const val DATA_ID_COMPUTER_LIGHT = 26
const val DATA_ID_SHELVING_UNIT_SCROLL = 27
const val DATA_ID_SHELVING_UNIT_FILTER = 28
const val DATA_ID_SHELVING_UNIT_LEVEL = 29
const val DATA_ID_THERMOPILE_HEAT_FLUX = 30
const val DATA_ID_UNUSED_1 = 31
const val DATA_ID_THERMOPILE_PRODUCTION = 32
const val DATA_ID_FLUID_AMOUNT_0 = 33 // Used by FluidHandlers
const val DATA_ID_FLUID_AMOUNT_1 = 34 // Used by FluidHandlers
const val DATA_ID_FLUID_AMOUNT_2 = 35 // Used by FluidHandlers
const val DATA_ID_FLUID_AMOUNT_3 = 36 // Used by FluidHandlers
const val DATA_ID_FLUID_AMOUNT_4 = 37 // Used by FluidHandlers
const val DATA_ID_ITEM_AMOUNT = 38
const val DATA_ID_DEPOSIT_SIZE = 39
const val DATA_ID_DEPOSIT_LEFT = 40
const val DATA_ID_STATUS = 41
const val DATA_ID_MACHINE_PROGRESS = 42
const val DATA_ID_MACHINE_CONSUMPTION = 43
const val DATA_ID_MONITOR_CLIPBOARD = 44
const val DATA_ID_SELECTED_OPTION = 45
const val DATA_ID_FLUID_NAME_0 = 46 // Used by FluidHandlers
const val DATA_ID_FLUID_NAME_1 = 47 // Used by FluidHandlers
const val DATA_ID_FLUID_NAME_2 = 48 // Used by FluidHandlers
const val DATA_ID_FLUID_NAME_3 = 49 // Used by FluidHandlers
const val DATA_ID_FLUID_NAME_4 = 50 // Used by FluidHandlers
const val DATA_ID_RF = 51
const val DATA_ID_HEAT_1 = 52
const val DATA_ID_HEAT_2 = 53
const val DATA_ID_HEAT_3 = 54
const val DATA_ID_FLAGS = 55
const val DATA_ID_SHELVING_UNIT_SORT = 56

val DATA_ID_VOLTAGE_LIST = listOf(
    DATA_ID_VOLTAGE_0,
    DATA_ID_VOLTAGE_1,
    DATA_ID_VOLTAGE_2,
    DATA_ID_VOLTAGE_3
)

val DATA_ID_FLUID_AMOUNT_LIST = listOf(
    DATA_ID_FLUID_AMOUNT_0,
    DATA_ID_FLUID_AMOUNT_1,
    DATA_ID_FLUID_AMOUNT_2,
    DATA_ID_FLUID_AMOUNT_3,
    DATA_ID_FLUID_AMOUNT_4
)

val DATA_ID_FLUID_NAME_LIST = listOf(
    DATA_ID_FLUID_NAME_0,
    DATA_ID_FLUID_NAME_1,
    DATA_ID_FLUID_NAME_2,
    DATA_ID_FLUID_NAME_3,
    DATA_ID_FLUID_NAME_4
)

val DATA_ID_HEAT_LIST = listOf(
    DATA_ID_HEAT_1,
    DATA_ID_HEAT_2,
    DATA_ID_HEAT_3
)