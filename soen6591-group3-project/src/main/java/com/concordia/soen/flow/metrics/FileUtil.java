package com.concordia.soen.flow.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
	
	public static String read(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
    	String source = Files.lines(path).collect(Collectors.joining("\n"));
    	return source;
    }
	
	public static List<String> getFilePath(String path){
		File directory = new File(path);
		List<String> filePaths = new ArrayList<String>();
		
		if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Input is not a directory");
        }
		
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
            	filePaths.addAll(getFilePath(file.getAbsolutePath()));
            } else if (file.getName().endsWith(".java")) {
            	filePaths.add(file.getAbsolutePath());
            }
        }
		return filePaths;
	}
}
