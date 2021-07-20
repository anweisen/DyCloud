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

