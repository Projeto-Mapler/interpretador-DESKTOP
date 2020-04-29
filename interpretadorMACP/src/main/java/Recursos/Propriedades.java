package Recursos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;


public class Propriedades {
	
	private static File configFile = new File("config.properties");
	private static File arqRecentes = new File("arquivos.properties"); 
	private static String file1, file2, file3, file4, autosave;
	
	public static void setPropriedade(String nomeP, String valorP) {
		try {
		    Properties props = new Properties();
		    props.setProperty(nomeP, valorP);
		    FileWriter writer = new FileWriter(configFile);
		    props.store(writer, "file settings");
		    writer.close();
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
		}
	}
	
	public static String getPropriedade(String nomeP) {
		try {
		    FileReader reader = new FileReader(configFile);
		    Properties props = new Properties();
		    props.load(reader);
		    String host = props.getProperty(nomeP);
		    return host;
		} catch (FileNotFoundException ex) {
		    return null;
		} catch (IOException ex) {
		    return null;
		}
	}
	
	public static int getNumDeFile() {
		int num = 0;
		String file = Propriedades.file1;
		if(file != null && !file.equals("")) {
			num = 1;
			file = Propriedades.file2;
			if(file != null && !file.equals("")) {
				num = 2;
				file = Propriedades.file3;
				if(file != null && !file.equals("")) {
					num = 3;
					file = Propriedades.file4;
					if(file != null && !file.equals("")) {
						num = 4;
					}
				}
			}
		}
		return num;
	}
	
	public static void zerarFiles() {
		Propriedades.file1 = "";
		Propriedades.file2 = "";
		Propriedades.file3 = "";
		Propriedades.file4 = "";
	}
	
	public static void setFile(String link) {
		int num = getNumDeFile();
		if(num == 0) {
			Propriedades.file1 = link;
		}else if(num == 1 && !Propriedades.file1.equals(link)) {
			Propriedades.file2 = Propriedades.file1;
			Propriedades.file1 = link;
		}else if(num==2 && !Propriedades.file1.equals(link) && !Propriedades.file2.equals(link)) {
			Propriedades.file3 = Propriedades.file2;
			Propriedades.file2 = Propriedades.file1;
			Propriedades.file1 = link;
		}else if(num>=3 && !Propriedades.file1.equals(link) && !Propriedades.file2.equals(link) && !Propriedades.file3.equals(link)) {
			Propriedades.file4 = Propriedades.file3;
			Propriedades.file3 = Propriedades.file2;
			Propriedades.file2 = Propriedades.file1;
			Propriedades.file1 = link;
		}
	}
	
	public static String getFile(String file) {
		try {
			String link = "";
		    FileReader reader = new FileReader(arqRecentes);
		    Properties props = new Properties();
		    props.load(reader);
		    link = props.getProperty(file);
			reader.close();
			return link;
		} catch (FileNotFoundException ex) {
		    return null;
		} catch (IOException ex) {
		    return null;
		}
	}
	
	public static void prepararBackup() {
		try {
		    FileReader reader = new FileReader(arqRecentes);
		    Properties props = new Properties();
		    props.load(reader);
		    Propriedades.file1 = props.getProperty("file1");
			Propriedades.file2 = props.getProperty("file2");
			Propriedades.file3 = props.getProperty("file3");
			Propriedades.file4 = props.getProperty("file4");
			reader.close();
		} catch (FileNotFoundException ex) {
		    
		} catch (IOException ex) {
		    
		}
		
	}
	
	public static void setBackup() {
		try {
			Properties props = new Properties();
		    props.setProperty("file1", Propriedades.file1);
		    if (Propriedades.file2 == null)
		    	Propriedades.file2 = "";
		    props.setProperty("file2", Propriedades.file2);
		    if (Propriedades.file3 == null)
		    	Propriedades.file3 = "";
		    props.setProperty("file3", Propriedades.file3);
		    if (Propriedades.file4 == null)
		    	Propriedades.file4 = "";
		    props.setProperty("file4", Propriedades.file4);
		    FileWriter writer = new FileWriter(arqRecentes);
		    props.store(writer, "file settings");
		    writer.close();
		} catch (FileNotFoundException ex) {
		    // file does not exist
			System.out.println("File not found");
		} catch (IOException ex) {
		    // I/O error
			System.out.println("IO exception");
		}
	}
}
