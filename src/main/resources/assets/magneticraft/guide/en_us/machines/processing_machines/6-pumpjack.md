# Pumpjack

The pumpjack is able to extract oil from the rocks (oil sources) near an oil deposit. 
It first scans for oil underneath it and if it founds some oil, starts scanning the entire deposit, 
then it will start to drill, the machine can detect if there is an oil deposit without energy, 
but for scanning the deposit and drilling to it needs a supply of power.

When the drilling is finished, the pumpjack will extract de oil of the deposit block by block.
After depleting an oil block, it will search for the next one until the deposit gets empty. 

The pumpjack requires 80 Watts to work at full speed.

The best way find oil is to search in places where the coordinates x and z are multiples of 2560 and
 then travel 128 block to the south and then 128 block to the east, you will be at the center of an oil deposit.

The oil deposits are located between 16 and 24 in the Y axis.

This structure is a [multiblock](../../3-multiblocks.md) of size *3, 5, 6* (x, y, z).

Materials needed:
- 1 x Pumpjack Blueprint
- 16 x Machine Block
- 6 x Support Column
- 33 x Iron Grate Machine Block
- 4 x Corrugated Iron Machine Block
- 1 x Copper Coil Machine Block