/**
 * 
 */
package edu.psu.citeseerx.myciteseer.loaders;

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.loaders.ContextReader;
import edu.psu.citeseerx.myciteseer.updates.UserIndexUpdateManager;

/**
 * Loads the userIndexUpdateManager bean and runs indexSinceLastUpdate.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class UserIndexUpdateLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ListableBeanFactory factory = ContextReader.loadContext();
		UserIndexUpdateManager updater = 
			(UserIndexUpdateManager)factory.getBean("userIndexUpdateManager");
		try {
			updater.indexSinceLastUpdate();
		} catch (Exception e) {
            e.printStackTrace();
        }
	} //- main

} //- class UserIndexUpdateLoader
