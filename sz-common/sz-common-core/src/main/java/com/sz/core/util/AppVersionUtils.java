package com.sz.core.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class AppVersionUtils {

    private static final String PROJECT_VERSION_TOKEN = "@project.version@";

    private static final String REVISION_START = "<revision>";

    private static final String REVISION_END = "</revision>";

    private AppVersionUtils() {
    }

    public static String resolve(String configuredVersion, Class<?> applicationClass) {
        if (isResolved(configuredVersion)) {
            return configuredVersion.trim();
        }
        return implementationVersion(applicationClass).or(AppVersionUtils::projectPomRevision)
                .orElseGet(() -> StringUtils.defaultIfBlank(configuredVersion, "unknown"));
    }

    private static boolean isResolved(String version) {
        return StringUtils.isNotBlank(version) && !StringUtils.contains(version, PROJECT_VERSION_TOKEN);
    }

    private static Optional<String> implementationVersion(Class<?> applicationClass) {
        return Optional.ofNullable(applicationClass).map(Class::getPackage).map(Package::getImplementationVersion).filter(StringUtils::isNotBlank);
    }

    private static Optional<String> projectPomRevision() {
        return searchFrom(Path.of(StringUtils.defaultIfBlank(System.getProperty("user.dir"), "."))).or(AppVersionUtils::searchFromClasspath);
    }

    private static Optional<String> searchFromClasspath() {
        try {
            URI location = Objects.requireNonNull(AppVersionUtils.class.getProtectionDomain().getCodeSource()).getLocation().toURI();
            return searchFrom(Path.of(location));
        } catch (NullPointerException | URISyntaxException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private static Optional<String> searchFrom(Path start) {
        Path directory = Files.isDirectory(start) ? start : start.getParent();
        if (directory == null) {
            return Optional.empty();
        }
        directory = directory.toAbsolutePath().normalize();
        while (directory != null) {
            Optional<String> revision = readRevision(directory.resolve("pom.xml"));
            if (revision.isPresent()) {
                return revision;
            }
            directory = directory.getParent();
        }
        return Optional.empty();
    }

    private static Optional<String> readRevision(Path pom) {
        if (!Files.isRegularFile(pom)) {
            return Optional.empty();
        }
        try {
            return Files.readAllLines(pom).stream().map(String::trim).filter(line -> line.startsWith(REVISION_START) && line.endsWith(REVISION_END))
                    .map(line -> line.substring(REVISION_START.length(), line.length() - REVISION_END.length()).trim()).filter(StringUtils::isNotBlank)
                    .findFirst();
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

}
