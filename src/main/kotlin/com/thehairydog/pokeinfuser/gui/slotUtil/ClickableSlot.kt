import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class ClickableSlot(
    container: SimpleContainer,
    index: Int,
    x: Int,
    y: Int,
    val onClick: () -> Unit
) : Slot(container, index, x, y) {

    override fun mayPickup(player: Player): Boolean {
        onClick() // Trigger your click action
        return false // Prevent taking the item out
    }

    override fun getItem(): ItemStack = super.getItem()
}
