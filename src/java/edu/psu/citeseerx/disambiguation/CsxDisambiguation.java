/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.disambiguation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.loaders.ContextReader;
import edu.psu.citeseerx.disambiguation.CsxAuthorBlock;
import edu.psu.citeseerx.disambiguation.CsxAuthorFilter;
import edu.psu.citeseerx.disambiguation.dbscan.DBScan;
import edu.psu.citeseerx.disambiguation.dao.CsxAuthor;

/**
 * CsxDisambiguation
 * 
 * @author Puck Treeratpituk
 * @version $Rev$ $Date$
 */
public class CsxDisambiguation {

	public static final double EPS  = 0.1;
	public static final int MIN_PTS = 2;

	public static void initDirectories(String dirpath) throws Exception {
		for (char a = 'A'; a <= 'Z'; a++) {
			for (char b = 'A'; b <= 'Z'; b++) {
				File dir = new File(dirpath + "/" + a + b);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
		}
	}
	public static void createBlocks(ListableBeanFactory factory) 
	throws Exception {
		String dirpath = "data/csauthors/blocks";

		DataSource dataSource = (DataSource) factory.getBean("csxDataSource");
		
		PreparedStatement st = dataSource.getConnection()
			.prepareStatement("SELECT * FROM authors", 
							  ResultSet.TYPE_FORWARD_ONLY,
							  ResultSet.CONCUR_READ_ONLY);
		st.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs = st.executeQuery();
		
		initDirectories(dirpath);

		CsxAuthorFilter filter = 
		    (CsxAuthorFilter) factory.getBean("csxAuthorFilter"); 
		//new CsxAuthorFilter("data/csauthors/name_stopwords.txt");
		BufferedWriter skip    = new BufferedWriter(new FileWriter("skip.txt"));

		int count = 0;
		Map<String, List<String>> blocks = new HashMap<String, List<String>>();
		while (rs.next()) {
			count ++;
			if ((count % 10000) == 0)
				System.out.println("#Auth:" + count);
			String rsname = rs.getString("name");
			if (!filter.isStopword(rsname) && !filter.isInstitute(rsname) && 
				!filter.isPosition(rsname)) {
				
				CsxAuthor auth = new CsxAuthor(rs);			
				String lname = auth.getLastName();
				String fname = auth.getFirstName();
				
				if ((lname != null) && (fname != null)) {
					if ((lname.charAt(0) >= 'A') && (lname.charAt(0) <= 'Z') &&
						(fname.charAt(0) >= 'A') && (fname.charAt(0) <= 'Z') &&
						!((fname.length() == 1) && (lname.length() == 1)) &&
						!(lname.matches(".*/.*"))) {
						
						String l_init = lname.substring(0,1).toUpperCase();
						String f_init = fname.substring(0,1).toUpperCase();
						String key = l_init + f_init + "/" + 
						    lname.toLowerCase() + "_" + f_init.toLowerCase() + 
						    ".txt";
						
						List<String> list;
						if (!blocks.containsKey(key)) {
							list = new ArrayList<String>();
							blocks.put(key, list);
						}
						else {
							list = blocks.get(key);
						}
						list.add(auth.getId());
					}
					else {
						skip.write("SKIP: [" + rsname + "]\n");
					}
				}
			}
		}
		skip.close();

		for (String key: blocks.keySet()) {
			List<String> aids = blocks.get(key);
			// only care about cluster with more than one document
			if (aids.size() > 1) {
				BufferedWriter out = new BufferedWriter(
				        new FileWriter(dirpath + "/" + key));
				for (String aid: aids) {
					out.write(aid + "\n");
				}
				out.close();
			}
		}
	}
	
	public static void disambiguate(ListableBeanFactory factory, String infile,
	        String outfile) throws Exception {
		
		CsxAuthorBlock block = 
		    (CsxAuthorBlock) factory.getBean("csxAuthorBlock");
		block.loadAuthors(infile);

		int min_pts = MIN_PTS;
		if (block.points.size() <= 3) min_pts = 1;
		DBScan dbscan = new DBScan(block, EPS, min_pts);
		dbscan.run();
		dbscan.printResults(outfile);
	}

	public static void disambiguateFile(ListableBeanFactory factory, 
	        String infile) throws Exception {
		//CsxAuthorBlock block = new CsxAuthorBlock(conn, rconn, "jian_huang2.block");
		//CsxAuthorBlock block = new CsxAuthorBlock(conn, rconn, "data/csauthors/blocks/MP/mitra_p.txt");
		//CsxAuthorBlock block = new CsxAuthorBlock(conn, rconn, "data/csauthors/blocks/BK/borner_k.txt");

		CsxAuthorBlock block = 
		    (CsxAuthorBlock) factory.getBean("csxAuthorBlock");
		block.loadAuthors(infile);

		int min_pts = MIN_PTS;
		if (block.points.size() <= 3) min_pts = 1;
		DBScan dbscan = new DBScan(block, EPS, min_pts);
		dbscan.run();
		dbscan.printResults();
	}
	
	public static void disambiguateDirectory(ListableBeanFactory factory, 
	        String indir, String outdir) throws Exception {
				
		File dir = new File(indir);
		String[] files = dir.list(); 
		Arrays.sort(files);
		for (String file : files) {
			String infile  = indir + "/" + file;
			String outfile = outdir + "/" + file.replace(".txt",".out");
			System.out.println("> " + file);
			disambiguate(factory, infile, outfile);				
		}
	}

	private static void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("");
		formatter.printHelp( "CsxDisambiguation", options );
		System.out.println("");
	}

	private static Options setupOptions() {
		Options options = new Options();

		options.addOption("help", false, "print help message");
		
		Option cmd      = OptionBuilder.withArgName("cmd")
		    .hasArg()
            .withDescription("init_dirs, init_blocks, dbscan")
            .create("cmd");
		Option infile   = OptionBuilder.withArgName("file")
			.hasArg()
			.withDescription("a single input file")
			.create("infile");
		Option indir   = OptionBuilder.withArgName("indir")
			.hasArg()
			.withDescription("directory containing the input files")
			.create("indir");
		Option outdir   = OptionBuilder.withArgName("outdir")
			.hasArg()
			.withDescription("directory to put the output files")
			.create("outdir");
		options.addOption(cmd);
		options.addOption(infile);
		options.addOption(indir);
		options.addOption(outdir);
		return options;
	}

	public static boolean validOptions(CommandLine line, Options options) {
		if (!line.hasOption("cmd")) {
			System.out.println("\nERROR: Please specify one command");	
			return false;
		}
		String cmd = line.getOptionValue("cmd");
		if (cmd.equals("dbscan") && !(line.hasOption("infile") ^ line.hasOption("indir"))) {
			System.out.println("\nERROR: Please specify either -infile or -indir, but not both");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Date start = new Date();			
		System.out.println("START:" + start);
		try {
            CommandLineParser cmdparser = new GnuParser();
			Options options  = setupOptions();
			CommandLine line = cmdparser.parse(options, args);

			ListableBeanFactory factory = ContextReader.loadContext();
			if (!validOptions(line, options)) {
				usage(options);
				System.exit(0);
			}
			String cmd = line.getOptionValue("cmd");
			String infile = line.getOptionValue("infile");
			if (cmd.equals("init_dirs")) {
				// 1) init directories
				initDirectories("data/csauthors/blocks");
				initDirectories("data/csauthors/output");				
			}
			else if (cmd.equals("init_blocks")) {
				// 2) create blocks
				createBlocks(factory);
			}
			else if (cmd.equals("dbscan")) {
				// 3) disambiguate (required 1. & 2.)
				//disambiguateDirectory(factory, args[0], args[1]);
				//disambiguateFile(factory, args[0]);
				disambiguateFile(factory, infile);
			}
			/*else if (cmd.equals("match_author")) {
				String input_file = "";
				disambiguateOneAuthor(input_file);
				}*/
		} catch (Exception ex) {
			ex.printStackTrace();
            Date now = new Date();			
			System.out.println("CRASH:" + now);
		}
		Date end = new Date();			
		System.out.println("END:" + end);
		System.out.println("TIME:" + (end.getTime()-start.getTime()));
	}
}
