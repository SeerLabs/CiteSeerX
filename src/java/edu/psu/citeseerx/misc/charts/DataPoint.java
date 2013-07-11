package edu.psu.citeseerx.misc.charts;

import java.util.Comparator;

public class DataPoint {
    public int year;
    public int ncites;
    public DataPoint(int year) {
        this.year = year;
        ncites = 0;
    }
}

class DataPointComparator implements Comparator<DataPoint> {
    
    public int compare(DataPoint o1, DataPoint o2) {

        if (o1.year > o2.year) {
            return 1;
        }
        if (o2.year > o1.year) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DataPointComparator) {
            return true;
        } else {
            return false;
        }
    }
}
