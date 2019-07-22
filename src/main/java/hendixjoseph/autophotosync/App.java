

package hendixjoseph.autophotosync;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPagedResponse;
import com.google.photos.library.v1.proto.DateFilter;
import com.google.photos.library.v1.proto.Filters;
import com.google.photos.types.proto.DateRange;
import com.google.photos.types.proto.MediaItem;
import com.google.type.Date;

public class App {	
	
	private static final List<String> REQUIRED_SCOPES =
		      ImmutableList.of("https://www.googleapis.com/auth/photoslibrary.readonly");
	
	public static void main(String... args) throws IOException, GeneralSecurityException {
		// Set up the Photos Library Client that interacts with the API
		PhotosLibrarySettings settings =
		     PhotosLibrarySettings.newBuilder()
		    .setCredentialsProvider(
		        FixedCredentialsProvider.create(CredentialBuilder.getCredentials("C:\\Users\\hendr\\Downloads\\client_secret_778040209018-tnhdl3a1gvvcjuehvqd28ksr7mq3le3c.apps.googleusercontent.com.json", REQUIRED_SCOPES)))
		    .build();

		try (PhotosLibraryClient photosLibraryClient =
		    PhotosLibraryClient.initialize(settings)) {
			Date start = Date.newBuilder().setMonth(7).setDay(10).setYear(2019).build();
			Date end = Date.newBuilder().setMonth(7).setDay(21).setYear(2019).build();
			
			DateRange dateRange = DateRange.newBuilder().setStartDate(start).setEndDate(end).build();
			DateFilter dateFilter = Filters.newBuilder().getDateFilterBuilder().addRanges(dateRange).build();
			Filters filters = Filters.newBuilder().setDateFilter(dateFilter).build();
			
			SearchMediaItemsPagedResponse response = photosLibraryClient.searchMediaItems(filters);
			
			for (MediaItem item : response.iterateAll()) {
				String mimeType = item.getMimeType();
				String fileName = item.getFilename();
				String baseUrl = item.getBaseUrl();
				
				if (mimeType.startsWith("image")) {
					baseUrl = baseUrl + "=w2048-h1024";
				} else if (mimeType.startsWith("video")) {
					baseUrl = baseUrl + "=dv";
				} else {
					continue;
				}
				
				URL url = new URL(baseUrl);
				ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream("files/" + fileName);
				FileChannel fileChannel = fileOutputStream.getChannel();
				fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				fileOutputStream.close();
			}

		} catch (ApiException e) {
		    e.printStackTrace();
		}
	}
}