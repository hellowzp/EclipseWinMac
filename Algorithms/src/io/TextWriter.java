package io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class TextWriter {
	
	private PrintWriter wr;
	MyPrintWriter pw;
	
	public TextWriter(String folder, String fname) {
		
		String userDir = System.getProperty("user.dir");
		Path path = Paths.get( userDir + File.separator + folder, fname);
		System.out.println(path);
		System.out.println(Paths.get("relative_path"));
		System.out.println(Paths.get("relative_path").toAbsolutePath());
		
		//create new dir if not exist
		//http://jakubstas.com/creating-files-and-directories-nio2/
		Path dir = Paths.get("dir");
		if (!Files.exists(dir)) {
		    Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-----");
		    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
		 
		    try {
		        Files.createDirectory(dir,fileAttributes);
		    } catch (IOException e) {
		        System.err.println(e);
		    }
		}	
		
		DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm");
		//Mind that relative path should not start with '/'
		String fileName = "dir/" + fmt.format(new Date()) + ".csv";  
		Path filePath = Paths.get(fileName);

		// http://jakubstas.com/working-with-files-and-directories-nio2/
//		try (BufferedWriter bw = Files.newBufferedWriter( filePath, 
//										StandardCharsets.UTF_8,		//charset， writer is character-oriented, 
//																	//output stream doesn't need to set charset
//																	//because its byte-oriented
//										StandardOpenOption.CREATE,  //open options
//										StandardOpenOption.WRITE )){
//			wr = new PrintWriter(bw, false);  //without auto-flush lines
//			pw = new MyPrintWriter(wr);
//			//print is a wrapper for the low-level write method
//			wr.println("##stage completion table");
//			wr.println();
//		} catch (IOException e) {
//			System.out.println("open file stream exception");
//			e.printStackTrace();
//		}
		
		//with auto-close, all afterwards print will fail
		try (OutputStream os = Files.newOutputStream( filePath,
					// StandardCharsets.UTF_8, //charset， writer is character-oriented,
					// output stream doesn't need to set charset
					// because its byte-oriented
					StandardOpenOption.CREATE, // open options
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE) ){
			wr = new PrintWriter(os, false); // without auto-flush lines
			System.out.println(wr.checkError());
			pw = new MyPrintWriter(wr);
			// print is a wrapper for the low-level write method
			wr.println("##stage completion table");
			System.out.println(wr.checkError());
		} catch (IOException e) {
			System.out.println("open file stream exception");
			e.printStackTrace();
		}
		
//		try {
//			wr = new PrintWriter(new FileOutputStream(filePath.toFile()));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		//don't make it auto-close
		try {
			wr = new PrintWriter( Files.newBufferedWriter( filePath, 
										StandardCharsets.UTF_8, 
										StandardOpenOption.CREATE, 
										StandardOpenOption.APPEND,
										StandardOpenOption.WRITE ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		try {
//			wr = new PrintWriter(fileName);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		//the low-level stream are already auto-closed!!
		wr.print(false);
		wr.print(',');
		wr.println(1234);
		System.out.println(wr.checkError());
//		System.out.println(pw.isOpen());
		
		Path logFile = Paths.get("dir/GoogleSoftwareUpdateAgent.log");
		ByteBuffer buf = ByteBuffer.allocate(256);
		try(FileChannel channel = FileChannel.open( logFile, 
										StandardOpenOption.READ, 
										StandardOpenOption.WRITE )){
			
			channel.read(buf, channel.size() - 20); //the last 20 bytes
			System.out.println(new String(buf.array(), StandardCharsets.UTF_8));
			System.out.println(new String(buf.array(), StandardCharsets.US_ASCII));
			System.out.println(new String(buf.array(), StandardCharsets.ISO_8859_1));
						
			System.out.println(channel.size());
			channel.write(buf);
			channel.force(false);
			System.out.println(channel.size());

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread( new Runnable() {
			@Override
			public void run() {
				System.out.println("calling the VM shutdown hook thread to close resource handler..");
				wr.close();
			}
		}));
		
//		try {
//			Files.createDirectories(dir);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		try(PrintWriter writer = new PrintWriter(new FileWriter("no/wang.txt")) ){
//			writer.print(false);
//			writer.print(',');
//			writer.println(1234);
//		} catch (IOException e) {
//			System.out.println("open file stream exception");
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		new TextWriter("folder", "file");
	}
	
	class MyPrintWriter extends PrintWriter {
		
		public MyPrintWriter(PrintWriter writer) {
			super(writer);
		}
		
		boolean isOpen() {
			return out != null;
		}
	}
}
