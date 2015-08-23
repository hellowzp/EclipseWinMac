package io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider {

	public static void main(String[] args) {
		
		try {
	//		Document doc = Jsoup.connect(url).get();
			Document doc = Jsoup.parse(new File("coursera_pgm.html"), "UTF-8", "");
			Elements fileHrefs = doc.select("a[href$=.pdf]");  
			
			String FOLDER = "src/io/download/";
			File dir = new File(FOLDER);
			if (!dir.exists())
				Files.createDirectory(dir.toPath());
			
			for(Element href : fileHrefs) {
				String absUrl = href.attr("abs:href");			
//				String fileName = absUrl.substring(
//					   absUrl.lastIndexOf("/") + 1, absUrl.length() - 4)
//					   + "_" + href.text() + ".pdf";
				String fileName = absUrl.substring(
						  absUrl.lastIndexOf("/") + 1, absUrl.length());
				
			 /*
			  * sanitize the file name
			  * and note that - (hyphen) and . are special chararacters in re
			  * so they need to be escaped by \
			  * while \ itself in Java is an escape character
			  * so it needs to be escaped first thus two back slash
			  * 
			  * Also note that re in a for loop will slow down the program a lot
			  * here by contrast it's alsmost 300% slower if don't copy files
			  * and string replacement will double the memory use
			  * so try to avoid this sanitization operation
			  */			 
//				fileName = fileName.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
				System.out.println(fileName);
				
				File file = new File(FOLDER + fileName);
				if( !file.exists()) {
					URL url = new URL(absUrl);			
					InputStream is = url.openStream();
					// use a separate try block so that
					// one copy exception doesn't stop the whole program
					try {
						Files.copy( is, Paths.get( FOLDER + fileName ));
					} catch(IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
