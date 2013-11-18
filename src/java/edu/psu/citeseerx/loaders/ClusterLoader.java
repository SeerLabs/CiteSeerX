package edu.psu.citeseerx.loaders;

import java.io.IOException;
import org.springframework.beans.factory.ListableBeanFactory;
import edu.psu.citeseerx.citematch.keybased.*;

/**
 * Loads the citationClusterer bean and runs buildAll with the specified
 * command-line argument.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ClusterLoader {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please specify a starting ID.  If you would " +
                    "like to cluster everything, just specify 0.0.0.0.0");
            System.exit(1);
        }
        ListableBeanFactory factory = ContextReader.loadContext();
        KeyMatcher matcher =
            (KeyMatcher)factory.getBean("citationClusterer");
        try {
            matcher.buildAll(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- main
} //- class ClusterLoader
