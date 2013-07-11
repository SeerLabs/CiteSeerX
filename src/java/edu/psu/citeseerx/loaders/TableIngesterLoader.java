package edu.psu.citeseerx.loaders;

import java.io.IOException;
import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.ingestion.TableIngester;

public class TableIngesterLoader {
	public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Must specify directory from which to ingest!");
            System.exit(0);
        }
        ListableBeanFactory factory = ContextReader.loadContext();
        TableIngester ingester =
            (TableIngester)factory.getBean("tableIngester");
        try {
            ingester.ingestDirectories(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- main
	} 
	