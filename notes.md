# Bugs

cobalt as ore subproduct (OP)
shelving unit crash + not blocking keys
wires breaking when chunks load without order
too much microcrafting
shelving unit chest right click
thermopile JEI recipes

### TODO list
- expand container
- fix guide
- add conveyor belt up/down blocks
- add sieve particles and sound
- add grinder particles and sound
- add flare stack
- add gears for steam engine tiers
- add wire coil durability and remove auto-connect (make lag)
- change pumpjack mining mechanic for : amount of oil near = speed of extraction
- fix recipe progression

### Ideas to add at some point
- Realistic reactors
- Voltage tiers
- Rainbow cables
- Computer controlled stuff
- Logistics
- Boats?
- Crafter/Assembler
- Laser Cutter
- Farms
- Original Tanks
- Auto-extract pipes
- Steam tubine
- Rockets/Rocket silo
- Better ore processing (galvanization tank, Allow furnace)
- Geothermal power
- Quarry?
- Chainsaw, Drill, pneumatic wrench
- Particles, lots of particles
- Textil factory
- Emerald nuggets for tradding?
- Pump?
- Shelving unit ordering...


### Steps to add a new multiblock
- Add Controller Block
- Add TileEntity
- Add Multiblock class 
    - Create class
    - Add hitboxes
    - Add pattern
    - Update properties, name, size, center
    
- Add TileEntityRenderer
    - Create class
    - Add texture
    - Add model
    - Import model
    - Add animations if needed
    
- Add Gui and Container
    - Create class
    - Import texture
    - Add components
    - Set component parameters
    - Add slots and slot regions
    
- Add Multiblock logic
    - Inventory
    - Energy
    - Fluids
    - Connections
    - Items IO
    - Crafting if needed
        - Create crafting process
        - Crate recipe manager, and recipe clases
        - Update api
        - Update JEI plugin
        
### Steps to add a new block

- Add a new block instance
    - Add new methods if needed
       
- Add TileEntity
- Add Multiblock class 
    - Create class
    - Add hitboxes
    - Add pattern
    - Update properties, name, size, center
    
- Add TileEntityRenderer
    - Create class
    - Add texture
    - Add model
    - Import model
    - Add animations if needed
    
- Add Gui and Container
    - Create class
    - Import texture
    - Add components
    - Set component parameters
    - Add slots and slot regions
