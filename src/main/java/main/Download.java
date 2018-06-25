package main;

import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Observable;

class Download  {
	private static final int MAX_BUFFER_SIZE = 1024;
	
	public static final String STATUSES[] = { "Downloading", "Paused", "Complete", "Cancelled",
	"Error" };

	private static DecimalFormat d = new DecimalFormat("##.##");
	public static final int DOWNLOADING = 0;

	public static final int PAUSED = 1;

	public static final int COMPLETE = 2;

	public static final int CANCELLED = 3;

	public static final int ERROR = 4;

	private URL url; // download URL

	private long size; // size of download in bytes

	private long downloaded; // number of bytes downloaded

	private int status; // current status of download

	private String progress;

	private Thread thread;

	private String fileName;

	// Constructor for Download.
	public Download(URL url, String fileName) {
		this.url = url;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		this.fileName = fileName;
	}

	// Get this download's URL.
	public String getUrl() {
		return url.toString();
	}

	// Get this download's size.
	public long getSize() {
		return size;
	}

	// Get this download's progress.
	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	public int getStatus() {
		return status;
	}

	public void pause() {
		status = PAUSED;
	}

	public void resume() {
		status = DOWNLOADING;
		download();
	}

	public void cancel() {
		status = CANCELLED;
	}

	private void error() {
		status = ERROR;
	}

//	private void download() {
//		thread = new Thread(this);
//		thread.start();
//	}

	public Thread getThread() {
		return thread;
	}

	// Download file.
	public void download() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

			// Connect to server.
			connection.connect();

			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2) {
				error();
			}

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}

			/*
			 * Set the size for this download if it hasn't been already set.
			 */
			if (size == -1) {
				size = contentLength;
			}
			File f = new File(fileName);
			if(f.length() > 0 ){
				downloaded = f.length();
			}
			// Open file and seek to the end of it.
			file = new RandomAccessFile(fileName, "rw");
			file.seek(downloaded);

			stream = connection.getInputStream();
			while (status == DOWNLOADING) {
				/*
				 * Size buffer according to how much of the file is left to download.
				 */
				if(size == downloaded) status = COMPLETE;
				byte buffer[];
				if (size - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				} else {
					buffer = new byte[(int) (size - downloaded)];
				}

				// Read from server into buffer.
				int read = stream.read(buffer);
				if (read == -1)
					break;

				// Write buffer to file.
				file.write(buffer, 0, read);
				downloaded += read;
				double current = (((double) downloaded / size) * 100);
				String prog =  d.format(current)+" %";
				if(progress == null){
					progress = prog;
//					console.printf("progress %s\n", progress);
					System.out.print("\rProgress: "+progress);
				}else if(!progress.equals(prog)){
					progress = prog;
//					console.printf("progress %s\n", progress);
					System.out.print("\rProgress: "+progress);
				}
			}

			/*
			 * Change status to complete if this point was reached because downloading
			 * has finished.
			 */
			if (status == DOWNLOADING) {
				status = COMPLETE;
			}
		} catch (Exception e) {
			error();
		} finally {
			// Close file.
			if (file != null) {
				try {
					file.close();
				} catch (Exception e) {
				}
			}

			// Close connection to server.
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
