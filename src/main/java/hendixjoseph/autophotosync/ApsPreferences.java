package hendixjoseph.autophotosync;

import java.io.File;
import java.time.LocalDate;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

public class ApsPreferences {

	private final static String PATH = "path";
	private final static String LAST_DATE = "lastDate";

	private final Preferences prefs = Preferences.userRoot().node("hendrixjoseph/autophotosync");


	public ApsPreferences() {
		if (prefs.get(PATH, null) == null) {
			prefs.put(PATH, selectDirectory());
		}
	}

	public void updateDate() {
		LocalDate today = LocalDate.now();

		int month = today.getMonthValue();
		int day = today.getDayOfMonth();
		int year = today.getYear();

		String todayString = year + "-" + month + "-" + day;

		prefs.put(LAST_DATE, todayString);
	}

	public String getLastDate() {
		return prefs.get(LAST_DATE, "2019-07-10");
	}

	public String getPath() {
		return prefs.get(PATH, "/") +  File.separator;
	}

	private String selectDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Please select a directory.");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		int option = chooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION) {
			File chosenDirectory = chooser.getSelectedFile();
			return chosenDirectory.getAbsolutePath();
		} else {
			System.exit(0);
			return ""; // we'll never get here
		}
	}
}
