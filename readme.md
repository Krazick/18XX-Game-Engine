# Project Description

The 18XX Game Engine allows the players to play the 18XX Game (current 1830 is fully playable). Other 18XX Games are in various stages of implementation that includes:

* 1830 -- Avalon Hill initial Version -- Fully Playable
* 1835 -- Germany
* 1853 -- India
* 1856 -- Upper Canada
* 1870 -- Mississippi Valley
* 1830+ -- Mayfair's Extended 1830

The 18XX Game Engine can be used by itself, to play the game in hot-seat mode (single Computer). Or if used in conjunction with the NetGameServer Project, and multiple platforms, each Player can run the same Game Engine and play in the same game in real-time. The NetGameServer coordinates the players into a common Game, and sends the game Actions and Effects between the players.

# Table of Contents
**[Code Base Description](#Description)**

**[How to Install and Run](#Install)**

**[How to Use](#Use)**

**[Credits](#Credits)**

**[License](#License)**

**[Badges](#Badges)**

**[Contribute](#Contribute)**

**[Tests](#Tests)**

**[Related Projects](#Related)**


# Code Base Description
<div id="Description>

The 18XX Game Engine is written in Java. The GUI utilizes the Java Swing UI. It has several data files that are written in XML. There are no actual Tile Images. The data for the Tiles are specified in an XML Structure and the code draws the images when needed.
This code base began in the early 1990's, in C, and then ported to Java to allow multiple platform operation (my development platform has the the Apple Mac, and currently play with my group who all run windows platforms, and my Mac as well. 

# How to Install and Run
<div id="Install">

To actually download the deployment package, there are a few requirements:
1. Have Java JRE Version 1.8 or later installed in your platform
2. Setup a directory on your platform, named "18XX_Games" (or equivalent). This directory is where you install the components necessary, and the Game Engine will create 'autoSaves' directory.
3. Retrieve the get18xx.jar File that is a runnable JAR File that uses Java 1.8 (or later). Place this into the directory created above.
4. Retrieve the '18XX XML Data.zip' Folder, that contains a directory structure of the XML Data that is used locally.
5. Unzip the '18XX XML Data.zip' Folder into the '18XX_Games' directory created above. This should create a folder '18XX XML Data' in which all of the XML Files are placed.
6. Optionally, create another folder 'SaveGames' to store games in progress with any desired name.
7. If using a Mac, you need two additional files
    7.1 Retrieve 'launch_ge18xx.command' and place into the 18XX_Games folder
    7.2 Retrieve 'runner.app' and place into the 18XX_Games folder
    7.3 To Launch (as shown below), double-click the 'launch_ge18xx.command' script rather than the '18xxxx.jar' file directly
8. Perform Test Launch
    8.1 Launch with just a double-click of the 'ge18xx.jar' file, a '18XX Game Engine' Frame should be displayed.
    8.2 Enter your name in the 'Client User Name' field
    8.3 Click the OK Button.
    8.4 A new 'Enter Player Information' Frame is shown. If there is no information shown under 'Players Entered: 1', revisit Steps 4 & 5 above. Or Verify

# How to Use
<div id="Use">

# Credits
<div id="Credits">

# License
<div id="License">

# Badges
<div id="Badges">

# How to Contribute
<div id="Contribute">

Interested contributors should start with expanding the JavaDocs for the various methods, and add to the existing JUNIT Test Cases. This will allow the contributor to get a feel for the Code Base, and add value with no impact to the existing working functionality.

# Tests
<div id="Tests">

Various JUnit Tests have been created to exercise the functionality of the Game Engine. These are by no means complete and exhaustive at this time. 

# Related Projects
<div id="Related">

Net Game Server allow for the support of the 18XX Game Engine, and the Card Game Manager to connect players together into a common game. The Net Game Server will send the Actions and Effects between the Players to be processed by the Game Engine. 