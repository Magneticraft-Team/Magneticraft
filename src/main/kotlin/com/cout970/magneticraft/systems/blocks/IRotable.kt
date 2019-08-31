package com.cout970.magneticraft.systems.blocks

interface IRotable<T> : Comparable<T> {

    fun next(): IRotable<T>
}