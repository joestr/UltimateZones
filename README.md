# UltimateZones

UltimateZones is the modified version of my fifth created Zone System for Minecraft. 
It supports many features and uses own plugins to connect to the database and control zones

# Features
0. Allow Players to protect areas
0. The possibility to create Child Zones in Zones with an unlimited depth
0. Two different Child Zone Types: Extended (inherits rights of parent) and Independent
0. Many rights (known as flags) for Zone Owners to give to other Players (currently over 27)
0. The possibility to control each Block Type inside of Zones
0. Multilingual with easy to modify xml files
0. Uses the new Text/Chat System of Minecraft over ChatComponents
0. Can automatically uses the Players Client locale to translate messages (and item names)
0. Easy (Over Presets) or profi rights system (Over the Flags)
0. Display of Zones without needed client mods
0. 4 different types of supported Zone geometries: Cuboid, Sphere, Cylinder and Polygon
0. Integrated support for dynmap

# Dependencies
* [DatabaseHandler](https://github.com/DerTod2/DatabaseHandler) -- `for accessing the databases`
* [ZonesLib](https://github.com/DerTod2/ZonesLib) -- `for handling the Zones`

# Installing
Just grab the jar file and put it into the `plugins` directory. When the Server starts the next time
the config file will be created inside the new `UltimateZones` folder inside the `plugins` folder.
There are also two folders called `worlds` and `locales`. Just configure the `config.yml` and the 
files within the `worlds` folder and restart the server or type the command `/uzone reload`
