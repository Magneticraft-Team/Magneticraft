### TODO list
- add conveyor belt up/down blocks
- add insert animation
- add steam engine animation
- add sieve animation, particles and sound
- add grinder particles and sound
- add flare stack
- fix pipes collision box and add auto-import
- add heat system and remove all 'symbolic heat values'
- add gears for steam engine tiers (and add a fucking GUI)
- change tank model and add auto-export
- add conveyor belt end that drops stuff in chests
- add wire coil durability and remove auto-connect (make lag)

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
