package io.youngkoss.app.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 */
@SuppressWarnings("nls")
public class DateUtils {

   public static final DateTimeFormatter df_HHMM = DateTimeFormat.forPattern("HHmm");
   public static final DateTimeFormatter df_HH_MM = DateTimeFormat.forPattern("HH:mm");
   public static final DateTimeFormatter df_YYYY_MM_DD_HH_MM_SS = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
}
