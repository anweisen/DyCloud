[![CI](https://img.shields.io/github/workflow/status/anweisen/DyCloud/CI?style=for-the-badge)](https://github.com/anweisen/DyCloud/actions/workflows/ci.yml)
[![wakatime](https://wakatime.com/badge/github/anweisen/DyCloud.svg?style=for-the-badge)](https://wakatime.com/badge/github/anweisen/DyCloud)
 ![lines](https://img.shields.io/tokei/lines/github/anweisen/DyCloud?style=for-the-badge)

# DyCloud
A minecraft cloud system using docker & netty supporting multiroot & multiproxy for learning purpose

**This project is only intended as a learning experience**.
You should **not use this cloud system for production**, as no support will be given and the project may be very inactive and lack features.

I would recommend you to use one of the **following cloud systems**:
- [CloudNet-v3](https://github.com/CloudNetService/CloudNet-v3)
- [CloudNet-v2](https://github.com/CloudNetService/CloudNet)
- [SimpleCloud](https://github.com/theSimpleCloud/SimpleCloud)
- [TimoCloud](https://github.com/TimoCloud/TimoCloud)

## Structure

- **master** (*base*): <br>
  Manager of the cloud, commander of nodes <br>
  *Maybe known as manager or base from other cloud systems*
- **node** (*base, remote*): <br>
  Responsible for starting & stopping of services (minecraft servers, bungee proxies), connects to the master. <br>
  *Probably known as wrapper or slave from other cloud system*
- **wrapper** (*remote*): <br>
  Runs a service, connects to the master. <br>
  *Probably known as runner or bridge from other cloud systems*

## Permissions

- **cloud.join.maintenance**: Player with this permission will be able to join during maintenance (*Cloud-Bridge*)
- **cloud.join.full**: Player with this permission will be able to join although the server is full (*Cloud-Bridge*)
- **cloud.auto.op**: Player with this permission will automatically be set as operators on bukkit servers (*Cloud-Perms*)
- **cloud.notify**: Player with this permission will receive messages when services are started/stopped (*Cloud-Notify*)
- **cloud.chat.color**: Player with this permission will be able to send colored message using ``&`` (*Cloud-ChatTab*)
- **cloud.command.use**: Player with this permission will be able to use ``/cloud`` commands (*Cloud.Bridge*)

## Modules

- **Bridge**: Allows the use of player management possible <br>
- **Perms**: Allows the use of an integrated permission system <br>
- **Cloudflare**: Allows the use of a multiproxy setup using round-robin dns load balancing <br>
- **Notify**: Allows the sending of service update messages to players <br>
- **Proxy**: Allows the use of proxy systems & configs <br>
  - Tablist Replacements:
    - ``{ping}`` The ping of the player provided by the proxy
    - ``{name}`` The name of the player
    - ``{server}`` The name of the server the player is currently on
    - ``{proxy}`` The name of the proxy the player is connected to
    - ``{node}`` The name of the proxy's node the player is connected to
    - ``{group.name}`` The name of the player's highest group
    - ``{group.color}`` The color of the player's highest group
    - ``{group.display}`` The display name of the player's highest group
    - ``{players.online}`` The count of online players on the network
    - ``{players.max}`` The max count of player on the network defined in *global.json* as *maxPlayers*
  - Motd Replacements:
    - ``{proxy}`` The name of the proxy the player is pinging
    - ``{node}`` The name of the proxy's node the player is pinging
    - ``{players.online}`` The count of online players on the network
    - ``{players.max}`` The max count of player on the network defined in *global.json* as *maxPlayers*
- **ChatTab**: Runtime module to enable the use of chat formatting & name tags
  - Message Replacements:
    - ``{message}`` The message sent by the player
    - ``{player.uuid}`` The uuid of the player
    - ``{player.name}`` The name of the player
    - ``{player.display}`` The display name of the player
    - ``{group.name}`` The name of the player's highest group
    - ``{group.color}`` The color of the player's highest group
    - ``{group.display}`` The display name of the player's highest group

## Progress

| 📁  | Name / Label / Description                                                                  |
|-----|---------------------------------------------------------------------------------------------|
| ✔️ | Logging                                                                                     |
| ✔️ | Basic Networking with Netty                                                                 |
| ✔️ | Advanced Networking: Chunked Packets & Queries                                              |
| ✔️ | Node Management + Authentication                                                            |
| ✔️ | Request API with Packets                                                                    |
| ✔️ | Event System                                                                                |
| ✔️ | Master TemplateStorage -> Download                                                          |
| ✔️ | Docker: Wrapper Setup                                                                       |
| ✔️ | Service Start / Stop                                                                        |
| ✔️ | Wrapper Authentication                                                                      |
| ❌  | Proper Docker Connection (Not Legacy)                                                       |
| 🚧  | Node Information Cycle & Timeout                                                            |
| ❌  | Node Load Balancing                                                                         |
| ❌  | Auto Client Reconnect                                                                       |
| ❌  | Encryption for sockets                                                                      |
| ❌  | Pre launch setup                                                                            |
| 🚧  | REST API                                                                                    |
| 🚧  | Websocket API for Dashboard                                                                 |
| ❌  | SSL Encryption for http server                                                              |
| ✔️ | ServiceInfo publish                                                                         |
| ✔️ | Basic Service Events                                                                        |
| 🧪 | Service auto start (minCount & maxCount)                                                    |
| 🧪 | Shutdown mechanism                                                                          |
| ❌  | Template Cache                                                                              |
| ✔️ | Proxy Bridge                                                                                |
| 🚧  |  Support for Velocity                                                                       |
| 🚧  | Support for MineStorm                                                                       |
| 🚧  | Support for SpongePowered                                                                   |
| ✔️ | Dynamic Proxy Server Registry                                                               |
| 🧪 | MultiProxy Cord                                                                             |
| ✔️ | Lobby Balancing                                                                             |
| ✔️ | Remote Database API                                                                         |
| 🧪 | Service Timeout (Crash detection) -> Delete -> Start new                                    |
| ❌  | Messenger System for Modules                                                                |
| ✔️ | Bukkit Bridge                                                                               |
| 🚧  | Implement all driver functions for wrapper & node (remote)                                  |
| ❌  | Wrapper & Node to Master Logging                                                            |
| ✔️ | Player Executor                                                                             |
| ✔️ | Global Player Management                                                                    |
| ✔️ | Permission System                                                                           |
| 🧪 | Permission Chat & Tab Extension                                                             |
| ✔️ | Module System (Master, Node?, Wrapper?)                                                     |
| ✔️ | Module Copy                                                                                 |
| ✔️ | Notify Module                                                                               |
| ✔️ | Synced Proxy Module                                                                         |
| ❌  | Service Screens                                                                             |
| ✔️ | Message Translations                                                                        |
| ✔️ | Advanced Translations (link to other messages & chatevents eg run command)                  |
| ✔️ | Proxy Commands (+ Cloud Command API?)                                                       |
| 🚧  | Cloud Commands                                                                              |
| ❌  | Migrate to Gradle                                                                           |
| ❌  | Relocate google depends & netty to prevent issues from server software using older versions |
| ⚠️ | Service Deployments                                                                         |
| ⚠️ | Static Services                                                                             |
| ⚠️ | NPC Module                                                                                  |
| ⚠️ | Signs Module                                                                                |
| ⚠️ | [CloudAPI](https://github.com/anweisen/CloudAPI) Implementation                             |

``✔️`` **Finished** <br>
``🧪`` **Experimental** <br>
``🚧`` **In Progress** <br>
``❌`` **Planned** <br>
``⚠️`` **Planned (Unimportant)**
