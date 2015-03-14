# Overview #

## Users ##

Complete Streets is a open-source android application. The app tracks the user's current location with data from the gps satellite and displays it on a map. At a location the user can take a picture or record a piece of audio describing the location. The app keeps tracks of all the special locations where the user took pictures and/or recorded audio.

## Researchers ##

The app was designed for Software Engineering class (CIS 650) at the University of Oregon. The app tracks users in the real world by capturing their saved locations and pictures/audio related to that location. The app also provides a server to get location data from a client app. That can be used by researchers to simulate a virtual world and use the app to track locations in that world.

# Minimum Requirements #

  * Hardware: Android Dev Phone 1
  * OS: Android 1.6 (API Level 4 with Google)

# Installing App #

The app can be download from the [source](http://code.google.com/p/cis650completestreets/source/checkout) tab. If you have the sdk setup with eclipse then you can import that application in eclipse and load it onto your developer phone. Another way of installing the app is following this [link](http://developer.android.com/guide/developing/tools/adb.html#move).

# Using the Program #

  * On load, the app will display the map with a red pin marking the user's current location. At any location, the user can click the Record button and they we see two options, **Take Picture** and **Record Audio**.
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_1.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_1.jpg) ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_2.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_2.jpg)

  * Clicking **Take Picture** will bring up the camera and the user can take a picture by looking through the screen and _clicking the screen_ to take a picture. Clicking **Record Audio** will bring up the record audio page where the user can record audio. To record audio click **Record Audio** and then **Stop recording**. Once done recording the user can click back to return to the map. Once you take a picture you are brought back to the map. The user will now see a purple pin indicating that your current location has been saved. When the user moves again the previously saved location will now have a green pin and your current location will have a red pin.
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_10.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_10.jpg) ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_3.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_3.jpg) ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_5.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_5.jpg)

  * To get instructions, the user can press the menu button on their phone which will shows options for **Instructions** and **Settings**. Clicking the **Instructions** button will bring up a page displaying the current instructions. **Settings** will show options (only for researchers).
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_6.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_6.jpg) ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_7.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_7.jpg)

  * The app also support zooming in and out.
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_11.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_11.jpg)

## Options for Researchers ##

  * Under **Settings**, you have options like turning on the socket server to get data from a client, turning on a predetermined border, clearing the data and pushing the data to the researchers online database. Use the **Sub #** field to fill in a name or number related to the subject that is being researched.
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_8.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_8.jpg) ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_9.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_9.jpg)
  * The app keeps track of all the user's events and puts it to a log that researchers can view live if the phone is plugged in to a computer. Also when you push data to the web a log file containing the log information is generated that goes together with the picture and audio data.
> ![http://pages.uoregon.edu/dmundra/cis650/scr_shot_4.jpg](http://pages.uoregon.edu/dmundra/cis650/scr_shot_4.jpg)

# FAQ #

### Every time I take a picture or record something at a location, I go back to the map but my location has already moved? ###

That behavior is caused by the gps satellite data. Even though it seems that you have not moved the gps data is still trying to get the most accurate data to the phone. This will cause "jitter" behavior moving you to a new location even though you haven't moved.

### Why does it seem that the pin is 5 to 6 meters away from my actual location? ###

We are rounding off gps data to avoid the "jitter" problem specified above. Our rounding off creates a 10mx10m box which specifies you location instead of just a point.