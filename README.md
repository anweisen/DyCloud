# MinecraftCloud
A simple minecraft cloud system using docker & netty supporting multiroot & multiproxy for learning purpose

**This project is only intended as a learning experience**.
You should **not use this cloud system for production**, as no support will be given and the project may be very inactive and lack features.

I would recommend you to use one of the **following cloud systems**:
- [CloudNet-v3](https://github.com/CloudNetService/CloudNet-v3)
- [CloudNet-v2](https://github.com/CloudNetService/CloudNet)
- [SimpleCloud](https://github.com/theSimpleCloud/SimpleCloud)
- [TimoCloud](https://github.com/TimoCloud/TimoCloud)

## Structure

- **driver**
  - **base**
	- **master**: Manager of the cloud, commander of nodes <br>
	  			  *Maybe known as manager or base from other cloud systems*
	- **node** (*semi remote*): Responsible for starting & stopping of services (minecraft servers, bungee proxies). <br>
	            *Probably known as wrapper or slave from other cloud system*
  - **wrapper** (*remote*): Runs on a service, connects to the master. <br>
				*Probably known as runner or bridge from other cloud systems*

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
🚧 | Docker: Wrapper Setup
❌ | Service Start / Stop
❌ | Wrapper Authentication
❌ | Proper Docker Connection (Not Legacy)
❌ | Node Information Cycle & Timeout
❌ | Basic Service Events
❌ | Service auto start (minCount & maxCount)
❌ | Shutdown mechanism
❌ | Template Cache
❌ | MultiProxy Cord
❌ | Lobby Balancing
✔️ | Remote Database API
❌ | Messenger System for Modules
❌ | Proxy Bridge
❌ | Bukkit Bridge
❌ | Implement all driver functions for wrapper & node (remote)
❌ | Wrapper & Node to Master Logging
❌ | Player Executor
❌ | Permission System
❌ | Permission Chat & Tab Extension
❌ | Module System (Master, Node?, Wrapper?)
❌ | Cloud Commands
❌ | Service Screens
❌ | Proxy Commands (+ Cloud Command API?)
⚠️ | Service Deployments
⚠️ | Static Services
⚠️ | [CloudAPI](https://github.com/anweisen/CloudAPI) Integration

``✔️`` Finished - Implemented <br>
``🚧`` Current Goal - In Progress <br>
``❌`` Planned <br>
``⚠️`` Not important - Long time planned goals 
