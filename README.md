# CConfs (17Spring v3)

## User Tutorial

This tutorial demonstrates how to use the conference app from user's perspective.

#### Preload app data:

![](http://i.giphy.com/26xByDi4A7aPyzl8k.gif) 

### Import/Export Backend Date

Note: only users with admin priviledge are allowed to use this feature. To add your account to the admin list: append your app account email to the "admin_mail_address" string array in this [file](https://github.com/zhexinq/conference/blob/master/app/src/main/res/values/strings.xml).

#### Import backend data: 

In order to reload the backend data using the latest conference information, one needs to click on the transfer icon in the app dashboard. You will be prompted to enter Google account credentials if not loginned on the device already. After logined as Google user, you can pick __Paper.csv__ and __Session.csv__ uploaded previously to your drive in order to process & load them into app backend.

![](http://i.giphy.com/26xBGAOiE92Em2Gt2.gif)

#### Export backend data: 

As a reverse step you can also reconstruct the correctly formatted .csv files by reading the imported backend data by using the export paper or export session service. The app will automatically detect your drive account and you can rename or store the files in any path you would like.

![](http://i.giphy.com/26xBATAqWxWqVqi0U.gif) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![](http://i.giphy.com/l3q2GLdI8LLeAsrIc.gif)

### Main side menu

There is main drawer menu in the app's dashboard view that have several sub features to use

#### My Profile: 

User can click "my profile" section to view their profie details. In this view, you can do things like: edit profile, custom setting, send notes/images, and view appintments.

Edit Profile:

![](http://i.giphy.com/l3q2BtRV4erZJ7Oes.gif) ![](http://i.giphy.com/26xBwjH1E6vBdUoXC.gif) ![](http://i.giphy.com/l3q2zLpofAGXatB6w.gif) ![](http://i.giphy.com/l3q2UhfRXqgr3HG3C.gif)

Custom setting: 

User can change their settings like whether to share their profile or sync their schedules to the local calendar.

![](http://i.giphy.com/26xBETRwhXTnr07bG.gif)

Send notes/images:

![](http://i.giphy.com/26xBHs925iMLfd8Gc.gif)

#### Todo List:

There is also a todo list feature that is bound to the user account. User can add/delete items in their to-do list and sync with the cloud.

![](http://i.giphy.com/26xBzw2v0AVZFI7te.gif)

#### Login/Registration:

User can login either using the option in the main side drawer or the setting module.

![](http://i.giphy.com/l3q2OlpGVrWkL5pKM.gif)

#### Sync Cloud App Data:

User can refresh app local data from the cloud to sync with the latest changes.

![](http://i.giphy.com/l3q2EnJGmlNApkKBy.gif)

### Author Session

There is also an author session in the app main dashboard that includes all authors whose papers will be presented in the conference. User can load more authors by simply scrolling down. It also supports custom search so you can use the toolbar on top to search any authors that you are familiar with.

By clicking on an author name, user can view all the papers asscoiated with that author. By clicking on a particular paper, user can view the paper detail as well as the information of the session that is presenting that paper.

![](http://i.giphy.com/26xBOeoeuQJGjrXIk.gif) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![](http://i.giphy.com/l3q2sBRbnkohQVAxq.gif)

### Social Networking

User can also interact with conference attenders by using the Network session in the app main dashboard. By clicking on the Network session, you can view a list of profiles belonged to users who want to share their profile in the app network. The list supports instant search and name indexing. You can choose a single profile to view the user's profile details. Under a certain user's profile, you can also do things like: make an appointment or send messages. The appointment request or messages will be passed to the other user in the form of notifications. Users can also receive appointment requests or messages.

![](http://i.giphy.com/26xBFgzhKCIvoTRdu.gif) ![](http://i.giphy.com/26xBL8xFrVyrhR2Vy.gif) ![](http://i.giphy.com/26xBJygf38qd6LcoU.gif) 

![](http://i.giphy.com/l3q2UsYm9yCX1E6Ry.gif) ![](http://i.giphy.com/l3q2sdmXLSejuq2w8.gif) ![](http://i.giphy.com/26xBKCZPvvGHku0JG.gif)

### Places Around Me

User can also explore the places around them by clicking on the Nearby session in app's main dashboard. The Nearby session supports features like: pick a place in map vidw, see categorized places, and search around places using keyword. When user clicks on a particular place. The place detail will be presented: place image, name, phone number, location, etc.

![](http://i.giphy.com/26xBSCNcNwwl1KdEs.gif) ![](http://i.giphy.com/l3q2L2ppFHeRevN3W.gif) ![](http://i.giphy.com/l3q2E28NgGNg0pZYc.gif)

### Floor Guide
User can also view the floor plans of the conference center by clicking on the Floor Guide session in app's main dashboard. 

![](https://s3.amazonaws.com/jennyliu/floorguide.gif) ![](https://s3.amazonaws.com/jennyliu/floorguide.gif) ![](https://s3.amazonaws.com/jennyliu/floorguide.gif)

## App installation

The installation of the app is easy:

1. Clone the project [repository](https://github.com/zhexinq/conference-v2) (e.g. in comand line type "git clone https://github.com/zhexinq/conference-v2.git") into your local directory: ![](https://s24.postimg.org/fewlmngyd/checkout_project.jpg)

2. Open the project using Android by choosing open existing project and find the location where you downloaded the repository: ![](https://s30.postimg.org/ak5il1wpd/open_project.jpg) ![](https://s30.postimg.org/ligs38lap/open_project_2.jpg)

3. Compile the code and run it either on an emulator or a physical device (using a USB cable to connect with the computer). After successul compilation the app will be installed on the device. You can read more [here](https://developer.android.com/training/basics/firstapp/running-app.html) about running Android project on device: ![](https://s29.postimg.org/jtbeu0mjb/compile_and_run.jpg)

4. Our app use several Google Play Serivces so you would need to install the it before being able to use some app features. Fortuanately, the app will automatically detect whether Google Play Service is available and if not it will direct you to a website to download the services during the first time installation.








