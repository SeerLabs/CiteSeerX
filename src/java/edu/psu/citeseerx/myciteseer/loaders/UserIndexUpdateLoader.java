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
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
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
