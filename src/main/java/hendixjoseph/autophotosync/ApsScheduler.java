package hendixjoseph.autophotosync;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApsScheduler {

	private final AutoPhotoSync autoPhotoSync;
	private final ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(0);

	public ApsScheduler(AutoPhotoSync autoPhotoSync) {
		this.autoPhotoSync = autoPhotoSync;
	}

	public void start() {
		timer.scheduleAtFixedRate(() -> {}, 0, 1, TimeUnit.DAYS);
	}

	public void cancel() {
		timer.shutdown();
	}
}
