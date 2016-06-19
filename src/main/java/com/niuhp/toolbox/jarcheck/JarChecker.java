/**
 *
 */
package com.niuhp.toolbox.jarcheck;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niuhp on 2016/4/13.
 */
public class JarChecker {

  private static final Pattern versionPattern = Pattern.compile("-\\d+\\.");
  private static final Pattern jarPattern = Pattern.compile("\\.jar");

  public static Map<String, List<JarConfig>> checkConflict(String path) {
    List<JarConfig> allJars = getAllJars(path);
    Map<String, List<JarConfig>> jarMap = toMap(allJars);
    Set<String> jarNames = jarMap.keySet();
    for (String jarName : jarNames) {
      List<JarConfig> jars = jarMap.get(jarName);
      if (jars == null || jars.size() <= 1) {
        jarMap.remove(jarName);
      }
    }
    return jarMap;
  }

  public static Map<String, List<JarConfig>> checkConflict(String path1, String path2) {
    List<JarConfig> allJars1 = getAllJars(path1);
    List<JarConfig> allJars2 = getAllJars(path2);
    Map<String, List<JarConfig>> jarMap1 = toMap(allJars1);
    Map<String, List<JarConfig>> jarMap2 = toMap(allJars2);
    Map<String, List<JarConfig>> jarMap = new HashMap<String, List<JarConfig>>();

    Set<String> jarNames = jarMap1.keySet();
    for (String jarName : jarNames) {
      List<JarConfig> jars1 = jarMap1.get(jarName);
      if (jars1 == null || jars1.isEmpty()) {
        continue;
      }
      List<JarConfig> jars2 = jarMap2.get(jarName);
      if (jars2 == null || jars2.isEmpty()) {
        continue;
      }
      List<JarConfig> jars = new ArrayList<JarConfig>();
      jars.addAll(jars1);
      jars.addAll(jars2);
      jarMap.put(jarName, jars);
    }
    return jarMap;
  }

  public static List<JarConfig> getAllJars(String path) {
    List<JarConfig> jars = new ArrayList<JarConfig>();
    File file = new File(path);
    saveJars(file, jars);
    Collections.sort(jars, new JarConfigComparator());
    return jars;
  }

  private static Map<String, List<JarConfig>> toMap(List<JarConfig> allJars) {
    Map<String, List<JarConfig>> jarMap = new HashMap<String, List<JarConfig>>();
    for (JarConfig jar : allJars) {
      String jarName = jar.getJarName();
      if (jarMap.containsKey(jarName)) {
        List<JarConfig> jars = jarMap.get(jarName);
        jars.add(jar);
      } else {
        List<JarConfig> jars = new ArrayList<JarConfig>();
        jars.add(jar);
        jarMap.put(jarName, jars);
      }
    }
    return jarMap;
  }

  private static void saveJars(File file, List<JarConfig> jars) {
    if (file == null || !file.exists()) {
      return;
    } else if (file.isFile()) {
      JarConfig jarConfig = getJarConfig(file);
      if (jarConfig == null) {
        return;
      }
      jars.add(jarConfig);
    } else if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files == null || files.length == 0) {
        return;
      }
      for (File f : files) {
        saveJars(f, jars);
      }
    }
  }

  private static JarConfig getJarConfig(File file) {
    if (file == null || !file.isFile()) {
      return null;
    }
    String jarName = null;
    String jarVersion = "";
    String fileName = file.getName();
    String jarPath = file.getAbsolutePath();

    int versionIndex = -1;
    int typeIndex = -1;

    Matcher matcher = versionPattern.matcher(fileName);
    if (matcher.find()) {
      versionIndex = matcher.start();
    }

    matcher = jarPattern.matcher(fileName);
    if (matcher.find()) {
      typeIndex = matcher.start();
    }
    if (typeIndex == -1) {
      Logger.getLogger(JarChecker.class).warn(String.format("file:%s is not a jar file", jarPath));
      return null;
    }
    if (versionIndex != -1) {
      jarName = fileName.substring(0, versionIndex);
      jarVersion = fileName.substring(versionIndex + 1, typeIndex);
    } else {
      jarName = fileName.substring(0, typeIndex);
    }
    return new JarConfig(jarName, jarVersion, jarPath);
  }
}
