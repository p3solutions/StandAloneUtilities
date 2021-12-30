package com.p3.splitter.utils;
/*
 * $$HeadURL$$
 * $$Id$$
 *
 * CCopyright (c) 2015, P3Solutions . All Rights Reserved.
 * This code may not be used without the express written permission
 * of the copyright holder, P3Solutions.
 */
/**
 *  Class: GeneralUtil
 *
 *  This class is a General Util class.
 *
 *@author     Malik
 *@version    1.0
 *
 */
public class GeneralUtil {


    /**
     * Does this string have a value. true or false. Checks for null or empty
     * string
     *
     * @param s
     *            the string to check for a value
     * @return true or false.
     */
    public static boolean hasValue(String s) {

        return (s != null) && (s.trim().length() > 0);
    }
    
    
    /**
     * Get the File Extension from a filename
     * 
     * @param filename   the filename
     * 
     * @return the file extension
     */
    public static String getFileExtension( String filename ) {
        if (! GeneralUtil.hasValue(filename)) return null;
        int index = filename.lastIndexOf('.');
        if (index == -1) return null;
        return  filename.substring(index +1 ,filename.length());
    }

}
