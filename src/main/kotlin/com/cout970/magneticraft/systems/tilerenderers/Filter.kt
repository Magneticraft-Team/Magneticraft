package com.cout970.magneticraft.systems.tilerenderers

enum class FilterTarget {
    BRANCH, LEAF, ANIMATION
}

sealed class Filter {
    abstract operator fun invoke(name: String, type: FilterTarget): Boolean
}

@JvmField
val IGNORE_ANIMATION = FilterNot(FilterAlways)

data class ModelSelector(val name: String, val componentFilter: Filter, val animationFilter: Filter = IGNORE_ANIMATION)

class FilterRegex(expression: String, val target: FilterTarget = FilterTarget.LEAF) : Filter() {
    val regex = expression.toRegex()

    override operator fun invoke(name: String, type: FilterTarget): Boolean {
        if (type != target) return true
        return regex.matches(name)
    }
}

class FilterNotRegex(expression: String, val target: FilterTarget = FilterTarget.LEAF) : Filter() {
    val regex = expression.toRegex()

    override operator fun invoke(name: String, type: FilterTarget): Boolean {
        if (type != target) return true
        return !regex.matches(name)
    }
}

class FilterString(val value: String, vararg val target: FilterTarget = arrayOf(FilterTarget.LEAF)) : Filter() {

    override operator fun invoke(name: String, type: FilterTarget): Boolean {
        if (type !in target) return true
        return value == name
    }
}

class FilterNotString(val value: String, vararg val target: FilterTarget = arrayOf(FilterTarget.LEAF)) : Filter() {

    override operator fun invoke(name: String, type: FilterTarget): Boolean {
        if (type !in target) return true
        return value != name
    }
}

object FilterAlways : Filter() {
    override operator fun invoke(name: String, type: FilterTarget): Boolean = true
}

class FilterAnd(vararg val children: Filter) : Filter() {
    override operator fun invoke(name: String, type: FilterTarget): Boolean = children.all { it.invoke(name, type) }
}

class FilterOr(vararg val children: Filter) : Filter() {
    override operator fun invoke(name: String, type: FilterTarget): Boolean = children.any { it.invoke(name, type) }
}

class FilterNot(val child: Filter) : Filter() {
    override fun invoke(name: String, type: FilterTarget): Boolean = !child.invoke(name, type)
}