package uk.co.markberridge.dropwizard.cluster.sample;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class OverrideConfig {

    private static final Logger log = LoggerFactory.getLogger(OverrideConfig.class);

    private final String name;

    public OverrideConfig(String fileName) {
        this("", fileName);
    }

    public OverrideConfig(String dirLocation, String fileName) {
        Preconditions.checkNotNull(dirLocation, "dirLocation is null");
        Preconditions.checkNotNull(fileName, "fileName is null");
        Preconditions.checkArgument(fileName.endsWith(".yml"), "fileName must end with .yml");

        String dir = dirLocation.isEmpty() || dirLocation.endsWith("/") ? dirLocation : dirLocation + "/";

        // Look for username override file
        String u = System.getProperty("user.name");
        File overrideFile = new File(dir + u + '-' + fileName);
        log.warn("looking for override file :" + overrideFile.getAbsolutePath());

        if (overrideFile.exists()) {
            this.name = dir + overrideFile.getName();
            log.warn("Found configuration override file {}", name);
        } else {
            this.name = dir + fileName;
        }
    }

    public String getName() {
        return name;
    }
}
