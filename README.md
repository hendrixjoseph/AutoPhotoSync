# AutoPhotoSync for Google Photos

AutoPhotoSync only uses Google's `photoslibrary.readonly`  scope. It does not need, nor should have, scopes for anything other than photos, or write and delete access. If the user consent screen indicates otherwise, do not authorize AutoPhotoSync.

AutoPhotoSync is a native (Java) application that provides local sync or automatic backup of users’ Photo files.

AutoPhotoSync does not collect or submit end-user data, private or otherwise. It only downloads users' photos to a local directory.

It uses the [Java Preferences API](https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html) to store user preferences. Currently, this includes only the download path, last sync date, and last sync time.

It also uses the [Java System Tray API](https://docs.oracle.com/javase/tutorial/uiswing/misc/systemtray.html) to run in the system tray.

Finally, it uses a [ScheduledThreadPoolExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html) to run scheduled tasks.
