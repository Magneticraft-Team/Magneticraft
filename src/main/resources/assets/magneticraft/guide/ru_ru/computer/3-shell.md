# Shell

This programs is inspired by the linux shell (sh), but it's a lot more basic.
The program can be loaded the same way as the other, inserting the shell floppy disk and restarting the computer.

The program adds the following commands:
- help: prints all available commands
- ls: list the content of the current directory
- cd: changes the current directory
- mkdir: makes a new directory
- rm: removes a file or directory
- format: formats a disk so it can have files and directories
- free: shows the free space in a disk
- fs: shows metadata about the filesystem
- cat: shows the content of a files
- touch: creates a empty file
- write: lets you write some lines into a file
- update_disk: when the shel is unable to detect if a disk is formatted this fixes it
- pastebin: let you download stuff from pastebin
- update: downloads a new version of a program
- quarry: mines a squared area, only works in miner robots

The most useful command probably will be 'quarry', in a mining robot allow you to mine an area automatically, right now the 
mining robots don't use energy and can't move items by themselves. You will need pipes or something to extract the items.

Other useful command is 'update', allow you to download the latest version of a program, right now the available programs 
are 'forth.bin', 'lisp.bin', 'shell.bin'. Updating requires a new floppy disk to store the new program. 
Even if the mod doesn't update you can get new versions of the programs with bug fixes or new features.