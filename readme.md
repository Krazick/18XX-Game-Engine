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

# Code Base Description

The 18XX Game Engine is written in Java. The GUI utilizes the Java Swing UI. It has several data files that are written in XML. There are no actual Tile Images. The data for the Tiles are specified in an XML Structure and the code draws the images when needed.
This code base began in the early 1990's, in C, and then ported to Java to allow multiple platform operation (my development platform has the the Apple Mac, and currently play with my group who all run windows platforms, and my Mac as well. 

# How to Install and Run

# How to Use

# Credits

# License

# Badges

# How to Contribute

Interested contributors should start with expanding the JavaDocs for the various methods, and add to the existing JUNIT Test Cases. This will allow the contributor to get a feel for the Code Base, and add value with no impact to the existing working functionality.

# Tests

Various JUnit Tests have been created to exercise the functionality of the Game Engine. These are by no means complete and exhaustive at this time. 

# Related Projects

Net Game Server allow for the support of the 18XX Game Engine, and the Card Game Manager to connect players together into a common game. The Net Game Server will send the Actions and Effects between the Players to be processed by the Game Engine. 