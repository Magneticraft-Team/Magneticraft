# Inserters

Inserters are a cheap way to transport items in really short distances, for example moving items from a chest to a conveyor belt. 
An inserter can grab items from an inventory in the back and drop those items to an inventory in the front, 
you can know which way is the front by the yellow pixels at the base. 
The inserter will only pick up items that can be inserted in the target inventory.

The inserters doesn't use power but they are quite slow, you can add a speed upgrade to make them twice as 
fast, but you can only use 1 speed upgrade. You can also use an stack upgrade, by default the inserter can only take 8 
items at the time, but with this upgrade it can take a whole stacks.

An inserter has a 9 filter slots at the left and 6 buttons at the right, the 2 buttons at the top right control 
if the inserter can grab and drop items into the ground, the rest of the buttons control the filters.

The button at the bottom left indicates if the filter is in whitelist or blacklist mode, 
in whitelist it will pick only the items marked in the filter, in blacklist it will pick all items that are not marked 
in the filter.

The rest of the buttons are:
- M: if it's marked the inserter will use the item metadata to check if an item matches the filter
- ORE: if it's marked the inserter will check if any of the ore dictionary names of the filter matches with any of the ore dictionary names of the item
- NBT: if it's marked the inserter will use the item NBT to check if an item matches the filter