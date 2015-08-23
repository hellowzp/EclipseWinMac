package com.pack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public class Main {

	public static void main(String[] args) {
		String home = System.getProperty("user.dir");
		System.out.println(home);
		
		String filename = null;
		if(home.endsWith("src"))
			filename = "../lib/res";
		else
			filename = "lib/res";
		
		//file io interpretes relative path based on the current working dir
		try (InputStream is = new FileInputStream(filename)) {
			is.hashCode();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		
		//see core java v1 10.1.3 Resources, 
		//this method interpretes relative path based on the current class file directory
		//the resource file should be in the same dir as the class file 
		//if not, use the following absolute-path approach
		// see http://stackoverflow.com/questions/15581687/class-getresource-returns-null
		InputStream is1 = Main.class.getResourceAsStream("rel/res");
		try {
			Files.copy(is1, Paths.get("copy"+Math.random()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		IOUtils.closeQuietly(is1);
		
		//this method interpretes absolute path based on the classpath
		//thus in this case the project root dir must be added to classpath 
		//in eclipse by default only the src dir is added to the classpath 
		InputStream is2 = Main.class.getResourceAsStream("/lib/res");
		try {
			Files.copy(is2, Paths.get("copy"+Math.random()));  //will be created in the user.dir
		} catch (IOException e) {
			e.printStackTrace();
		}
		IOUtils.closeQuietly(is2);

	}

}
