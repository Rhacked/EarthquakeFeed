EarthquakeFeed
==============

Shows the biggest earthquakes around the world and tweets associated with the location of the earthquakes

How the app works
-----------------
First of all the app checks if it has network connection. 

0. **If it does have network connection** it retrives an earthquake JSON feed from [USGS Earthquake Hazard Program](http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson), parses the result from the feed and creates a list of 20 earthquakes sorted by the magnitude of the quakes. After creating the list of earthquakes it iterates through all the earthquakes in the list and retrives tweets from the location of the earthquake. Both the JSON feed from the earthquakes and the tweets are stored on files for use in offline mode.
1. **If it does not have network connection** it will try to retrive the JSON from file and creates the list of earthquakes that were retrieved last time it was connected to a network.

When clicking on an earthquake from the list the app will display a new screen with a map of the location of the earthquake and tweet texts from that location. The map **does not** work in offline mode, but the tweets displayed will be up to date with the last time the app had a network connection.

Personal reflections
--------------------
This is my first experience with JSON and using Twitter and Google Map within an app, and I have to say I've learned alot through this experience.

