package hendixjoseph.autophotosync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

import javax.swing.JFileChooser;

public class ApsProperties {

	private final Properties props = new Properties();
	private final File propertyFile = new File("autophotosync.properties");
	
	public ApsProperties() throws FileNotFoundException, IOException {
		if (propertyFile.exists()) {
			props.load(new FileInputStream(propertyFile));
		} else {
			props.setProperty("path", selectDirectory());
			props.setProperty("lastDate", "2019-7-10");
		}
	}
	
	public void updateFile() throws IOException {
		props.store(new FileWriter(propertyFile), "AutoPhotoSync Properties");
	}
	
	public void updateDate() {
		LocalDate today = LocalDate.now();
		
		int month = today.getMonthValue();
		int day = today.getDayOfMonth();
		int year = today.getYear();
		
		String todayString = year + "-" + month + "-" + day;
		
		props.setProperty("lastDate", todayString);
	}
	
	public String getLastDate() {
		return props.getProperty("lastDate");
	}
	
	public String getPath() {
		return props.getProperty("path");
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
