# Bugs

### TODO list
- expand container
- add conveyor belt up/down blocks
- add flare stack
- add gears for machines
- add wire coil durability
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
- Rockets/Rocket silo
- Better ore processing (galvanization tank, Allow furnace)
- Geothermal power
- Quarry?
- Particles, lots of particles
- Textil factory
- Emerald nuggets for tradding?
- Pump?

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
