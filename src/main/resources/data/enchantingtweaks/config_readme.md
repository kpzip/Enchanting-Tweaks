## Enchanting Tweaks Configuration

Configuration is stored in `<Your Minecraft Folder>/config/enchantingtweaks`.<br>
this file serves to explain the function of all of the options in enchanting-tweaks-config.json

# bypassAnvilMaxLevel

`true` or `false`<br>
if set to true, anvils will not limit the amount of levels you can spend at one time (the "TOO EXPENSIVE!" text)<br>
if set to false, it behaves like vanilla.

# showAllLevelEnchantedBooksInCreativeInventory

`true` or `false`<br>
if set to true, it behaves like vanilla.<br>
if set to false, only max level enchanted books will be shown in the creative inventory.

# enchantmentCommandAbidesByMaxLevel

`true` or `false`<br>
if set to true, it behaves like vanilla.<br>
if set to false, the `/enchant` command will allow any level of enchantment to be placed on items, regardless of the enchantment's maximum level.

# allowRiptideAlways

`true` or `false`<br>
if set to true, the `Riptide` enchantment will work regardless of whether or not the player is in rain or water.<br>
if set to false, it behaves like vanilla.

# maxLevels

each entry contains the `id` of an enchantment and it's `maximum level`. (Including all vanilla Enchantments and ones added by other mods)
changing the `maximum level` will change the maximum obtainable level of that enchantment in-game. Supports modded enchantments.

# exclusivity

each entry contains the id's of two enchantments and one boolean value. If set to true, those enchantments will be allowed to combine. If set to false they will not be allowed to combine. Supports modded enchantments.
