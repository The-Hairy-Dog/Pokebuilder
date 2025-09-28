package com.thehairydog.pokebuilder.gui.editors

import ClickableSlot
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.pokemon.Pokemon
import com.thehairydog.pokebuilder.gui.PokebuilderOpenMenus
import com.thehairydog.pokebuilder.gui.slotUtil.LockedSlot
import com.thehairydog.pokebuilder.util.PokeItemFormatter.configureNatureItem
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class PokeBuilderNatureEditor(
    syncId: Int,
    player: ServerPlayer,
    pokemon: Pokemon
) : AbstractContainerMenu(MenuType.GENERIC_9x3, syncId) {

    private val container = SimpleContainer(45)

    init {
        for (i in 0 until 45) {
            val slotX = 8 + (i % 9) * 18
            val slotY = 18 + (i / 9) * 18

            val itemStack = when (i) {
                // Neutral Natures
                8  -> configureNatureItem(CobblemonItems.SERIOUS_MINT, Natures.BASHFUL)
                17 -> configureNatureItem(CobblemonItems.SERIOUS_MINT, Natures.DOCILE)
                26 -> configureNatureItem(CobblemonItems.SERIOUS_MINT, Natures.HARDY)
                35 -> configureNatureItem(CobblemonItems.SERIOUS_MINT, Natures.QUIRKY)
                44 -> configureNatureItem(CobblemonItems.SERIOUS_MINT, Natures.SERIOUS)

                // Attack +, Others -
                0  -> configureNatureItem(CobblemonItems.ADAMANT_MINT, Natures.ADAMANT)
                9  -> configureNatureItem(CobblemonItems.BRAVE_MINT, Natures.BRAVE)
                27 -> configureNatureItem(CobblemonItems.LONELY_MINT, Natures.LONELY)
                36 -> configureNatureItem(CobblemonItems.NAUGHTY_MINT, Natures.NAUGHTY)

                // Defense +, Others -
                1  -> configureNatureItem(CobblemonItems.BOLD_MINT, Natures.BOLD)
                10 -> configureNatureItem(CobblemonItems.IMPISH_MINT, Natures.IMPISH)
                19 -> configureNatureItem(CobblemonItems.LAX_MINT, Natures.LAX)
                28 -> configureNatureItem(CobblemonItems.RELAXED_MINT, Natures.RELAXED)

                // Special Attack +, Others -
                2  -> configureNatureItem(CobblemonItems.MODEST_MINT, Natures.MODEST)
                11 -> configureNatureItem(CobblemonItems.MILD_MINT, Natures.MILD)
                20 -> configureNatureItem(CobblemonItems.QUIET_MINT, Natures.QUIET)
                29 -> configureNatureItem(CobblemonItems.RASH_MINT, Natures.RASH)

                // Special Defense +, Others -
                6  -> configureNatureItem(CobblemonItems.CALM_MINT, Natures.CALM)
                15 -> configureNatureItem(CobblemonItems.CAREFUL_MINT, Natures.CAREFUL)
                24 -> configureNatureItem(CobblemonItems.GENTLE_MINT, Natures.GENTLE)
                33 -> configureNatureItem(CobblemonItems.SASSY_MINT, Natures.SASSY)

                // Speed +, Others -
                7  -> configureNatureItem(CobblemonItems.TIMID_MINT, Natures.TIMID)
                16 -> configureNatureItem(CobblemonItems.HASTY_MINT, Natures.HASTY)
                25 -> configureNatureItem(CobblemonItems.JOLLY_MINT, Natures.JOLLY)
                34 -> configureNatureItem(CobblemonItems.NAIVE_MINT, Natures.NAIVE)

                else -> ItemStack.EMPTY
            }

            val slot = if (itemStack.isEmpty) {
                LockedSlot(container, i, slotX, slotY) // or a normal slot
            } else {
                ClickableSlot(container, i, slotX, slotY) {
                    when (i) {
                        // Neutral Natures
                        8  -> PokebuilderOpenMenus.openConfirmInfuse(
                            pokemon, player, 20, ItemStack(CobblemonItems.SERIOUS_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.BASHFUL
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Bashful!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        17 -> PokebuilderOpenMenus.openConfirmInfuse(
                            pokemon, player, 20, ItemStack(CobblemonItems.SERIOUS_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.DOCILE
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Docile!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        26 -> PokebuilderOpenMenus.openConfirmInfuse(
                            pokemon, player, 20, ItemStack(CobblemonItems.SERIOUS_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.HARDY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Hardy!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        35 -> PokebuilderOpenMenus.openConfirmInfuse(
                            pokemon, player, 20, ItemStack(CobblemonItems.SERIOUS_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.QUIRKY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Quirky!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        44 -> PokebuilderOpenMenus.openConfirmInfuse(
                            pokemon, player, 20, ItemStack(CobblemonItems.SERIOUS_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.SERIOUS
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Serious!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )

                        // Attack+
                        0  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.ADAMANT_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.ADAMANT
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Adamant!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        9  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.BRAVE_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.BRAVE
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Brave!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        27 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.LONELY_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.LONELY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Lonely!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        36 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.NAUGHTY_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.NAUGHTY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Naughty!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )

                        // Defense+
                        1  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.BOLD_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.BOLD
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Bold!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        10 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.IMPISH_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.IMPISH
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Impish!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        19 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.LAX_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.LAX
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Lax!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        28 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.RELAXED_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.RELAXED
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Relaxed!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )

                        // Special Attack+
                        2  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.MODEST_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.MODEST
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Modest!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        11 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.MILD_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.MILD
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Mild!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        20 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.QUIET_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.QUIET
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Quiet!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        29 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.RASH_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.RASH
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Rash!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )

                        // Special Defense+
                        6  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.CALM_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.CALM
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Calm!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        15 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.CAREFUL_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.CAREFUL
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Careful!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        24 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.GENTLE_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.GENTLE
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Gentle!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        33 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.SASSY_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.SASSY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Sassy!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )

                        // Speed+
                        7  -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.TIMID_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.TIMID
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Timid!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        16 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.HASTY_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.HASTY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Hasty!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        25 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.JOLLY_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.JOLLY
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Jolly!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                        34 -> PokebuilderOpenMenus.openConfirmInfuse(pokemon, player, 20, ItemStack(CobblemonItems.NAIVE_MINT),
                            onConfirm = { poke, ply ->
                                poke.nature = Natures.NAIVE
                                ply.sendSystemMessage(Component.literal("${poke.nickname?.string ?: poke.species.name} is now Naive!"))
                            },
                            onCancel = { PokebuilderOpenMenus.openNaturePage(player, pokemon) }
                        )
                    }
                }
            }

            container.setItem(i, itemStack)
            addSlot(slot)
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack? = ItemStack.EMPTY
    override fun stillValid(player: Player): Boolean = true
}
