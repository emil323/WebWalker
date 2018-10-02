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

None of the obstacles or location nodes (vertices) are hardcoded, they are found in the xml files in the root directory for now. 

### Adding vertices

I suggest using the tool included, this is what is looks like

![Image of tool](https://raw.githubusercontent.com/emil323/WebWalker/master/vertice_generator.PNG)

1. To create a vertex, first click "Select vertex", the nearest vertex will then be selected
2. Walk to the desired location for new vertex, then click "Create vertex". It will now be visible on minimap and on ground.
3. If you need to join two vertices together, simply walk to the vertex, and click "Join vertex". 
4. Click "Save to XML", and the changes will be saved.

You can now test them out using the "Calculate path" button. 

### Here are some examples of obstacles, if you wish to add your own. 

There are currently 5 types of obstacles you can add

* door
* gate
* trapdoor
* staircase
* universal
* security_stronghold_gate

### A simple gate

```xml
  <!-- gate to cows behind crafting guild -->
  <obstacle name="grafting_guild_cows_gate" type="gate" object_id="1558">
      <triggers>
          <vertex id="falador237" goal_id="falador267"/>
          <vertex id="falador267" goal_id="falador237"/>
      </triggers>
  </obstacle>
```

### A shortcut with agility requirements

```xml
        <obstacle name="edgeville_obstacle_pipe" type="universal" object_id="16511" action="Squeeze-through" object_name="Obstacle pipe">
            <requirement type="skill" skill_id="16" required_level="51"/>
            <triggers>
                <vertex id="edgeville523" goal_id="edgeville524"/>
                <vertex id="edgeville524" goal_id="edgeville523"/>
            </triggers>
        </obstacle>
```

### A gate with custom bounds (to make clicking more accurate)

```xml
  <!-- Gate to farm with chickens in lumbridge -->
  <obstacle name="lumbridge_farm_1_gate" type="gate" object_id="1558" bounds="69, 162, -109, -13, -109, 124">
      <triggers>
          <vertex id="lum521" goal_id="lum165"/>
          <vertex id="lum165" goal_id="lum521"/>
      </triggers>
  </obstacle>
```

### You can add as many trigger vertices as you like

```xml
  <!-- Alkharid castle doors-->
  <obstacle name="alkharid_castle_doors" type="door" object_id="1511" bounds="45, 164, -168, 17, -22, 274">
      <triggers>
          <vertex id="alkharid492" goal_id="alkharid493"/>
          <vertex id="alkharid493" goal_id="alkharid492"/>
          <vertex id="alkharid490" goal_id="alkharid491"/>
          <vertex id="alkharid491" goal_id="alkharid490"/>
          <vertex id="alkharid488" goal_id="alkharid489"/>
          <vertex id="alkharid489" goal_id="alkharid488"/>
      </triggers>
  </obstacle>
```

