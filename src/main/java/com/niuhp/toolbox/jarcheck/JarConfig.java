/**
 *
 */
package com.niuhp.toolbox.jarcheck;

/**
 * Created by niuhp on 2016/4/13.
 */
public class JarConfig {

    private String jarName;
    private String jarVersion;
    private String jarPath;

    public JarConfig(String jarName, String jarPath) {
        this(jarName, "", jarPath);
    }

    public JarConfig(String jarName, String jarVersion, String jarPath) {
        this.jarName = jarName;
        this.jarVersion = jarVersion;
        this.jarPath = jarPath;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public void setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public boolean isNormal() {
        return jarPath != null && jarPath.toLowerCase().endsWith(".jar");
    }

    @Override
    public String toString() {
        return "JarConfig [jarName=" + jarName + ", jarVersion=" + jarVersion + ", jarPath=" + jarPath + "]";
    }
}
