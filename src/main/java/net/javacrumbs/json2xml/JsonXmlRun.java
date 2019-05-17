package net.javacrumbs.json2xml;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import picocli.CommandLine;

public class JsonXmlRun {
	
	public static void main(String[] args) throws Exception {
		try {
			// TODO: logger
			CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
			if(cli.help)
				printUsageAndExit();
			
			//System.out.println("Performing operation: " + cli.queryOperation.toString());
			File inputFile = new File(cli.inputFilePath);
			if(!inputFile.exists())
				throw new IllegalArgumentException("Input file \"" + inputFile.getAbsolutePath() + "\" does not exist");
			if(!inputFile.canRead())
				throw new SecurityException("Can not read from input file \"" + inputFile.getAbsolutePath() + "\"");
			
			// if input file is a directory 
//			if (inputFile.isDirectory()) {
//				Collection<File> files = FileUtils.listFiles(
//						inputFile,
//						new RegexFileFilter(".*\\.(rq|sparql)"),
//						DirectoryFileFilter.DIRECTORY
//				);
//				List<File> fileList = new ArrayList<File>(files);
//				Collections.sort(fileList);
//				// Recursively iterate over files in the directory in the alphabetical order
//				Iterator<File> iterator = fileList.iterator();
//				while (iterator.hasNext()) {
//					File f = iterator.next();
//					String queryString = resolveVariables(FileUtils.readFileToString(f));
//					logger.info("Executing: ");
//					logger.info(queryString);
//					executeQuery(conn, queryString, f.getPath());
//				}
//				
//			} else if (FilenameUtils.getExtension(inputFile.getName()).equals("yaml")) { 
//				// If input file is YAML we parse it to execute provided queries
//				parseQueriesYaml(conn, inputFile);
//			} else {
				// Single file provided
//				String queryString = resolveVariables(FileUtils.readFileToString(inputFile));
//				executeQuery(conn, queryString, inputFile.getPath());
			
			String stringJson = readFile(cli.inputFilePath, StandardCharsets.UTF_8);
			
			Node node = JsonXmlHelper.convertToDom(stringJson, null, true, "root");
			node.toString();
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			InputSource source = new InputSource(new StringReader(stringJson));
			StringWriter writer = new StringWriter();
			//DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(new JsonXmlReader(null, true, "root"), source),  new StreamResult(writer));
			String xml = writer.toString();
			System.out.println("XML done");
			System.out.println(xml);
			//result.getNode();

		} catch (Exception e) {
			printUsageAndExit(e);
		}
	}
	
	private static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	private static void printUsageAndExit() {
		printUsageAndExit(null);
	}
	
	private static void printUsageAndExit(Throwable e) {
		CommandLine.usage(new CliOptions(), System.out);
		if(e == null)
			System.exit(0);
		e.printStackTrace();
		System.exit(-1);
	}

}
