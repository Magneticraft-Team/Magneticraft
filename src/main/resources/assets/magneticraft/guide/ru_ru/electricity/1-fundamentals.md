# Electricity Fundamentals

The mechanics of the energy system tries to imitate the real behaviour of electricity,
so the formulas I = V/R and W = I*R work and can be used to predict the behaviour of the system

Fortunately there is no need to use math to figure out how everything works,
like any other energy system there are generators and machines that consume energy,
the generators will make energy until the network voltage reaches a limit (120V-125V),
voltage can be though as the energy stored in the cables, 
the voltage will be distributed across all connected devices so every block has the same voltage, 
however the generator will try to make the voltage higher and the machines will try to make the voltage lower,
this will cause a difference between the voltage in the start and the end of the network, 
this difference will create a flow of electricity (amperage or intensity) that will carry the energy from the generators to the consumers.

Most machines have an internal battery, when the voltage is higher than a internal limit the battery will start to charge,
if the voltage drops below another internal limit, the battery will start to discharge. 
This means that the batteries will keep the voltage at least at the lower limit, while they have power, this is important,
because the energy that is moved trough cables is calculated by Energy = Voltage * Amperage, 
this means that the total energy moved depends on the flow of electricity and the voltage, 
so the higher the voltage the more energy is transmitted.

The system has energy loss over distance, this doesn't mean the the cables delete energy, 
this is caused by the voltage drop in the network, 
for example: if we have a generator and a furnace,
the generator will be constantly trying to make the voltage higher, while the furnace tries to lower the voltage, 
both machines just create or consume energy in form of amperage (electric flow),
to create amperage the generator uses the formula I = W/V, 
so it produces amperage (electric flow) based on the energy that want to produce and the voltage in the generator block,
the furnace does the same, I = W/V drains electricity based on the energy that want to consume and the current voltage on the furnace block,
so if the voltage is higher in generator the amperage (electric flow) created will be lower,
and if the voltage is lower in the furnace the amperage drained will be high, 
this will cause the generator to create less energy and the furnace to use more, this means there is energy loss, 
even if the two machines want to produce/consume the same amount of energy.

The energy loss depends on the difference of the two voltages and this depends on the total resistance between the blocks,
this is the resistance of 1 block multiplied by the distance, so distance affect losses.