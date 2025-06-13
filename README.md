# Overview

# How to Run

`make startApp` will run the gradle application with entry on the `MasterCoordinator` class.
The `MasterCoordinator` will read the `deployment-config.json` file, find free ports, and spin up instances of `KVNode`
accordingly.

Once up and running, the external HTTP ports will be printed on the console. Use these ports for commands to target 
specific KV instances. 

To put a new key value pair: `make putKeyValue PORT=<port_number> KEY=<key_string> VALUE=<value_string>`
To get a value for a given key: `make getKey PORT=<port_number> KEY=<key_string>`
To delete a key if it exists: `make deleteKey PORT=<port_number> KEY=<key_string>`
