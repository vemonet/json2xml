package net.javacrumbs.json2xml;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "rdf4j-sparql-operations")
public class CliOptions {
	@Option(names = { "-h", "--help" }, usageHelp = true, description = "Display a help message")
	boolean help = false;
	
	@Option(names= {"-i", "--input-file"}, description = "JSON file to convert to XML.")
	String inputFilePath = null;

}