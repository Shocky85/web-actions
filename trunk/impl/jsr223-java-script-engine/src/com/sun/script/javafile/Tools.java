package com.sun.script.javafile;

import javax.script.ScriptException;
import javax.tools.*;
import java.io.*;
import java.util.*;

/**
 * @author Ivan Latysh
 * @version 0.1
 * @since 27-Apr-2007 : 11:37:22 PM
 */
public class Tools {

  /**
   * Do not instantiate this class
   */
  private Tools() {
  }

  /**
   * Look for a file in the scripts source directory recursively
   *
   * @param root root directory
   * @param fileName script file name
   * @return script file
   * @throws java.io.FileNotFoundException when script file not found
   */
  public static File findFile(final File root, final String fileName) throws FileNotFoundException {

    // list files with script name
    File[] files = root.listFiles(new FilenameFilter(){
      public boolean accept(File dir, String name) {
        return fileName.equals(name);
      }
    });

    // return script file if found
    if (files.length>0) return files[0];

    // recurse into subdirectories when no file with given name found in this dir

    // list dirs
    File[] dirs = root.listFiles(new FileFilter(){
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    });
    // check all dirs
    for (File dir : dirs) {
      try {
        // return if file found
        return findFile(dir, fileName);
      } catch (FileNotFoundException ex) {
        // ommit it
      }
    }

    // script not found
    throw new FileNotFoundException("File {"+fileName+"} not found in {"+root.getAbsolutePath()+"}.");
  }

  /**
   * Return fully qualified script class name from script source file
   *
   * @param root root directory
   * @param script script name
   * @return fully qualified class name
   */
  public static String getFullyQualifiedClassName(File root, File script) {
    String relativeURI = root.toURI().relativize(script.toURI()).toString();
    relativeURI = relativeURI.substring(0, relativeURI.lastIndexOf("."));
    return relativeURI.replaceAll("/", ".");
  }

  /**
   * Compile given script file
   *
   * @param script script file to compile
   * @param sourceRoot root directoy where script source files are located
   * @param classesRoot root directory where to comile the script file
   * @param err where to write errors if any
   * @param classpath compile classpath
   * @throws java.io.IOException when error occured
   */
  public static void compile(File[] script, File sourceRoot, File classesRoot, Writer err, Iterable<? extends File> classpath) throws IOException{
    // get Java Compiler
    JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
    // check if compiler available
    if (null==tool) throw new IllegalStateException("Java compiler is not available. \n"+getJavaInfo());
    // get standart file manager
    StandardJavaFileManager stdManager = tool.getStandardFileManager(null, Locale.getDefault(), null);
    try {
      // to collect errors, warnings etc.
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

      // set output location
      stdManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(classesRoot));
      // set source path
      stdManager.setLocation(StandardLocation.SOURCE_PATH, Collections.singleton(sourceRoot));
      // Append classpath if given
      if (null!=classpath) {
        // set class path
        ArrayList<File> clp = new ArrayList<File>();
        // copy existent classpath
        for (File a_classpath : stdManager.getLocation(StandardLocation.CLASS_PATH)) {
          clp.add(a_classpath);
        }
        // copy given classpath
        for (File a_classpath : classpath) {
          clp.add(a_classpath);
        }
        stdManager.setLocation(StandardLocation.CLASS_PATH, clp);
      }

      // prepare the compilation unit
      List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
      // add file objects
      for (JavaFileObject fobj :stdManager.getJavaFileObjects(script)) {
        compUnits.add(fobj);
      }

      // javac options
      List<String> options = new ArrayList<String>();
      options.add("-Xlint:all");
      options.add("-g:none");
      options.add("-deprecation");

      // create a compilation task
      javax.tools.JavaCompiler.CompilationTask task = tool.getTask(err, stdManager, diagnostics, options, null, compUnits);

      // execute compilation
      if (!task.call()) {
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
          err.write("JavaCompiler: "+diagnostic.getMessage(null)+"\n");
        }
      }
    } finally {
      if (null!=stdManager) {
        try {
          stdManager.close();
        } catch (Exception exp) {
          exp.printStackTrace();
        }
      }
    }
  }

  /**
   * read a Reader fully and return the content as string
   *
   * @param reader reader
   * @return read string
   * @throws javax.script.ScriptException wrapped IOException
   */
  public static String readFully(Reader reader) throws ScriptException {
    // 255b at a time, script filename will not be longer than 255 bytes
    char[] arr = new char[255];
    StringBuilder buf = new StringBuilder();
    int numChars;
    try {
      while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
        buf.append(arr, 0, numChars);
      }
    } catch (IOException exp) {
      throw new ScriptException(exp);
    }
    return buf.toString();
  }

  /**
   * Return java info
   *
   * @return java info
   */
  private static String getJavaInfo() {
    return  new StringBuilder()
      .append("=========================================================").append("\n")
      .append("Java Version: ").append(System.getProperty("java.version")).append("\n")
      .append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n")
      .append("Java VM Version: ").append(System.getProperty("java.vm.version")).append("\n")
      .append("Java VM Vendor: ").append(System.getProperty("java.vm.vendor")).append("\n")
      .append("Operating System name: ").append(System.getProperty("os.name")).append("\n")
      .append("Operating system architecture: ").append(System.getProperty("os.arch")).append("\n")
      .append("Operating system version: ").append(System.getProperty("os.version")).append("\n")
      .append("=========================================================")
      .toString();
  }
}
