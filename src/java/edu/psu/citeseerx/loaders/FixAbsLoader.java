package edu.psu.citeseerx.loaders;

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.fixers.FixAbs;

/**
 * Throwaway fixer.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 * @deprecated
 */
public class FixAbsLoader {

    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        FixAbs fixAbs =
            (FixAbs)factory.getBean("fixAbs");
        try {
            fixAbs.fixAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- main
} //- class FixAbsLoader
