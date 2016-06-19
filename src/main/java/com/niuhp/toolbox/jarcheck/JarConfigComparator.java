/**
 *
 */
package com.niuhp.toolbox.jarcheck;

import java.util.Comparator;

/**
 * Created by niuhp on 2016/4/13.
 */
public class JarConfigComparator implements Comparator<JarConfig> {

  @Override
  public int compare(JarConfig o1, JarConfig o2) {
    if (o1 == o2) {
      return 0;
    } else if (o1 == null) {
      return -1;
    } else if (o2 == null) {
      return 1;
    } else {
      int result = o1.getJarName().compareTo(o2.getJarName());
      if (result == 0) {
        result = o1.getJarVersion().compareTo(o2.getJarVersion());
      }
      if (result == 0) {
        result = o1.getJarPath().compareTo(o2.getJarPath());
      }
      return result;
    }
  }

}
