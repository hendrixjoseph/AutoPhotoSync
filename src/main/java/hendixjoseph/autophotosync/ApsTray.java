package hendixjoseph.autophotosync;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

public class ApsTray {
	
	public static void main(String... args) throws BackingStoreException {		
		try {
			new ApsTray();
		} catch (IOException | AWTException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	private final AutoPhotoSync autoPhotoSync = new AutoPhotoSync();
	private final PopupMenu menu = new PopupMenu();
	private final TrayIcon trayIcon;
	
	public ApsTray() throws AWTException, GeneralSecurityException, IOException {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			
			addMenuItem("Sync Now", (e) -> sync());
			addMenuItem("Close", (e) -> close());
			
			Image image = ImageIO.read(new File("bulb.gif"));	
			
			trayIcon = new TrayIcon(image, "AutoPhotoSync", menu);
			tray.add(trayIcon);
		} else {
			trayIcon = null; // Maybe throw exception instead?
		}
	}
	
	private void addMenuItem(String label, ActionListener listener) {
		MenuItem item = new MenuItem(label);
		item.addActionListener(listener);
		menu.add(item);
	}
	
	private void sync() {		
		autoPhotoSync.sync();
		autoPhotoSync.updateDate();
		trayIcon.displayMessage("AutoPhotoSync", "Sync complete", MessageType.INFO);
	}
	
	private void close() {
		autoPhotoSync.disconnect();
		System.exit(0);
	}
}
