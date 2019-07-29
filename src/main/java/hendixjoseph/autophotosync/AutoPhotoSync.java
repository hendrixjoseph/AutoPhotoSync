package hendixjoseph.autophotosync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPagedResponse;
import com.google.photos.library.v1.proto.DateFilter;
import com.google.photos.library.v1.proto.Filters;
import com.google.photos.types.proto.DateRange;
import com.google.photos.types.proto.MediaItem;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.type.Date;

public class AutoPhotoSync {

	private static final String JSON_FILE = "client_secret_778040209018-tnhdl3a1gvvcjuehvqd28ksr7mq3le3c.apps.googleusercontent.com.json";
	private static final List<String> REQUIRED_SCOPES =
			ImmutableList.of("https://www.googleapis.com/auth/photoslibrary.readonly");

	private final PhotosLibraryClient photosLibraryClient;
	private final PhotosLibrarySettings settings;
	private final ApsPreferences prefs = new ApsPreferences();

	public AutoPhotoSync() throws IOException, GeneralSecurityException {
		InputStream credentialsStream = AutoPhotoSync.class.getResourceAsStream(JSON_FILE);

		settings = PhotosLibrarySettings.newBuilder()
				.setCredentialsProvider(
						FixedCredentialsProvider.create(CredentialBuilder.getCredentials(credentialsStream, REQUIRED_SCOPES)))
				.build();

		photosLibraryClient = PhotosLibraryClient.initialize(settings);
	}

	public int sync() throws IOException {
		Date start = getDate(prefs.getLastDate());
		Date end = getToday();
		int count = 0;

		Filters filters = getDateRangeFilter(start, end);

		SearchMediaItemsPagedResponse response = photosLibraryClient.searchMediaItems(filters);

		for (MediaItem item : response.iterateAll()) {
			String mimeType = item.getMimeType();
			String baseUrl = item.getBaseUrl();
			String filename = getFullDownloadPath(item);

			if (!fileExists(filename)) {
				if (mimeType.startsWith("image")) {
					baseUrl = baseUrl + "=w2048-h1024";
				} else if (mimeType.startsWith("video")) {
					baseUrl = baseUrl + "=dv";
				} else {
					continue;
				}

				write(baseUrl, filename);
				count++;
			}
		}

		return count;
	}

	public void disconnect() {
		photosLibraryClient.close();
	}

	public void updateDate() {
		prefs.updateDate();
	}

	public String getLastTime() {
		return prefs.getLastTime();
	}

	private String getFullDownloadPath(MediaItem item) {
		String filename;

		if (item.hasMediaMetadata() && item.getMediaMetadata().hasCreationTime()) {
			Timestamp timestamp = item.getMediaMetadata().getCreationTime();
			String year = Timestamps.toString(timestamp).substring(0, 4);

			File newDir = new File(prefs.getPath() + year);

			if (!newDir.exists()) {
				newDir.mkdir();
			}

			filename = prefs.getPath() + year + File.separator + item.getFilename();
		} else {
			filename = prefs.getPath() + item.getFilename();
		}

		return filename;
	}

	private Filters getDateRangeFilter(Date start, Date end) {
		DateRange dateRange = DateRange.newBuilder().setStartDate(start).setEndDate(end).build();
		DateFilter dateFilter = Filters.newBuilder().getDateFilterBuilder().addRanges(dateRange).build();
		return Filters.newBuilder().setDateFilter(dateFilter).build();
	}

	private boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists();
	}

	private void write(String baseUrl, String filename) throws IOException {
		URL url = new URL(baseUrl);
		ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		FileChannel fileChannel = fileOutputStream.getChannel();
		fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		fileOutputStream.close();
	}

	private Date getDate(int month, int day, int year) {
		return Date.newBuilder().setMonth(month).setDay(day).setYear(year).build();
	}

	private Date getDate(String dateString) {
		String[] tokens = dateString.split("-");

		if (tokens.length >= 3) {
			int year = Integer.parseInt(tokens[0]);
			int month = Integer.parseInt(tokens[1]);
			int day = Integer.parseInt(tokens[2]);

			return getDate(month, day, year);
		} else {
			throw new IllegalArgumentException("Date string format invalid. Should be of form YYYY-MM-DD. String looks like: " + dateString);
		}
	}

	private Date getToday() {
		LocalDate today = LocalDate.now();

		int month = today.getMonthValue();
		int day = today.getDayOfMonth();
		int year = today.getYear();

		return getDate(month, day, year);
	}
}