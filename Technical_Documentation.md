# Overview #

Complete Streets is a open-source android application. The app tracks the user's current location with data from the gps satellite and displays it on a map. At a location the user can take a picture or record a piece of audio describing the location. The app keeps tracks of all the special locations where the user took pictures and/or recorded audio.

## Developers ##

Android developers can download the app and use it. We would however like that you still keep our server configuration so that the data you collect will be pushed to our servers and we can use that for our future research.

# Minimum Requirements #

  * Hardware: Android Dev Phone 1
  * OS: Android 1.6 (API Level 4 with Google)

# Installing App #

The app can be download from the [source](http://code.google.com/p/cis650completestreets/source/checkout) tab. If you have the sdk setup with eclipse then you can import that application in eclipse and load it onto your developer phone. Another way of installing the app is following this [link](http://developer.android.com/guide/developing/tools/adb.html#move).

In [maptabview.xml](http://code.google.com/p/cis650completestreets/source/browse/trunk/res/layout/maptabview.xml) and [recordtabview.xml](http://code.google.com/p/cis650completestreets/source/browse/trunk/res/layout/recordtabview.xml) you will need to supply your own Google Map Api key.

# Using the Program #

Please refer to [User documentation](http://code.google.com/p/cis650completestreets/wiki/User_Documentation).

# Classes #

[MapTabView](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/MapTabView.java): Is the starting view for our app. On load you will see a map with a red pin marking you current location and green pins marking saved locations (if any). This view gets all the map information from the database and using [MapOverlay](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/MapOverlay.java) creates overlays that is sets pins for saved locations and for the current location. Once the data is collected and overlays created the view will add them to the map and invalidates the view. In this view we also manage the location manager/listener to get the data from the gps and also the socket listener to get gps data from a client. The private inner class Socket listener (extends [AsyncTask](http://developer.android.com/intl/zh-TW/reference/android/os/AsyncTask.html)) is the server that the Virtual Environment communicates with to fake the phone into thinking it is located in the virtual space. We also handle the menu here that displays instructions and settings.

[TakePictureView](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/TakePictureView.java): In this view we launch the camera to take a picture. Before we take the picture, we save the current location on the map to the db as a [GeoStamp](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/GeoStamp.java). To take a picture you need to click on the screen. Once a picture is taken we save the picture to the sd card and store into the database the path to the picture and [GeoStamp](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/GeoStamp.java) id.

[RecordAudioView](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/RecordAudioView.java): Similar to the camera but in this view the user clicks a button to record and save audio. We then save the audio to the sd card and save the path to the db with the [GeoStamp](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/GeoStamp.java) id.

http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/SettingTabView.java: This view has all the settings that will be used by researchers. In this view we show the option to start the server listener and the port number associated with it. We have the option to select a predetermined border. All the preferences from above are stored to a preference file that all other views load. We also have options to clear all the data and also push it to our servers with a subject name.

[WebPushView](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/WebPushView.java): This view is loaded when the user/researchers wants to send the data to a web server. Currently we have set the server to be our research serve. We would like to leave that way so that we use the data collected by other developers for future research.

[GeoStamp](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/GeoStamp.java): All location information is encapsulated in this [GeoStamp](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/GeoStamp.java) class. In this class we keep track of a locations latitude/longitude and database id.

[Border](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/Border.java): In this class we have defined border information that will be used by researches to regulate where users can go. Right now we preset border information for our research so any additional borders would need to be added to this file.

[IGeoDB](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/db/IGeoDB.java): This is the main interface we use to connect with the database to store and retrieve locations and their associated pictures and audio. The code for our database can be found in [GeoDBConnector](http://code.google.com/p/cis650completestreets/source/browse/trunk/src/edu/uoregon/db/GeoDBConnector.java).

# Open Problems #

## Non UI Thread Problems ##

**Description**: To accommodate the virtual environment we added a server thread to our app. When researchers want to send location data from their virtual environment app to the phone they just turn on the server from the **Settings** page. When the server option is set to true, we create a new thread that waits for location data. When the server thread gets a set of location data it will create the new stamp and update the map on the screen. This is where we ran into a problem. The problem is we create a thread in our app that is a non UI thread and from that thread we try to call UI components like a toast message or the mapview to update the map. This problem was also found when we push data to the web.

Android [MapView](http://code.google.com/android/add-ons/google-apis/reference/com/google/android/maps/MapView.html) supports a non UI thread calling updates on it but other UI components do not have the same support.

**Solution**:

Android provides a simple class called [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) that, (as noted in the docs) "enables proper and easy use of the UI thread. This class allows one to perform background operations and publish results on the UI thread without having to manipulate threads and/or handlers." This class has special methods that are automatically called by the UI thread. These methods can update the UI with data that was generated in a non-UI thread.

For example, in our socket server, we [publishProgress](http://developer.android.com/reference/android/os/AsyncTask.html#publishProgress(Progress...)) every time we received data from the VE. The UI thread then calls [onProgressUpdate](http://developer.android.com/reference/android/os/AsyncTask.html#onProgressUpdate(Progress...)) and processes the data (by updating the map).