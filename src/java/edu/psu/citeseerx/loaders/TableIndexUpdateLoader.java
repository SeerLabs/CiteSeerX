package edu.psu.citeseerx.loaders;

/**
 * Loads the indexUpdateManager bean and runs indexAll.
 * @author Isaac Councill
 * @version $$Rev$$ $$Date$$
 */

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.updates.TableIndexUpdater;

public class TableIndexUpdateLoader {

    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        TableIndexUpdater updater =
            (TableIndexUpdater)factory.getBean("tableIndexUpdater");
        try {
            updater.indexAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- main
} //- class IndexUpdateLoader
