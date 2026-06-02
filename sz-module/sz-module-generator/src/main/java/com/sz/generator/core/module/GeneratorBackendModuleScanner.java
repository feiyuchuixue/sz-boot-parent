package com.sz.generator.core.module;

import com.sz.generator.pojo.property.GeneratorProperties;
import com.sz.generator.pojo.vo.GeneratorBackendModuleOptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Scans backend modules that can be used as code generation targets.
 */
@Component
@RequiredArgsConstructor
public class GeneratorBackendModuleScanner {

    public static final String STATUS_READY = "ready";

    public static final String STATUS_PENDING = "pending";

    public static final String STATUS_UNAVAILABLE = "unavailable";

    private static final Pattern PREFIX_PATTERN = Pattern.compile("return\\s+\"(/[^\"]*)\"");

    private static final Pattern MODULE_PATTERN = Pattern.compile("module\\(\\)\\s*\\{\\s*return\\s+\"([^\"]+)\"", Pattern.DOTALL);

    private static final Pattern BASE_PACKAGE_PATTERN = Pattern.compile("new\\s+String\\[\\]\\s*\\{\\s*\"([^\"]+)\"");

    private static final Set<String> EXCLUDED_MODULES = Set.of("sz-module-common", "sz-module-generator");

    private final GeneratorProperties generatorProperties;

    public List<GeneratorBackendModuleOptionVO> scan(Path projectRoot) {
        Path moduleRoot = projectRoot.resolve(generatorProperties.getModuleName()).normalize();
        if (!Files.isDirectory(moduleRoot)) {
            return List.of();
        }
        Path rootPom = projectRoot.resolve("pom.xml");
        Path modulePom = moduleRoot.resolve("pom.xml");
        Path serviceRoot = resolveServiceRoot(projectRoot);
        Path servicePom = serviceRoot.resolve("pom.xml");
        Path serviceChangelog = serviceRoot.resolve("src/main/resources/db/changelog/changelog-master.xml");
        Path serviceApplication = serviceRoot.resolve("src/main/resources/application.yml");

        List<GeneratorBackendModuleOptionVO> options = new ArrayList<>();
        try (Stream<Path> stream = Files.list(moduleRoot)) {
            stream.filter(Files::isDirectory)
                    .filter(path -> isGeneratableModule(path.getFileName().toString()))
                    .filter(path -> Files.isRegularFile(path.resolve("pom.xml")))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(path -> buildOption(projectRoot, path, rootPom, modulePom, servicePom, serviceChangelog, serviceApplication))
                    .forEach(options::add);
        } catch (IOException ignored) {
            return List.of();
        }

        options.stream().filter(option -> STATUS_READY.equals(option.getStatus())).findFirst().ifPresent(option -> option.setRecommended(true));
        return options;
    }

    private static boolean isGeneratableModule(String moduleName) {
        return moduleName.startsWith("sz-module-") && !EXCLUDED_MODULES.contains(moduleName);
    }

    public Optional<GeneratorBackendModuleOptionVO> findByModuleName(Path projectRoot, String moduleName) {
        if (moduleName == null || moduleName.isBlank()) {
            return Optional.empty();
        }
        return scan(projectRoot).stream().filter(option -> moduleName.equals(option.getModuleName())).findFirst();
    }

    public GeneratorBackendModuleOptionVO buildNewModuleOption(Path projectRoot, String moduleName, String moduleCode, String modulePath) {
        String normalizedCode = normalizeModuleCode(moduleCode);
        String normalizedModuleName = normalizeModuleName(moduleName, normalizedCode);
        GeneratorBackendModuleOptionVO option = new GeneratorBackendModuleOptionVO();
        option.setModuleName(normalizedModuleName);
        option.setModuleCode(normalizedCode);
        option.setPath(resolveNewModulePath(projectRoot, normalizedModuleName, modulePath).toString());
        option.setPackageName(defaultPackageName(normalizedCode));
        option.setApiPrefixModule(normalizedCode);
        option.setApiPrefix("/" + normalizedCode);
        option.setStatus(STATUS_PENDING);
        option.getMissingItems().add("module-directory");
        option.getMissingItems().add("module-pom");
        option.getMissingItems().add("dependency-management");
        option.getMissingItems().add("service-dependency");
        option.getMissingItems().add("api-prefix");
        option.getMissingItems().add("mapper-scan");
        option.getMissingItems().add("liquibase-include");
        return option;
    }

    private GeneratorBackendModuleOptionVO buildOption(Path projectRoot, Path modulePath, Path rootPom, Path modulePom, Path servicePom, Path serviceChangelog,
            Path serviceApplication) {
        String artifactId = modulePath.getFileName().toString();
        String moduleCode = normalizeModuleCode(artifactId.replaceFirst("^sz-module-", ""));
        String rootText = readString(rootPom);
        String moduleText = readString(modulePom);
        String serviceText = readString(servicePom);
        String changelogText = readString(serviceChangelog);
        String moduleSourceText = readModuleJavaSource(modulePath);
        String apiPrefixSourceText = readMatchingJavaSource(modulePath, "ApiPrefixRegister");
        Properties apiPrefixProperties = readYamlProperties(serviceApplication);
        String apiPrefixModule = resolveApiPrefixModule(apiPrefixSourceText, moduleCode);

        GeneratorBackendModuleOptionVO option = new GeneratorBackendModuleOptionVO();
        option.setModuleName(artifactId);
        option.setModuleCode(moduleCode);
        option.setPath(modulePath.normalize().toString());
        option.setPackageName(resolvePackageName(apiPrefixSourceText, moduleCode));
        option.setApiPrefixModule(apiPrefixModule);
        option.setApiPrefix(resolveEffectiveApiPrefix(apiPrefixProperties, apiPrefixModule, apiPrefixSourceText, moduleCode));

        if (!moduleText.contains("<module>" + artifactId + "</module>")) {
            option.getMissingItems().add("parent-module");
        }
        if (!rootText.contains("<artifactId>" + artifactId + "</artifactId>")) {
            option.getMissingItems().add("dependency-management");
        }
        if (!serviceText.contains("<artifactId>" + artifactId + "</artifactId>")) {
            option.getMissingItems().add("service-dependency");
        }
        if (!moduleSourceText.contains("ApiPrefixRegister")) {
            option.getMissingItems().add("api-prefix");
        }
        if (!moduleSourceText.contains("@MapperScan")) {
            option.getMissingItems().add("mapper-scan");
        }
        if (!hasModuleChangelog(modulePath, moduleCode, changelogText)) {
            option.getMissingItems().add("liquibase-include");
        }
        option.setStatus(option.getMissingItems().isEmpty() ? STATUS_READY : STATUS_PENDING);
        return option;
    }

    private static String readModuleJavaSource(Path modulePath) {
        return readMatchingJavaSource(modulePath, null);
    }

    private static String readMatchingJavaSource(Path modulePath, String requiredText) {
        Path javaRoot = modulePath.resolve("src/main/java");
        if (!Files.isDirectory(javaRoot)) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        try (Stream<Path> stream = Files.walk(javaRoot)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".java")).forEach(path -> {
                String source = readString(path);
                if (requiredText == null || source.contains(requiredText)) {
                    text.append(source).append('\n');
                }
            });
        } catch (IOException ignored) {
            return text.toString();
        }
        return text.toString();
    }

    private static boolean hasModuleChangelog(Path modulePath, String moduleCode, String serviceChangelogText) {
        String changelogFile = "module-" + moduleCode + "-changelog.xml";
        return Files.isRegularFile(modulePath.resolve("src/main/resources/db/changelog").resolve(changelogFile))
                && serviceChangelogText.contains(changelogFile);
    }

    private static String resolvePackageName(String moduleSourceText, String moduleCode) {
        Matcher matcher = BASE_PACKAGE_PATTERN.matcher(moduleSourceText);
        if (!matcher.find()) {
            return defaultPackageName(moduleCode);
        }
        String basePackage = matcher.group(1);
        int controllerIndex = basePackage.indexOf(".controller");
        if (controllerIndex > 0) {
            return basePackage.substring(0, controllerIndex);
        }
        String[] segments = basePackage.split("\\.");
        if (segments.length >= 3) {
            return segments[0] + "." + segments[1] + "." + segments[2];
        }
        return basePackage;
    }

    private static String resolveApiPrefixModule(String moduleSourceText, String moduleCode) {
        Matcher matcher = MODULE_PATTERN.matcher(moduleSourceText);
        return matcher.find() ? matcher.group(1) : moduleCode;
    }

    private static String resolveApiPrefix(String moduleSourceText, String moduleCode) {
        Matcher matcher = PREFIX_PATTERN.matcher(moduleSourceText);
        return matcher.find() ? matcher.group(1) : "/" + moduleCode;
    }

    private static String resolveEffectiveApiPrefix(Properties apiPrefixProperties, String apiPrefixModule, String moduleSourceText, String moduleCode) {
        String configuredPrefix = apiPrefixProperties.getProperty("sz.api-prefix.modules." + apiPrefixModule + ".prefix");
        if (configuredPrefix != null && !configuredPrefix.isBlank()) {
            return normalizeApiPrefix(configuredPrefix);
        }
        return normalizeApiPrefix(resolveApiPrefix(moduleSourceText, moduleCode));
    }

    private static String normalizeApiPrefix(String apiPrefix) {
        if (apiPrefix == null || apiPrefix.isBlank()) {
            return "";
        }
        String normalized = apiPrefix.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        while (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static Properties readYamlProperties(Path path) {
        if (!Files.isRegularFile(path)) {
            return new Properties();
        }
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new FileSystemResource(path));
            Properties properties = factory.getObject();
            return properties == null ? new Properties() : properties;
        } catch (Exception ignored) {
            return new Properties();
        }
    }

    private static String defaultPackageName(String moduleCode) {
        return "com.sz." + normalizeModuleCode(moduleCode).replace("-", "");
    }

    private static String normalizeModuleCode(String moduleCode) {
        return moduleCode == null || moduleCode.isBlank() ? "demo"
                : moduleCode.trim().replaceFirst("^sz-module-", "").replace("_", "-").toLowerCase(Locale.ROOT);
    }

    private static String normalizeModuleName(String moduleName, String moduleCode) {
        return moduleName == null || moduleName.isBlank() ? "sz-module-" + moduleCode : moduleName.trim();
    }

    private Path resolveNewModulePath(Path projectRoot, String moduleName, String modulePath) {
        if (modulePath != null && !modulePath.isBlank()) {
            return Path.of(modulePath).normalize();
        }
        return projectRoot.resolve(generatorProperties.getModuleName()).resolve(moduleName).normalize();
    }

    private static Path resolveServiceRoot(Path projectRoot) {
        Path defaultService = projectRoot.resolve("sz-service").resolve("sz-service-admin").normalize();
        if (Files.isRegularFile(defaultService.resolve("pom.xml"))) {
            return defaultService;
        }
        Path serviceParent = projectRoot.resolve("sz-service").normalize();
        if (!Files.isDirectory(serviceParent)) {
            return defaultService;
        }
        try (Stream<Path> stream = Files.list(serviceParent)) {
            return stream.filter(Files::isDirectory)
                    .filter(path -> Files.isRegularFile(path.resolve("pom.xml")))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .findFirst()
                    .orElse(defaultService);
        } catch (IOException ignored) {
            return defaultService;
        }
    }

    private static String readString(Path path) {
        if (!Files.isRegularFile(path)) {
            return "";
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }
}
