# AutoPhotoSync for Google Photos

AutoPhotoSync is a desktop Java application. It runs in the system tray (known as the Notification Area in Windows) with a simplistic GUI.

When first run, it will ask where you want to save your photos. Then it will run its first syncronizing task - syncing all images in your Google Photo account since July 10, 2019. It will then sync new photos every 24 hours.

If somehow AutoPhotoSync closes, it will syncronize once the program is run again, and then sync again every 24 hours.

Syncing can be forced by selecting the Sync Now option in the right-click menu.

It uses the [Java Preferences API](https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html) to store user preferences. Currently, this includes only the download path, last sync date, and last sync time.

It also uses the [Java System Tray API](https://docs.oracle.com/javase/tutorial/uiswing/misc/systemtray.html) to run in the system tray.

Finally, it uses a [ScheduledThreadPoolExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html) to run scheduled tasks.
