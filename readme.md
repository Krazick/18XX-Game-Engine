# Project Description

The 18XX Game Engine allows the players to play the 18XX Game (current 1830 is fully playable). Other 18XX Games are in various stages of implementation that includes:

* 1830 -- Avalon Hill initial Version -- Fully Playable
* 1835 -- Germany - Begining to flesh out unique rule implementations
* 1853 -- India
* 1856 -- Upper Canada -- Final Bug fixing, Fully Playable
* 1870 -- Mississippi Valley
* 1830+ -- Mayfair's Extended 1830

The 18XX Game Engine can be used by itself, to play the game in hot-seat mode (single Computer). Or if used in conjunction with the NetGameServer Project, and multiple platforms, each Player can run the same Game Engine and play in the same game in real-time. The NetGameServer coordinates the players into a common Game, and sends the game Actions and Effects between the players.

# Table of Contents
*[Code Base Description](#Description)*

*[How to Install and Run](#Install)*

*[How to Use](#Use)*

*[Credits](#Credits)*

*[License](#License)*

*[Badges](#Badges)*

*[Contribute](#Contribute)*

*[Tests](#Tests)*

*[Related Projects](#Related)*

# Code Base Description
<a name="Description"></a>

The 18XX Game Engine is written in Java. The GUI utilizes the Java Swing UI. There are several data files that are written in XML. These data files are stored and used from the Krazick.github.io Repository. There are no actual Tile Images. The data for the Tiles are specified in an XML Structure and the code draws the images when needed.
This code base began in the early 1990's, in C, and then ported to Java to allow multiple platform operation (my development platform is the the Apple Macintosh, and currently play with my group who all run Windows platforms, and my Mac as well. The code base does follow specific formatting conventions (that are not necessarily the "java standard". The objects and their associated fields, and internal method variable have specific naming guidelines.


# How to Install and Run
<a name="Install"></a>

To actually download the deployment package, there are a few requirements:

1. Have Java JRE Version 1.8 or later installed in your platform
1. Setup a directory on your platform, named "18XX_Games" (or equivalent). This directory is where you install the components necessary, and the Game Engine will create 'autoSaves' directory.
1. Retrieve the get18xx.jar File that is a runnable JAR File that uses Java 1.8 (or later). Place this into the directory created above.
1. Optionally, create another folder 'SaveGames' to store games in progress with any desired name.
1. If using a Mac, you need two additional files
    1. Retrieve 'launch_ge18xx.command' and place into the 18XX_Games folder
    1. Retrieve 'runner.app' and place into the 18XX_Games folder
    1. To Launch (as shown below), double-click the 'launch_ge18xx.command' script rather than the '18xxxx.jar' file directly
1. Perform Test Launch
    1. Launch with just a double-click of the 'ge18xx.jar' file, a '18XX Game Engine' Frame should be displayed.
    1. Enter your name in the 'Client User Name' field
    1. Click the OK Button.
    1. A new 'Enter Player Information' Frame is shown.  

# How to Use
<a name="Use"></a>

Use of the GE18XX Game Engine does need a Network Connection to retrieve the XML Data Files. The actual use is simply setup and launch as directed above. There is a NetGameServer, from a separate github repository that can be used to support network game play. The NetGameServer repository is currently private. 

The NetGameServer when launched on a platform, listens on port 18300 for connections from the Game Engine. If you get a NetGameServer setup on another platform, you must have it listen to port 18300, and allow any firewall to pass that port through to your server platform.

You can play the GE18XX Game Engine in Hot Seat mode, where all players are together and play on a single computer.

# Credits
<a name="Credits"></a>

# License
<a name="License"></a>
Standard MIT License -- See the License File for details.

# Badges
<a name="Badges"></a>

# How to Contribute
<a name="Contribute"></a>

Interested contributors should start with expanding the JavaDocs for the various methods, and add to the existing JUNIT Test Cases. This will allow the contributor to get a feel for the Code Base, and add value with no impact to the existing working functionality.

# Tests
<a name="Tests"></a>

Various JUnit Tests have been created to exercise the functionality of the Game Engine. These are by no means complete and exhaustive at this time. There is use of Mockito to provide Mock Objects. Attempts have been made to use factories to create the more complex objects to be mocked. These factories have consistent name schemes.


# Related Projects
<a name="Related"></a>

Net Game Server allow for the support of the 18XX Game Engine, and the Card Game Manager to connect players together into a common game. The Net Game Server will send the Actions and Effects between the Players to be processed by the Game Engine. 
