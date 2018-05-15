package org.pinae.timon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FileUtils {

	private static Logger logger = Logger.getLogger(FileUtils.class);

	public static File getFile(String filename) {
		return getFile(null, filename);
	}

	public static File getFile(String path, String filename) {
		File file = null;
		
		if (path != null) {
			file = new File(path + File.separator + filename);
			if (file.exists() && file.isFile()) {
				return file;
			}
		} else {
			file = new File(filename);
			if (file != null &&file.exists() && file.isFile()) {
				return file;
			}
		}

		if (path != null) {
			file = new File(ClassLoaderUtils.getResourcePath("") + File.separator + path + File.separator + filename);
			if (file.exists() && file.isFile()) {
				return file;
			}
		} else {
			file = new File(ClassLoaderUtils.getResourcePath("") + File.separator + filename);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}

		URL resUrl = FileUtils.class.getClassLoader().getResource(filename);
		if (resUrl != null) {
			try {
				file = new File(resUrl.toURI());
				if (file != null) {
					return file;
				}
			} catch (URISyntaxException e) {

			}
			
		}

		return null;
	}

	public static File getDirectory(String path) {
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			return file;
		}
		file = new File(ClassLoaderUtils.getResourcePath("") + File.separator + path);
		if (file.exists() && file.isDirectory()) {
			return file;
		}
		return null;
	}
	
	public static InputStream getFileInputStream(String filename) {
		try {
			File file = getFile(filename);
			if (file != null) {
				return new FileInputStream(file);
			}
			
			InputStream fileStream = FileUtils.class.getClassLoader().getResourceAsStream(filename);
			if (fileStream != null) {
				return fileStream;
			}
		} catch (IOException e) {
			
		}
		return null;
	}

	public static StringBuffer read(String filename) {
		return read(FileUtils.getFile(null, filename));
	}
	
	public static StringBuffer read(InputStream fileStream) {
		try {
			StringBuffer buff = new StringBuffer();

			String encoding = "UTF-8";
			if (fileStream != null) {
				InputStreamReader read = new InputStreamReader(fileStream, encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					buff.append(line);
				}
				read.close();
				return buff;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static StringBuffer read(File file) {
		if (file != null && file.exists() && file.isFile()) {
			try {
				return read(new FileInputStream(file));
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public static List<String> readList(String filename) {
		return readList(FileUtils.getFile(null, filename));
	}
	
	public static List<String> readList(InputStream fileStream) {
		try {
			List<String> content = new ArrayList<String>();

			String encoding = "UTF-8";
			if (fileStream != null) {
				InputStreamReader read = new InputStreamReader(fileStream, encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					content.add(line);
				}
				read.close();
				return content;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static List<String> readList(File file) {
		if (file != null && file.exists() && file.isFile()) {
			try {
				return readList(new FileInputStream(file));
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public static void write(String filename, StringBuffer content, String encoding) {
		try {
			FileWriter writer = new FileWriter(filename);
			writer.write(content.toString());
			writer.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public static List<File> getFileList(String path, String[] excludes) {
		return getFileList(new File(path), excludes);
	}

	public static List<File> getFileList(File path, String[] excludes) {

		List<File> filelist = new ArrayList<File>();

		File[] files = path.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					filelist.addAll(getFileList(files[i], excludes));
				} else {
					boolean isExclude = true;
					if (excludes != null && excludes.length > 0) {
						for (String exclude : excludes) {
							exclude = exclude.trim();
							String filename = files[i].getName();
							if (filename.matches(exclude)) {
								isExclude = false;
							}
						}
					}

					if (isExclude == true) {
						filelist.add(files[i]);
					}
				}
			}
		}

		return filelist;
	}
}
