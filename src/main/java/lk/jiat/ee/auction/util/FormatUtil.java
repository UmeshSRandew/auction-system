package lk.jiat.ee.auction.util;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

/**
 * Utility class for formatting dates, times, and currency values
 */
public class FormatUtil {
    
    // Date and time formatters
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Currency formatter for Sri Lankan Rupee
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
    
    static {
        CURRENCY_FORMATTER.setCurrency(Currency.getInstance("LKR"));
    }
    
    /**
     * Format LocalDateTime to display format
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Format LocalDateTime to ISO format for database storage
     */
    public static String formatDateTimeForDB(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(ISO_FORMATTER);
    }
    
    /**
     * Parse ISO formatted date string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
    }
    
    /**
     * Format currency value
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMATTER.format(amount);
    }
} 