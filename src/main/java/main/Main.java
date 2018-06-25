package main;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
	private static String ZipURL = "http://ubuntu.biz.net.id/18.04/ubuntu-18.04-desktop-amd64.iso";
//	private static String ZipURL = "https://drive.google.com/open?id=0BxCSz4GCxzcLTkhMTGdVazBFSlk";
	private static Download download;
	private static double s;
	private static DecimalFormat df = new DecimalFormat("##.##");
	private static final String API_KEY = "AIzaSyDfhi56WxGUA8UENONrLSD7XH0uyxNInRg";
	private static Map<URL, String> urlMap = new HashMap<URL, String>();
	
	private static void initMap(){
		try {
			urlMap.put(new URL("https://www.googleapis.com/drive/v3/files/1fMEHafyX73J0oEwqy837TSJfQMwyRZPm?alt=media&key="+API_KEY), "Paul, Apostle of Christ.mp4");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		initMap();
		String startDate = "14-06-2018 02:01:10";
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date d = null;
		try {
			d = format.parse(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		while(true){
			Date now = Calendar.getInstance().getTime();
			
			ZonedDateTime n = ZonedDateTime.now();
			ZonedDateTime oldDate = ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
			Duration duration = Duration.between(n,oldDate);
			double as = (double)duration.toMinutes() / 60;
			if(s == 0){
				s = (double)duration.toMinutes() / 60;
				System.out.println("Distance to download:"+df.format(s)+" hour");
			}else if(as < s){
				s = (double)duration.toMinutes() / 60;
				System.out.println("Distance to download:"+df.format(s)+" hour");
			}
			if(now.after(d) && download == null){
				System.out.println("Download start");
				for(Map.Entry<URL, String> entry : urlMap.entrySet()){
					URL url = entry.getKey();
					String fileName = entry.getValue();
					System.out.println("Download "+fileName);
					download = new Download(url, fileName);
					download.download();
				}
				System.out.println("Download finish");
				break;
			}
		}
	}

}
