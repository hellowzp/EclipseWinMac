package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;


/**
 * TO BE IMPROVED:
 * 
 * copy to a directory whose parent directory doesn't exist yet: Files.createDirectories()
 * File.delete() doesn't work 
 * open stream to a directory
 * Files.readAllLines() cannot read .DS_Store, how to know its encoding at first
 * MalformedInputException handling
 * 
 * copy with thread pool or BlockQueue
 * 
 * @author Benchun
 *
 */
public class EncodeTransformer {
	
	public static void transform( File source, String srcEncoding, 
								  File target, String tgtEncoding) 
								  throws IOException {
	    try (
	      BufferedReader br = new BufferedReader(
	    		  new InputStreamReader(
	    				  new FileInputStream(source), srcEncoding));
	      BufferedWriter bw = new BufferedWriter(
	    		  new OutputStreamWriter(
	    				  new FileOutputStream(target), tgtEncoding)); ) {
	          char[] buffer = new char[4096];
	          int read;
	          while ((read = br.read(buffer)) != -1)
	              bw.write(buffer, 0, read);
	    } 
	}
	
	public static void transform( Path srcPath, Charset srcEncoding,
								  Path desPath, Charset desEncoding)
								  throws IOException {		
		if(desPath.toFile().exists())
			desPath.toFile().delete();
		int srcRootPathDepth = srcPath.getNameCount();
		transform(srcPath, srcEncoding, desPath, desEncoding, srcRootPathDepth);
	}
	
	private static void transform( Path srcPath, Charset srcEncoding,
			  					   Path desPath, Charset desEncoding,
			  					   int srcRootPathDepth) throws IOException {		
		if(srcPath.getNameCount() > srcRootPathDepth) {
	    	String fileName = srcPath.getFileName().toString();
	    	desPath = Paths.get(desPath.toString(), fileName);
//	    	System.out.println(fileName + desPath);
	    }
		
		if( !Files.isDirectory(srcPath) ){			
//			List<String> lines = null;
//			try {
//				lines = Files.readAllLines(srcPath, srcEncoding);
//			} catch( MalformedInputException ex) {
//				//read file starts with "." will trigger java.nio.charset.MalformedInputException
//				System.out.println("File " + srcPath.toAbsolutePath() + " cann't be read"
//						+ " with the given charset, standard charset is used instead.");
////				lines = Files.readAllLines(srcPath);
//				System.err.println(ex.getCause() + ": " + ex.getMessage());
//			}
//
//			if(lines==null) return;
//			
//			//open a stream to folder will trigger java.nio.file.FileSystemException
//			try (BufferedWriter wr = Files.newBufferedWriter(desPath, desEncoding, 
//											StandardOpenOption.CREATE,  	
//											StandardOpenOption.WRITE,
//											StandardOpenOption.TRUNCATE_EXISTING) ){
//				for(String line : lines) 
//					wr.write(line);				
//			} 
			
			if( srcPath.toString().endsWith(".jsp") || srcPath.toString().endsWith(".properties") )
				transform(srcPath.toFile(), srcEncoding.name(), desPath.toFile(), desEncoding.name());
		} else {
			//create the directory first, otherwise it will throw NoSuchFileException when creating files
			Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-----");
		    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);		 			
		    Files.createDirectory(desPath, fileAttributes);
			
			File srcDir = srcPath.toFile();
			for(File file : srcDir.listFiles()) {
				transform(file.toPath(), srcEncoding, desPath, desEncoding, srcRootPathDepth);
			}				
		}

	}
	
	public static void main(String[] args) {
		Path src = Paths.get("/Users/Benchun/Documents/Engineering/NetBeans/AuctionJavaEE");
		Path des = Paths.get("/Users/Benchun/Documents/Engineering/NetBeans/AuctionJavaEE_Copy2");
		Charset srcCharset = Charset.forName("GB2312"); //unmapped charset
		Charset desCharset = StandardCharsets.UTF_8;
		try {
//			transform(src.toFile(), "GB2312", des.toFile(), "UTF-8");
			transform(src, srcCharset, des, desCharset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
