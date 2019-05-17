package net.javacrumbs.json2xml;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import picocli.CommandLine;

public class JsonXmlRun {
	
	public static void main(String[] args) throws Exception {
		// TODO: logger
		CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
		if(cli.help)
			printUsageAndExit();
		
		File inputFile = new File(cli.inputFilePath);
		
		// if input file is a directory 
		if (inputFile.isDirectory()) {
			Collection<File> files = FileUtils.listFiles(
					inputFile,
					new RegexFileFilter(".*\\.json"),
					DirectoryFileFilter.DIRECTORY
			);
			List<File> fileList = new ArrayList<File>(files);
			Collections.sort(fileList);
			// Recursively iterate over files in the directory in the alphabetical order
			Iterator<File> iterator = fileList.iterator();
			while (iterator.hasNext()) {
				File f = iterator.next();
				try {
					transformFileToJson(FileUtils.readFileToString(f, "UTF-8"), f.getAbsolutePath() + ".xml");
				} catch (Exception e) {
					System.out.println(e);
					System.out.println("Error processing file: " + f.getAbsolutePath());
				}
			}
		} else {
			// If input is a single file
			try {
				transformFileToJson(FileUtils.readFileToString(inputFile, "UTF-8"), cli.inputFilePath);
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Error processing file: " + cli.inputFilePath);
			}
		}
	}
	
	// Create a XML file for each given file
	private static String transformFileToJson(String stringJson, String outputFilePath) throws IOException, TransformerConfigurationException, TransformerException {	
		PrintWriter out = new PrintWriter(outputFilePath);
		
		Node node = JsonXmlHelper.convertToDom(stringJson, null, true, "root");
		node.toString();
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		InputSource source = new InputSource(new StringReader(stringJson));
		StringWriter writer = new StringWriter();
		transformer.transform(new SAXSource(new JsonXmlReader(null, true, "root"), source),  new StreamResult(writer));
		String xml = writer.toString();
		out.println(xml);
		out.close();
		System.out.println("XML done");
		System.out.println(xml);
		return xml;
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
