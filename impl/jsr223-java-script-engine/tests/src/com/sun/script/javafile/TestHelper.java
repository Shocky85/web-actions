package com.sun.script.javafile;

import java.io.File;

/**
 * @author Ivan
 * @version 0.1
 * @since 17-Mar-2007 : 2:19:43 PM
 */
public class TestHelper {

  // Deletes all files and subdirectories under dir.
  // Returns true if all deletions were successful.
  // If a deletion fails, the method stops attempting to delete and returns
  // false.
  public static boolean deleteDir(File dir) {
    if ( dir.isDirectory() ) {
      String[] children = dir.list();
      for (String aChildren : children) {
        boolean success = deleteDir(new File(dir, aChildren));
        if (!success) {
          return false;
        }
      }
    }
    // The directory is now empty so delete it
    return dir.delete();
  }

}

