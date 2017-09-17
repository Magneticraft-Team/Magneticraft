package com.cout970.magneticraft.misc.tileentity

/**
 * Created by cout970 on 2017/09/17.
 */


/**
 * This is used to mark methods that override ITickable::update,
 * because TileBase already has TileBase::update, and there is a bug in MCP,
 * so it doesn't change the name to srg and the mod crashes only in non-dev environment
 * (you don't know until people start complaining that the mod crashes randomly)
 */
annotation class DoNotRemove