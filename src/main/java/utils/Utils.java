package utils;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    // define various date formats
    public static SimpleDateFormat date12TimeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
    public static SimpleDateFormat date24TimeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dayMonthDateFormat = new SimpleDateFormat("dd.MM");
    public static SimpleDateFormat dayMonthYearDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat monthDayYearDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    public static SimpleDateFormat monthDayYearDateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    /**
     * Gets the unique values with the same key from a list of JSON objects.
     *
     * @param jsonList the list of <code>JsonObject</code>
     * @param key      the string to search for in the list
     * @return the list of unique values
     */
    public static List<String> getUniqueItems(List<JsonObject> jsonList, String key) {
        Set<String> set = new HashSet<>();
        for (JsonObject o : jsonList) {
            set.add(o.get(key).getAsString());
        }
        return new ArrayList<>(set);
    }

    /**
     * Gets all objects of the list which have the specified value at the specified
     * key.
     *
     * @param jsonList the list of <code>JsonObject</code>
     * @param key      the key to consider
     * @param value    the value the key must have
     * @return the list of <code>JsonObject</code>
     */
    public static List<JsonObject> getItemsWithValue(List<JsonObject> jsonList, String key, String value) {
        List<JsonObject> items = new ArrayList<>();
        for (JsonObject o : jsonList) {
            if (o.get(key).getAsString().equals(value)) {
                items.add(o);
            }
        }
        return items;
    }

    /**
     * Gets all values with the same key from a list of JSON objects.
     *
     * @param jsonList the list of <code>JsonObject</code>
     * @param key      the string to search for in the list
     * @return the list of values
     */
    public static List<String> getItems(List<JsonObject> jsonList, String key) {
        List<String> items = new ArrayList<>();
        for (JsonObject o : jsonList) {
            items.add(o.get(key).getAsString());
        }
        return items;
    }

    /**
     * Creates a mapping from key1 to key2 for all items contained in the list of
     * <code>JsonObject</code>. Sorts the map by key at the end.
     *
     * @param jsonList the list of <code>JsonObject</code>
     * @param jsonKey1 first key to extract
     * @param jsonKey2 second key to extract
     * @return the <code>Map</code> from key1 to key2
     */
    public static Map<String, String> mapKeyToAnotherKeyFromJsonList(List<JsonObject> jsonList, String jsonKey1,
                                                                     String jsonKey2) {
        Map<String, String> key1ToKey2 = new HashMap<>();
        // iterate over every all json objects
        for (JsonObject json : jsonList) {
            String value1 = json.get(jsonKey1).getAsString();
            String value2 = json.get(jsonKey2).getAsString();
            key1ToKey2.put(value1, value2);
        }
        return key1ToKey2;
    }

    /**
     * Converts a given String in 12 hour format to another String in 24 hour
     * format.
     *
     * @param time the string with format <b>hh:mm:ss a</b>. If time is not in the
     *             correct format, an exception occurs and <code>null</code> is
     *             returned.
     * @return the string with format <b>HH:mm</b>
     */
    public static String convertTime12To24(String time) {
        String converted = null;
        if (time != null) {
            try {
                converted = date24TimeFormat.format(date12TimeFormat.parse(time));
            } catch (ParseException e) {
                logger.log(Level.FINE, "%s parse failed, thus returning null.", time);
            }
        }
        return converted;
    }

    /**
     * Converts the given string into a <code>Date</code> object.
     *
     * @param date       the string containing a date. If date is not in the correct
     *                   format, an exception occurs and <code>null</code> is
     *                   returned.
     * @param dateFormat the format the string must have
     * @return the <code>Date</code> object
     */
    public static Date parseStringToDate(String date, SimpleDateFormat dateFormat) {
        Date outputDate = null;
        if (date != null && dateFormat != null) {
            try {
                outputDate = dateFormat.parse(date);
            } catch (ParseException e) {
                logger.log(Level.FINE, "%s parse failed, thus returning null.", date);
            }
        }
        return outputDate;
    }

    /**
     * Converts the given list of strings into a list of dates.
     *
     * @param dates      the list of strings, each containing a date. If a string is
     *                   not in the correct format, <code>null</code> is added to
     *                   the list.
     * @param dateFormat the format the strings in the list must have
     * @return the list of <code>Date</code> objects
     */
    public static List<Date> parseListOfStringsToDates(List<String> dates, SimpleDateFormat dateFormat) {
        List<Date> newDates = new ArrayList<>();
        if (dates != null && dateFormat != null) {
            for (String d : dates) {
                Date parsedDate = parseStringToDate(d, dateFormat);
                if (parsedDate != null) {
                    newDates.add(parsedDate);
                }
            }
        }
        return newDates;
    }

    /**
     * Checks if the date is in the past compared to today's date and time.
     *
     * @param date the <code>Date</code> object to check
     * @return <code>true</code> if date is in the past; <code>false</code>
     * otherwise
     */
    public static boolean isPastDate(Date date) {
        Date todaysDate = new Date();
        return date.compareTo(todaysDate) < 0;
    }

    /**
     * Checks if the string is a valid email address, that is it has the correct
     * format.
     *
     * @param email the string to check
     * @return <code>true</code> if string is a valid email address;
     * <code>false</code> otherwise
     */
    public static boolean isValidEmailAddress(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || email.isEmpty()) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /**
     * Checks if the string contains only alpha characters, that is 1 or more
     * Unicode letters, diacritics, whitespaces, apostrophes or hyphens.
     *
     * @param input the string to check
     * @return <code>true</code> if string contains only letters; <code>false</code>
     * otherwise
     */
    public static boolean isAlpha(String input) {
        if (input == null) {
            return false;
        }
        // from
        // https://stackoverflow.com/questions/40764681/java-regex-adding-umlaut-and-other-german-characters-for-first-name
        return input.matches("(?U)[\\p{L}\\p{M}\\s'-]+");
    }

    /**
     * Checks if the string is valid as a password, that means the string is not
     * null or empty and longer or equal to 8 characters.
     *
     * @param password the string to check
     * @return <code>true</code> if string is a valid password; <code>false</code>
     * otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

}
