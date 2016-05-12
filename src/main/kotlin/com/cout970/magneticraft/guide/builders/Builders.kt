package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.components.PageComponent
import com.cout970.magneticraft.util.MODID

const val GUIDE_FOLDER = "$MODID:textures/gui/guide"

inline fun entry(title: String, init: EntryBuilder.() -> Unit) = EntryBuilder(title).apply(init).build()

inline fun page(init: PageBuilder.() -> Unit) = PageBuilder().apply(init).build()

inline fun image(init: ImageBuilder.() -> Unit) = ImageBuilder().apply(init).build()

inline fun icon(init: IconBuilder.() -> Unit) = IconBuilder().apply(init).build()

inline fun link(init: LinkBuilder.() -> PageComponent) = LinkBuilder().apply { base = init() }.build()

inline fun text(init: TextBuilder.() -> Unit) = TextBuilder().apply(init).build()

inline fun recipe(init: RecipeBuilder.() -> Unit) = RecipeBuilder().apply(init).build()