# MC-Pillage
Minecraft faction plugin which is developed for two teams.

## Features
 + Temporal Build/ Break protection
 + Events
 + Factions
 + Basic Farmworlds
 + Egg Shop
 
 ## Commands
 - (Op) ... Operator Rights
 - (Lord) ... Lord Role
 - (Lieutenant) ... Lieutenant Role
 + **/faction**
        
        * add <username> (Lieutenant)
        * kick <username> (Lieutenant)
        * promote <username> (Lord) # There only two roles so it also works as demote
        * members
        * spawn
        * forceadd <username> <team> (Op) # this member is automaticaly Lord
        * forceleader <username> <team> (Op) # Updates Role
        
 + **/war** 
        
        * start <attacking_team> (Op)
        * stop (Op)
        * info
 
 + **/farm**
 
        * nether
        * overworld
        * end
        * create <farm_world, farm_nether, farm_end> (Op)
 
  + **/eggshop**
  
        * <see /eggshop lists you all available spawneggs>
        
              
**Note** that the teleportation commands works differently for Player with op rights and in creative mode.


 ## Change Log
 
 **v0.1a** (2019.x.x)
 
 + Added war faction and farm commands
 + Added Event Manager
 + Place and Break protection
 + Added Faction Chat
 + Added Shop for Spawneggs
 + Added Auto Trigger for war events
 + Added Spawn Protection
