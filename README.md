# MinecraftCloud
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
- ~~**cord** (*remote*)~~: <br>
  A really simple multi proxy cord. When a client connect to the cord all its packets will be forwarded (client <-> cord <-> proxy). <br>
  But using this cord basically is of no use and as you then just have one big proxy which is the cord which can then be easily crashed. <br>
  **For a multiproxy setup you should use something like a rotating dns system and not this**

## Progress

 📁 | Name / Label / Description
--- | --------------------------
✔️ | Logging
✔️ | Basic Networking with Netty
✔️ | Advanced Networking: Chunked Packets & Queries
✔️ | Node Management + Authentication
✔️ | Request API with Packets
✔️ | Event System
✔️ | Master TemplateStorage -> Download
✔️ | Docker: Wrapper Setup
✔️ | Service Start / Stop
✔️ | Wrapper Authentication
❌ | Proper Docker Connection (Not Legacy)
❌ | Node Information Cycle & Timeout
❌ | Node Load Balancing
✔️ | ServiceInfo publish
🚧 | Basic Service Events
🚧 | Service auto start (minCount & maxCount)
🧪 | Shutdown mechanism
❌ | Template Cache
🚧 | Proxy Bridge
🧪 | Dynamic Proxy Server Registry
🚧 | MultiProxy Cord
🧪 | Lobby Balancing
✔️ | Remote Database API
❌ | Messenger System for Modules
🚧 | Bukkit Bridge
🚧 | Implement all driver functions for wrapper & node (remote)
❌ | Wrapper & Node to Master Logging
✔️ | Player Executor
🧪 | Global Player Management
🧪 | Permission System
🚧 | Permission Chat & Tab Extension
✔️ | Module System (Master, Node?, Wrapper?)
🧪 | Module Copy
❌ | Cloud Commands
❌ | Service Screens
❌ | Proxy Commands (+ Cloud Command API?)
⚠️ | Service Deployments
⚠️ | Static Services
⚠️ | NPC Module
⚠️ | Signs Module
⚠️ | [CloudAPI](https://github.com/anweisen/CloudAPI) Implementation

``✔️`` **Finished** <br>
``🧪`` **Experimental** <br>
``🚧`` **In Progress** <br>
``❌`` **Planned** <br>
``⚠️`` **Planned (Unimportant)**
