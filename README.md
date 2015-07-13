EarthquakeFeed
==============

Shows the biggest earthquakes around the world and tweets associated with the location of the earthquakes

How the app works
-----------------
First of all the app checks if it has network connection. 

0. **If it does have network connection** it retrives an earthquake JSON feed from [USGS Earthquake Hazard Program](http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson), parses the result from the feed and creates a list of 20 earthquakes sorted by the magnitude of the quakes. After creating the list of earthquakes it iterates through all the earthquakes in the list and retrives tweets from the location of the earthquake (1000km radius at the moment). Both the JSON feed from the earthquakes and the tweets are stored on files for use in offline mode.
1. **If it does not have network connection** it will try to retrive the JSON from file and creates the list of earthquakes that were retrieved last time it was connected to a network.

When clicking on an earthquake from the list the app will display a new screen with a map of the location of the earthquake and tweet texts from that location. In offline mode the tweets displayed will be up to date with the last time the app had a network connection.

**The application will not work by cloning this project. I've removed all authorization keys to both the Twitter API and the Google Maps API**

Further development
-------------------
Some ideas for further development
0. Allowing the user to choose the radius of the twitter search
1. Displaying more info about tweets i.e. author, number of retweets
2. Allowing the user to add filters to twitter searches i.e. language filters, most retweeted or most liked


