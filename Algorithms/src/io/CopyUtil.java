package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;

public class CopyUtil {

	public static void copyWithStream(File src, File des) throws IOException {
		if(isSameFile(src, des)) return;
		InputStream is = null;
		OutputStream os = null;
		
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(des);
			
			int bufSize = 1024;
			byte[] buf = new byte[bufSize];
			int len = 0;
			while( (len = is.read(buf, 0, bufSize)) > 0)
				os.write(buf,0,len);
//			os.flush();
//			System.out.println("throwing IO exception 1..");
//			throw new IOException("intentional IO exception 1..");
		} finally {
//			System.err.println("finally IO exception 1 thrown without being caught..");
			try{ 
				if (is != null) is.close(); 
//				System.out.println("throwing IO exception 2..");
//				throw new IOException("intentional IO exception 2..");
			} catch(IOException e) { 
				System.out.println("catch exception: " + e.getMessage());
				e.printStackTrace(); 
			}

//			System.out.println("closing output stream still..");
			try{ if (os != null) os.close(); } catch(IOException e) { e.printStackTrace(); }
			
			// with Commons IO, this can be simply done as
//			IOUtils.closeQuietly(is);
//			IOUtils.closeQuietly(os);
		}
	}
	
	//if des doesn't exist, FileNotFoundException may be thrown if using FileChannel.open() directly
	public static void copyWithChannel(File src, File des) throws IOException {
		if(isSameFile(src, des)) return;
		FileChannel srcChannel = null;
		FileChannel desChannel = null;
		
//		srcChannel = new FileInputStream(src).getChannel();
//		desChannel = new FileOutputStream(des).getChannel();

		try {
			srcChannel = FileChannel.open(src.toPath(), StandardOpenOption.READ);
			
			//take care with this!!
//			if(!des.exists()) des.createNewFile();  
			if(!des.exists()) {
				desChannel = new FileOutputStream(des).getChannel();
			}else{
				desChannel = FileChannel.open(des.toPath(), StandardOpenOption.WRITE, 
												StandardOpenOption.TRUNCATE_EXISTING );
			}
			desChannel.transferFrom(srcChannel, 0, srcChannel.size());
		} finally {
			if(srcChannel!=null) srcChannel.close();
			if(desChannel!=null) desChannel.close();
		}
	}
	
	//if file already exists, FileAlreadyExistsException will be thrown
	public static void copyWithJava7(File src, File des) throws IOException {
		if(isSameFile(src, des)) return;
		Files.copy( src.toPath(), des.toPath(), 
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES,
				LinkOption.NOFOLLOW_LINKS );
	}
	
	public static void copyWithApacheCommonsIO(File src, File des) throws IOException {
		FileUtils.copyFile(src, des); 
	}
	
	//more info should be checked like isDirectory, permission.. 
	private static boolean isSameFile(File src, File des) throws IOException {
		return src.getCanonicalPath().equals(des.getCanonicalPath());
	}
	
	
	//http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	//http://docs.oracle.com/javase/tutorial/essential/io/copy.html
	public static Path copyDirectory(Path source, Path target, CopyOption... options)
	        throws IOException {
        CopyVisitor copyVisitor = new CopyVisitor(source, target, options);
        EnumSet<FileVisitOption> fileVisitOpts;
        if ( Arrays.asList(options).contains(LinkOption.NOFOLLOW_LINKS )){
            fileVisitOpts = EnumSet.noneOf(FileVisitOption.class);
        } else {
            fileVisitOpts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        }
        Files.walkFileTree(source, fileVisitOpts, Integer.MAX_VALUE, copyVisitor);
	    return target;
    }
	
	
	public static void main(String[] args) {
		//http://stackoverflow.com/questions/5714053/how-can-we-redirect-eclipse-console-output-to-a-file
//		try {
//			System.setOut( 
//					new PrintStream( 
//							Files.newOutputStream(
//								Paths.get("runtime_test.txt"),
//								StandardOpenOption.APPEND, 
//								StandardOpenOption.CREATE ), 
//					true));  //auto-flush
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		try {
//			System.setErr( 
//					new PrintStream( 
//							Files.newOutputStream(
//								Paths.get("runtime_test.txt"),
//								StandardOpenOption.APPEND, 
//								StandardOpenOption.CREATE ), 
//					true));  //auto-flush
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	
		System.out.println("\n-----new test-----");
		File src = new File("dir/国家公派留学人员须知.pdf");
		
		try {
			File des = new File("dir/copy1.pdf");
			long t = System.currentTimeMillis();
			copyWithStream(src, des);
			System.out.println(System.currentTimeMillis() - t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			File des = new File("dir/copy2.pdf");
			long t = System.currentTimeMillis();
			copyWithChannel(src, des);
			System.out.println(System.currentTimeMillis() - t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			File des = new File("dir/copy3.pdf");
			if(des.exists()) des.delete();
			long t = System.currentTimeMillis();
			copyWithJava7(src, des);
			System.out.println(System.currentTimeMillis() - t);
		} catch (IOException e) {
			System.out.println(e.getMessage());
//			e.printStackTrace();
		}
		
		try {
			File des = new File("dir/copy4.pdf");
			long t = System.currentTimeMillis();
			copyWithApacheCommonsIO(src, des);
			System.out.println(System.currentTimeMillis() - t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		try {
//			copyDirectory( Paths.get("dir"), Paths.get("copiedDir"), 
//							StandardCopyOption.REPLACE_EXISTING,
//							StandardCopyOption.COPY_ATTRIBUTES );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	
    static class CopyVisitor implements FileVisitor<Path>  {
        final Path source;
        final Path target;
        final CopyOption[] options;

        CopyVisitor(Path source, Path target, CopyOption... options) {
             this.source = source;  this.target = target;  this.options = options;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
	        // before visiting entries in a directory we copy the directory
	        // (okay if directory already exists).
	        Path newdir = target.resolve(source.relativize(dir));
	        try {
	            Files.copy(dir, newdir, options);
	        } catch (FileAlreadyExistsException x) {
	            // ignore
	        } catch (IOException x) {
	            System.err.format("Unable to create: %s: %s%n", newdir, x);
	            return FileVisitResult.SKIP_SUBTREE;
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
	        Path newfile= target.resolve(source.relativize(file));
	        try {
	            Files.copy(file, newfile, options);
	        } catch (IOException x) {
	            System.err.format("Unable to copy: %s: %s%n", source, x);
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
	        // fix up modification time of directory when done
	        if (exc == null && Arrays.asList(options).contains(StandardCopyOption.COPY_ATTRIBUTES)) {
	            Path newdir = target.resolve(source.relativize(dir));
	            try {
	                FileTime time = Files.getLastModifiedTime(dir);
	                Files.setLastModifiedTime(newdir, time);
	            } catch (IOException x) {
	                System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
	            }
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc) {
	        if (exc instanceof FileSystemLoopException) {
	            System.err.println("cycle detected: " + file);
	        } else {
	            System.err.format("Unable to copy: %s: %s%n", file, exc);
	        }
	        return FileVisitResult.CONTINUE;
	    }
	    
	}

}
