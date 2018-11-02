# WebWalker for RSBot 

by Emil

#### WebWalker is a project for use in RSBot scripts to make traversal of the RuneScape world easy. WebWalker will walk anywhere in RuneScape, and will open up doors, gates and climb ladders and staircases. 

A quick video showing WebWalker in action: https://www.youtube.com/watch?v=Q6hZ6igrRCs

![Showcase](https://github.com/emil323/WebWalker/blob/master/showcase.PNG?raw=true)

### Setup 
To compile this project in Intellj and launch RSbot, use this guide: https://www.powerbot.org/community/topic/1273162-rsbot-v7-debug-instructions/

A Powerbot account is required.

### How to use

Two scripts are included in this project:

* An example script "SuperFighter" is added to showcase the functionality of the project, modify "food_id" and "monster_name" in SuperFighter.java, and it will fight and bank for food at closest bank.
* Start the script "Walkweb", and a simple GUI will appear for generating your own vertices. click "save to XML", and it will be added to the graph.xml file. You can also test walking by entering a vertex  (see graph.xml file), and click "calculate path".The bot wil find the quickest path.
See more below

### The project is far from done, but fully functional. Here are some of the areas included:

* Varrock
* Edgeville (with edgeville dungeon)
* Lumbridge
* Falador (with falador mine)
* Taverley
* Canifis/Slayer tower
* Ardougne
* Yanille
* Catherby
* Gnome Stronghold (and Grand Tree)
* Camelot
* Rellekka Slayer cave
* Rellekka troll cave
* Al-kharid
* Taverley dungeon
* Barbarian outpost
* Baxtorian waterfall, with cave


### Here are some of the notable obstacles included

* Alkharid toll gate (if >10gp in inventory)
* Shantay pass (if pass is present in inventory)
* Stronghold of security (all 4 levels, answers questions at door)
* Gate to Taverley
* Trapdoor to thieving guild in burthrope
* Gates in lumbridge farms/cows/chickens
* Entrance to Morytania
* Falador crumbeling wall (if high enough agility)
* GE shortcut (if high enough agility)
* Ferry to Karamja
* Baxtorian waterfall (with rope)
* Desert carpet rides 

None of the obstacles or location nodes (vertices) are hardcoded, they are found in the xml files in the data directory.

### Adding vertices

I suggest using the tool included, this is what is looks like

![Image of tool](https://raw.githubusercontent.com/emil323/WebWalker/master/vertice_generator.PNG)

1. To create a vertex, first click "Select vertex", the nearest vertex will then be selected
2. Walk to the desired location for new vertex, then click "Create vertex". It will now be visible on minimap and on ground.
3. If you need to join two vertices together, simply walk to the vertex, and click "Join vertex". 
4. Click "Save to XML", and the changes will be saved.

You can now test them out using the "Calculate path" button. 

### Obstacle handling

The obstacle XML files resides in the data folder, you can create your own obstacle files. It is important that the filename ends with ".obstacles.xml" to be loaded. 

A obstacle XML files consist of the root tag <obstacles>, the each obstacle has an <obstacle> node.

An obstacle has the always required attributes name and type. Name is unique and descriptive, and it's purpose is to make debugging easier if something is incorrectly loaded, or a bot is stuck at any obstacle.

A obstacle type attribute, has there alternatives:

*object
*npc
*item_selection
*widget

These are made as general as possible, to tackle a wide range of obstacles in the game. These types require additional attributes to function. 



### Object type example

```xml
<obstacle name="draynor_manor_main_entrance" 
          type="object" 
          object_id="1201" <!-- REQUIRED multiple integers allowed seperated by "|" -->
          action="Open" <!--REQUIRED multiple integers allowed seperated by "|" -->
          object_name="Door" <!--REQUIRED multiple strings allowed seperated by "|" -->
          bounds="-32, 128, -224, 17, -29, 20" <!-- OPTIONAL to make interaction more accurate -->
          dialogue="draynor_manor_dialogue" <!-- OPTIONAL reference to dialogue (example: Security stronghold doors) -->
</obstacle>          
```

### NPC type example

```xml
<obstacle name="ferry_to_karamja" 
          type="npc" 
          npc_id="3648" <!-- REQUIRED multiple integers allowed seperated by "|" -->
          action="Pay-fare" <!--REQUIRED multiple integers allowed seperated by "|" -->
          npc_name="Customs officer" <!--REQUIRED multiple strings allowed seperated by "|" -->
          bounds="45, 100, 45, -45, -100, 100" <!-- OPTIONAL to make interaction more accurate -->
          dialogue="karmja_ferry_dialogue" <!-- OPTIONAL reference to dialogue -->
</obstacle>          
```
### Item selection type example



```xml
<obstacle name="camelot_teleport" 
          type="item_selection" 
          item_id="8010" <!-- REQUIRED multiple integers allowed seperated by "|" -->
</obstacle>          
```


