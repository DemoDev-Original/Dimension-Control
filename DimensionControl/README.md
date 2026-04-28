# Dimension Control Mod

A powerful server-side Fabric mod for Minecraft 1.21.4 that gives administrators complete control over dimension access, item/block restrictions, and entity spawning.

## Features

### 🌍 Dimension Management
- **Enable/Disable Dimensions**: Completely block access to specific dimensions
- **Permission-Based Access**: Control which player ranks can enter each dimension
- **Real-time Configuration**: Changes apply immediately without server restart

### 🚫 Item & Block Restrictions
- **Block Item Obtainment**: Prevent players from picking up specific items in dimensions
- **Block Breaking**: Restrict which blocks can be broken in each dimension
- **Per-Dimension Rules**: Different restrictions for Overworld, Nether, End, and custom dimensions

### 👾 Entity Control
- **Spawn Prevention**: Block specific entities from spawning in dimensions
- **Complete Protection**: Works for natural spawns, spawn eggs, and commands

### 🎮 User-Friendly GUI
- **Inventory-Based Configuration**: Simple chest-style GUI for adding restrictions
- **Visual Management**: Place items/blocks in the GUI to add them to the blocked list
- **Auto-Save**: Configuration saves automatically when closing the GUI

### ⚙️ Flexible Configuration
- **JSON-Based**: Easy to read and edit configuration file
- **Hot Reload**: Reload config with a command without restarting
- **Permission System**: Rank-based access control with customizable permissions

## Installation

1. Download the mod JAR file
2. Place it in your server's `mods/` folder
3. Ensure you have Fabric API installed
4. Start/restart your server
5. Configuration file will be created at `config/dimensioncontrol.json`

## Requirements

- **Minecraft**: 1.21.4
- **Fabric Loader**: 0.16.9 or higher
- **Fabric API**: 0.119.4+1.21.4 or higher
- **Java**: 21 or higher

## Commands

All commands require permission level 2 (operator):

### Main Command
```
/dimensioncontrol
```

### Reload Configuration
```
/dimensioncontrol reload
```
Reloads the configuration file without restarting the server.

### Open GUI
```
/dimensioncontrol gui
```
Opens an inventory GUI to configure blocked items for your current dimension.

### Dimension Management
```
/dimensioncontrol dimension <dimension_id> enable
/dimensioncontrol dimension <dimension_id> disable
```
Examples:
```
/dimensioncontrol dimension minecraft:the_nether disable
/dimensioncontrol dimension minecraft:the_end enable
```

### Permission Management
```
/dimensioncontrol permission <player> set <level>
/dimensioncontrol permission <player> get
/dimensioncontrol permission <player> remove
```
Examples:
```
/dimensioncontrol permission Steve set dimensioncontrol.nether
/dimensioncontrol permission Alex get
/dimensioncontrol permission Bob remove
```

## Configuration

Default configuration file (`config/dimensioncontrol.json`):

```json
{
  "dimensions": {
    "minecraft:overworld": {
      "enabled": true,
      "blockedItems": [],
      "blockedBlocks": [],
      "blockedEntities": [],
      "allowedPermissions": []
    },
    "minecraft:the_nether": {
      "enabled": true,
      "blockedItems": ["minecraft:netherite_ingot"],
      "blockedBlocks": [],
      "blockedEntities": [],
      "allowedPermissions": ["dimensioncontrol.nether"]
    },
    "minecraft:the_end": {
      "enabled": true,
      "blockedItems": ["minecraft:elytra"],
      "blockedBlocks": [],
      "blockedEntities": [],
      "allowedPermissions": ["dimensioncontrol.end"]
    }
  },
  "enablePermissionSystem": true,
  "defaultPermissionLevel": "default"
}
```

### Configuration Options

- **enabled**: Whether the dimension is accessible
- **blockedItems**: List of item IDs that cannot be obtained
- **blockedBlocks**: List of block IDs that cannot be broken
- **blockedEntities**: List of entity IDs that cannot spawn
- **allowedPermissions**: List of permission levels that can access this dimension
- **enablePermissionSystem**: Global toggle for the permission system
- **defaultPermissionLevel**: Default permission level for players without explicit permissions

### Item/Block ID Format
Use the full identifier format:
```
namespace:path
```
Examples:
- `minecraft:diamond`
- `minecraft:netherite_ingot`
- `minecraft:elytra`
- `minecraft:ancient_debris`
- `modid:custom_item`

### Entity ID Format
Same as items:
```
namespace:entity_type
```
Examples:
- `minecraft:ender_dragon`
- `minecraft:wither`
- `minecraft:zombie`

## Usage Examples

### Example 1: Disable the Nether
```
/dimensioncontrol dimension minecraft:the_nether disable
```
Players will see: "§cThis dimension is currently disabled!" when attempting to enter.

### Example 2: Block Netherite in the Nether
1. Open GUI: `/dimensioncontrol gui` (while in the Nether)
2. Place a Netherite Ingot in the GUI
3. Close the GUI (auto-saves)

Any netherite ingots players try to pick up will be blocked with a message.

### Example 3: VIP-Only End Access
```
/dimensioncontrol permission VIP_Player set dimensioncontrol.end
```
Only players with the `dimensioncontrol.end` permission can enter the End.

### Example 4: Prevent Dragon Spawning
Edit `config/dimensioncontrol.json`:
```json
"minecraft:the_end": {
  "enabled": true,
  "blockedEntities": ["minecraft:ender_dragon"]
}
```
Then reload: `/dimensioncontrol reload`

### Example 5: Progressive Server
Create a progression system where players need permissions to access dimensions:
```
# Start: Only Overworld accessible
/dimensioncontrol dimension minecraft:the_nether disable
/dimensioncontrol dimension minecraft:the_end disable

# Grant Nether access to established players
/dimensioncontrol dimension minecraft:the_nether enable
/dimensioncontrol permission Player1 set dimensioncontrol.nether

# Grant End access to veteran players
/dimensioncontrol dimension minecraft:the_end enable
/dimensioncontrol permission Player1 set dimensioncontrol.end
```

## Permission System

### How It Works
1. Players without explicit permissions use the `defaultPermissionLevel`
2. Server operators (level 2+) bypass all restrictions
3. Dimensions with empty `allowedPermissions` are accessible to everyone
4. Dimensions with `allowedPermissions` only allow players with matching permission levels

### Permission Levels
You can define any permission level strings you want. Examples:
- `default`, `member`, `vip`, `admin`
- `dimensioncontrol.nether`, `dimensioncontrol.end`
- `tier1`, `tier2`, `tier3`

### Disabling Permission System
Set `enablePermissionSystem` to `false` in the config to disable permission checks entirely.

## Features in Action

### When a Player Tries to Enter a Disabled Dimension:
> §cThis dimension is currently disabled!

### When a Player Lacks Permission:
> §cYou don't have permission to enter this dimension!

### When a Player Picks Up a Blocked Item:
> §cYou cannot obtain this item in this dimension!

### When a Player Breaks a Blocked Block:
> §cYou cannot break this block in this dimension!

## Troubleshooting

### Config not loading?
- Check `logs/latest.log` for errors
- Ensure JSON syntax is valid (use a JSON validator)
- Try deleting the config file and restarting (creates fresh default)

### Restrictions not working?
- Use `/dimensioncontrol reload` after editing the config
- Verify item/block/entity IDs are correct (use F3+H in-game to see IDs)
- Check that the dimension ID matches (e.g., `minecraft:the_nether` not just `nether`)

### Players can still access dimensions?
- Operators (permission level 2+) bypass all restrictions by design
- Check if `enablePermissionSystem` is set to `true`
- Verify the dimension is set to `"enabled": false` if you want to block it entirely

## License

MIT License - Feel free to use and modify

## Support

For issues, feature requests, or questions, please create an issue on the project repository.