package io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NetworkDownloader {

	public static void main(String[] args) {
//		String BASE = "https://d396qusza40orc.cloudfront.net"
//					+ "/ml/docs/slides/Lecture"; ==> 1-18
		String BASE = "http://cs229.stanford.edu/notes/cs229-notes";
		String EXTENSION = ".pdf";
		String FOLDER = "src/io/download/";
		
		File dir = new File(FOLDER);
		try {
			if (!dir.exists())
				Files.createDirectory(dir.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=1; i<=11; i++) {
			URL url = null;
			try {
				url = new URL( BASE + i + EXTENSION);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			try( InputStream is = url.openStream()) {
				Files.copy( is, Paths.get( FOLDER + i + EXTENSION));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
