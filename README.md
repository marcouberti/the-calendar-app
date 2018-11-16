# The calendar

Manage the schedules of a set of **caregivers** in a hospital including an **auto fill** feature. The auto-fill feature has the following  constraints:

- 100 caregivers and 10 rooms
- 8h working day from 9:00 AM to 17:00 PM
- max 5h/week per caregiver ( max 1h/week of overtime)
- if multiple caregivers are eligible follow this priority scale: no overtime, working the same day, nearest room/hour, worked less hours in the past 4 weeks 

# The project

This is a native Android app developed in **Java**. The app runs in **both portrait and landscape** screen orientations. The app runs on devices with Android 4.0 and up (API level 14).

## Setup instructions

- Clone the Git repo from [here](https://github.com/marcouberti?tab=repositories) or alternatively download the project as .zip file and extract on your machine.
- Open the project with Android Studio (version 3.2.1+ suggested) choosing "Open Existing Project..." and selecting the folder containing the *versions.gradle* file
- Synch Gradle
- Make Project
- All the required dependencies should be downloaded and installed automatically by Gradle and Android Studio
- Run the app on a real device or emulator

# Architecture

The project uses a **MVVM** architecture with the help of the new  **Android Architecture Components**. 

Each View (Activity/Fragment) has a **ViewModel** that exposes observable data through **LiveData** objects. ViewModels are *lifecycle aware* so we can have more robust and maintainable apps without all the well known Android lifecycle issues. 

LiveDatas are linked directly with DAO queries so that **the app reacts immediately and autonomously to database changes**.

Each ViewModel use the **Repositories** to access remote and/or local data. Depending on the specific use case each repository has **Dao** and/or **WebService** objects.

>For this small project there is no need to add additional layers, but maybe the case as soon the complexity will grow.

In order to have readable and maintainable code I also attached a **Routers** to each View so that you can immediately understand the app navigation flows starting from each View without deep dive into the logic code.

## Database

I used the **Room Persistent Library** to read and write data to the database. I used only two entities for this project: *CaregiverEntity* and *SlotEntity*. For join query and to avoid multiple db calls I also created a pojo *SlotCaregiver* to group slot and caregiver interesting data at once. 

## WorkManager and Workers

Background long tasks are executed using Workers and the new WorkManager. WorkManager can run workers sequentially or in parallel and with user defined constraints, so it is very powerful. 

In this project I created two workers that run sequentially during the auto fill feature:

- *FetchAllCaregivers* worker
- *AutoFill* worker

The first one is needed because you cannot auto fill the calendar without before having downloaded all the 100 caregivers. If the database is already filled this worker skips.

The last one is the auto fill logic with all the db read and write operations.

>Since we are using LiveData there is no need to explicitly update the UI when the slots are filled.

## Executors

To avoid the effect of task starvation I used a **global application thread pool**. In this way it is easy to run task on the *main* thread, *diskIO* thread or *network* thread.									

## HTTP Client

I used Retrofit 2 as the HTTP client because it is easy to setup.

## Additional implementations

- *App shortcuts*. If running on Android N+ if you long tap on the app icon a shortcut appears. You can also pin this shortcut to your home page to a direct access to the calendar.

# Testing

You can run local unit tests and UI/Instrumentation tests directly from Android Studio or from the command line.

## Unit Test

To run local unit tests from the command line, go to the project root directory and run this command:

> ./gradlew test

HTML test result files: `./app/build/reports/tests/` directory.

## UI/Instrumentation Tests

To run UI/Instrumentation tests from the command line, go to the project root directory and run this command:

> ./gradlew connectedAndroidTest

HTML test result files:`./app/build/outputs/reports/androidTests/connected/`directory.

# Further Improvements

- Kotlin porting
- Improve calendar RecyclerView performance
- Implement all the unit and UI tests required by a production app
- Dependency Injection with Dagger 2 (for this little assignment it is too much overhead)
- Add more architecture layers if the project complexity grows
- Improve remote data loading with some ResourceState handler

# Links

  **[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)**, on the Android Developers website
  **[Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)**, on the Android Developers website
 **[Android Testing](https://developer.android.com/tools/testing/index.html)**, on the Android Developers website
  **[Android Testing Support Library](https://google.github.io/android-testing-support-library/)**, on the ATSL website
   **[Retrofit](https://square.github.io/retrofit/)**, HTTP client library
 **[Mockito](http://mockito.org/)**, mocking framework for unit tests in Java
