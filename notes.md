### TODO list

- update conveyor belt model
- add conveyor belt corners
- add conveyor belt up/down blocks
- add insert animation
- add steam engine animation
- add sieve animation, particles and sound
- add grinder particles and sound
- add flare stack
- add pumpjack
- fix pipes collision box and add auto-import
- add heat system and remove all 'symbolic heat values'
- add gears for steam engine tiers (and add a fucking GUI)
- add gui to the steam boiler
- change tank model and add auto-export
- add conveyor belt end that drops stuff in chests
- add wire coil durability and remove auto-connect (make lag)
- add shipping crate/container
- the rest of the things that i can't remember

### change log
- updated forge
- fix manual markdown support
- fix ming robot desync on login


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
