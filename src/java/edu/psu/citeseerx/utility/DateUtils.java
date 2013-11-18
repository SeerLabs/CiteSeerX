package edu.psu.citeseerx.utility;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Date transformation tools.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DateUtils {

    private static final SimpleDateFormat rfc822formatter =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    /**
     * @param date
     * @return a String representing a date in RFC822 format.
     */
    public static String formatRFC822(Date date) {
        return rfc822formatter.format(date);
    } //- formatRFC822
    
    private static final SimpleDateFormat rfc3339formatter =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    /**
     * @param date
     * @return a String representing a date in RFC3339 format.
     */
    public static String formatRFC3339(Date date) {
        return rfc3339formatter.format(date);
    } //- formatRFC3339
    
    /**
     * @param date
     * @return A string representing a date (year-month-day only) in ISO8601 
     * (UTC)
     */
    public static String formatDateISO8601UTC(Date date) {
    	return DateFormatUtils.formatUTC(date, 
				DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    } //- formatDateISO8601UTC
    
    /**
     * @param date
     * @return A string representing a date until seconds in ISO8601 
     * (UTC)
     */
    public static String formatDateTimeISO8601UTC(Date date) {
    	return DateFormatUtils.formatUTC(date, 
				DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
    } //- formatDateISO8601UTC
    
    /**
     * 
     * @param date
     * @return A date build based on a date string in ISO8601
     * @throws ParseException
     */
    public static Date parseDateToUTCDate(String date) throws ParseException {
    	String[] patterns = {DateFormatUtils.ISO_DATE_FORMAT.getPattern(),
    			DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern()};
    	return org.apache.commons.lang.time.DateUtils.parseDate(date, patterns);
    } //- parseDateToUTCDate
}  //- class DateUtils
