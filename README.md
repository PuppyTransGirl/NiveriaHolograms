# NiveriaHolograms

NiveriaHolograms is a Minecraft plugin that provides persistent, configurable holograms (text, item, block) with a full command set and migration utility. \
It is built for Paper and depends on NiveriaAPI.

## Table of contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Basic Usage & Commands](#basic-usage--commands)
  - [/niveriaholograms](#niveriaholograms)
  - [/hologram (aliases: /holo, /nholo)](#hologram-aliases-holo-nholo)
  - [Examples (common subcommands)](#examples-common-subcommands)
- [Permissions](#permissions)
- [Migration](#migration)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)
- [Acknowledgements](#acknowledgements)

## Features
- Create persistent holograms of multiple types:
  - Text holograms (multi-line text, colors, formatting)
  - Item holograms (display an item as a hologram)
  - Block holograms (display a block as hologram)
- Full command-driven management:
  - Create, edit, clone, remove holograms
  - List and display holograms near you
  - Teleport to a hologram
  - View detailed hologram info
- Migration tool to import holograms from other plugins
- Persistence via built-in loader/saver (holograms are saved to plugin data folder)
- Language support via NiveriaAPI Lang system (messages are translatable)
- Update checker

## Requirements
- Paper Minecraft server 1.21.4-1.21.11
- Java 21+ (match your server runtime)
- [NiveriaAPI](https://modrinth.com/plugin/niveriaapi) plugin installed
- The plugin jar placed in the `plugins/` folder

## Installation
1. Place `NiveriaHolograms.jar` into the server `plugins/` directory.
2. Ensure `NiveriaAPI` is installed and present in `plugins/`.
3. Start the server.
4. Use the provided commands to create and manage holograms.

## Basic Usage & Commands
Two top-level command roots are registered:

- /niveriaholograms — administration (migrate, reload)
- /hologram (aliases: /holo, /nholo) — hologram management

Examples and common subcommands:

- Open migration GUI (admin)
  - /niveriaholograms migrate
  - Opens a MigrationMenu GUI to migrate holograms from supported plugins.

- Reload plugin data / translations
  - /niveriaholograms reload
  - Reloads language files and holograms; returns the time taken to reload.

Hologram management root (aliases `/holo` and `/nholo`):

- Create a hologram
  - /hologram create \<type> \<name>
  - Permission: niveriaholograms.command.hologram.create
  - Example: `/hologram create text welcome_sign`
  - Types are typically: `text`, `item`, `block` (see server messages or HologramType enum for exact names)
  - You are expected to stand where you want the hologram placed when executing create.

- Clone a hologram
  - /hologram clone \<existingName> \<newName>
  - Permission: niveriaholograms.command.hologram.clone
  - Copies configuration of an existing hologram for the executing player.

- Edit a hologram
  - /hologram edit \<hologramName> \<property> \<value>
  - Permission: niveriaholograms.command.hologram.edit.<property>
  - Use the in-game edit subcommands to change text, item, offsets, visibility, etc. (the plugin exposes edit actions via the edit command set).

- List holograms
  - /hologram list
  - Permission: niveriaholograms.command.hologram.list
  - Lists all holograms.

- Nearby holograms
  - /hologram nearby \<radius>
  - Permission: niveriaholograms.command.hologram.nearby
  - Shows holograms within a specified radius of your location.

- Remove a hologram
  - /hologram remove \<hologramName>
  - Permission: niveriaholograms.command.hologram.remove
  - Permanently delete the hologram from storage.

- Teleport to a hologram
  - /hologram teleport \<hologramName>
  - Permission: niveriaholograms.command.hologram.teleport
  - Teleports you to the hologram location.

- Show hologram info
  - /hologram info \<hologramName>
  - Permission: niveriaholograms.command.hologram.info
  - Displays owner, location, type and configuration details.

If you are unsure about exact argument names or available options, run a subcommand without arguments or consult the in-game help messages (NiveriaAPI Lang messages).

## Permissions
- niveriaholograms.command.hologram - root hologram management
- niveriaholograms.command.hologram.edit.\<property> - hologram editing
- niveriaholograms.command.niveriaholograms - general admin root
- niveriaholograms.command.niveriaholograms.reload - reload command
- niveriaholograms.command.niveriaholograms.migrate - migration GUI

## Migration
Use `/niveriaholograms migrate` to open the migration GUI (MigrationMenu).

## Contributing
Contributions are welcome !
Please feel free to submit a pull request or open an issue for any bugs or feature requests :3

## License
This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for details.

## Support
Open an issue on the repository or contact the repository owner via GitHub for support, feature requests or questions.

## Acknowledgements
- Thanks to [FancyHolograms](https://github.com/FancyInnovations/FancyPlugins) who I took the command layout inspiration from.
