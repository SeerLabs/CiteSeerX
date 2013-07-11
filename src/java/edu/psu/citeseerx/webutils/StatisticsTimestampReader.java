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
package edu.psu.citeseerx.webutils;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class StatisticsTimestampReader {

    public static String getReadableTimestamp(String fpath) {
        try {
            FileReader fr = new FileReader(fpath);
            BufferedReader reader = new BufferedReader(fr);
            String timestamp = reader.readLine();
            reader.close();
            return normalizeTimestamp(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown date";
        }
    }
    
    private static String normalizeTimestamp(String timestr)
    throws NumberFormatException {
        timestr = timestr.trim();
        long time = Long.parseLong(timestr);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        String month = getMonthName(cal);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        return month + " " + day + ", " + year;

    }
    
    private static final String[] months = {
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
    };
    
    private static String getMonthName(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        return months[month];
    }
}
