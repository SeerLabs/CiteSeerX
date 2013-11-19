/**
 * 
 */
package edu.psu.citeseerx.loaders;

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.misc.charts.ChartDataBuilder;

/**
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 *
 */
public class ChartDataBuilderLoader {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        boolean buildAll = false;
        
        if (args.length > 0){
            if (args[0].compareToIgnoreCase("all") == 0) {
                buildAll =  true;
            }else{
                System.out.println("To create the citegraph data for all the" +
                		"documents specify 'all' as a parameter.");
                System.exit(1);
            }
        }
            
        ListableBeanFactory factory = ContextReader.loadContext();
        ChartDataBuilder builder =
            (ChartDataBuilder)factory.getBean("chartDataBuilder");
        try {
            if (buildAll) {
                builder.buildAllChartData();
            }else{
                builder.buildChartData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    } //- main

} //- class ChartDataBuilderLoader
