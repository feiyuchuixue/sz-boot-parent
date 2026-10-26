import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;

import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Generates self-contained configuration entry schemas from JavaDoc and declarations.
 *
 * <p>This is intentionally a dependency-free JDK source-file program so CI can run it before Maven.</p>
 */
public class OssSchemaGenerator {

    private static final Path OSS_OUTPUT = Path.of("schemas/config/oss.schema.json");
    private static final Path APPLICATION_OUTPUT = Path.of("schemas/config/application.schema.json");
    private static final List<Path> OUTPUTS = List.of(OSS_OUTPUT, APPLICATION_OUTPUT);
    private static final List<SourceSpec> CONFIG_SOURCES = List.of(
            new SourceSpec("sz-common/sz-common-oss/src/main/java/com/sz/oss/OssProperties.java", "OssProperties",
                    "ossConfig", "S3 兼容对象存储的全局连接与访问地址生成配置。", "sz.oss"),
            new SourceSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/config/ResourceProperties.java", "ResourceProperties",
                    "resourceConfig", "统一资源管理配置。", "sz.resource"),
            new SourceSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/config/ResourceSceneConfig.java", "ResourceSceneConfig",
                    "resourceScene", "单个资源场景配置。", null),
            new SourceSpec("sz-common/sz-common-core/src/main/java/com/sz/core/common/web/ApiPrefixProperties.java", "ApiPrefixProperties",
                    "apiPrefixConfig", "模块 API 路径前缀配置。", "sz.api-prefix"),
            new SourceSpec("sz-common/sz-common-db-core/src/main/java/com/sz/db/DataScopeProperties.java", "DataScopeProperties",
                    "dataScopeConfig", "数据权限全局配置。", "sz.data-scope"),
            new SourceSpec("sz-common/sz-common-security/src/main/java/com/sz/security/debounce/DebounceProperties.java", "DebounceProperties",
                    "debounceConfig", "接口防抖配置。", "sz.debounce"),
            new SourceSpec("sz-common/sz-common-security/src/main/java/com/sz/security/core/CorsProperties.java", "CorsProperties",
                    "corsConfig", "后台服务跨域 Origin 配置。", "sz.cors"),
            new SourceSpec("sz-common/sz-common-wechat/src/main/java/com/sz/wechat/WechatProperties.java", "WechatProperties",
                    "wechatConfig", "微信小程序与企业微信配置。", "sz.wechat"),
            new SourceSpec("sz-module/sz-module-generator/src/main/java/com/sz/generator/pojo/property/GeneratorProperties.java", "GeneratorProperties",
                    "generatorConfig", "代码生成器工程路径与默认配置。", "sz.generator"));
    private static final List<EnumSpec> ENUM_SOURCES = List.of(
            new EnumSpec("sz-common/sz-common-oss/src/main/java/com/sz/oss/OssProviderEnum.java", "OssProviderEnum",
                    "ossProvider", "对象存储服务提供商"),
            new EnumSpec("sz-common/sz-common-oss/src/main/java/com/sz/oss/SchemeEnum.java", "SchemeEnum",
                    "scheme", "访问协议"),
            new EnumSpec("sz-common/sz-common-oss/src/main/java/com/sz/oss/OssAddressingStyle.java", "OssAddressingStyle",
                    "addressingStyle", "S3 寻址风格"),
            new EnumSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/enums/StorageTypeEnum.java", "StorageTypeEnum",
                    "storageType", "资源存储类型"),
            new EnumSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/enums/ServeModeEnum.java", "ServeModeEnum",
                    "serveMode", "资源访问模式"),
            new EnumSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/enums/NamingRuleEnum.java", "NamingRuleEnum",
                    "namingRule", "文件命名规则"),
            new EnumSpec("sz-common/sz-common-resource/src/main/java/com/sz/resource/enums/PathStrategyEnum.java", "PathStrategyEnum",
                    "pathStrategy", "路径分层策略"));
    private static final String JAVA_WHITESPACE = "[\\u0009-\\u000D\\u001C-\\u0020\\u1680\\u2000-\\u2006\\u2008-\\u200A\\u2028\\u2029\\u205F\\u3000]";
    private static final String ENUM_SEPARATOR = "[^0-9A-Za-z\\u0080-\\uFFFF]*";

    public static void main(String[] args) {
        PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PrintStream utf8Err = new PrintStream(System.err, true, StandardCharsets.UTF_8);
        int exitCode = run(args, utf8Out, utf8Err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            Arguments arguments = Arguments.parse(args);
            Path root = arguments.root().toAbsolutePath().normalize();
            Map<Path, String> generated = new OssSchemaGenerator().generate(root);
            if (arguments.mode() == Mode.CHECK) {
                List<Path> stale = generated.entrySet().stream()
                        .filter(entry -> !isCurrent(root.resolve(entry.getKey()), entry.getValue()))
                        .map(Map.Entry::getKey)
                        .toList();
                if (!stale.isEmpty()) {
                    for (Path output : stale) {
                        err.println("配置 Schema 与源码不同步：" + output.toString().replace('\\', '/'));
                    }
                    err.println("请执行：java sz-build/config-schema/OssSchemaGenerator.java --write " + root);
                    return 1;
                }
                out.println("配置 Schema 已与源码同步：" + OUTPUTS.stream()
                        .map(path -> path.toString().replace('\\', '/')).toList());
                return 0;
            }
            writeAllAtomically(root, generated);
            out.println("已生成配置 Schema：" + OUTPUTS.stream().map(root::resolve).toList());
            return 0;
        } catch (GenerationException | IOException exception) {
            err.println("配置 Schema 生成失败：" + exception.getMessage());
            return 1;
        }
    }

    private static boolean isCurrent(Path output, String generated) {
        try {
            return Files.exists(output)
                    && Arrays.equals(Files.readAllBytes(output), generated.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            return false;
        }
    }

    private Map<Path, String> generate(Path root) throws IOException, GenerationException {
        ParsedSources parsed = parseSources(root);
        LinkedHashMap<String, Object> definitions = buildDefinitions(parsed);

        LinkedHashMap<Path, String> outputs = new LinkedHashMap<>();
        outputs.put(OSS_OUTPUT, Json.write(entrySchema(
                "Sz-Admin OSS 与资源配置",
                "为拆分的 oss.yml 提供 sz.oss 与 sz.resource 的字段、类型和候选值提示。跨字段组合及运行时约束仍由 Java 配置校验负责。",
                "ossSzConfig",
                "Sz-Admin 自定义配置命名空间。本 Schema 只细化其中的 oss 与 resource；sz 下其他框架配置仍允许存在。",
                definitions)));
        outputs.put(APPLICATION_OUTPUT, Json.write(entrySchema(
                "Sz-Admin 应用配置",
                "为服务 application.yml 中的 Sz-Admin 自定义配置提供字段、类型、初始化值与建议值提示。Spring 标准配置和导入文件由各自 Schema 负责。",
                "applicationSzConfig",
                "Sz-Admin 自定义配置命名空间。本 Schema 细化 api-prefix、data-scope、debounce、generator、wechat 与 cors；sz 下其他框架配置仍允许存在。",
                definitions)));
        return outputs;
    }

    private LinkedHashMap<String, Object> entrySchema(String title, String description, String definition,
            String propertyDescription, LinkedHashMap<String, Object> definitions) throws GenerationException {
        LinkedHashMap<String, Object> schema = map();
        schema.put("$schema", "http://json-schema.org/draft-07/schema#");
        schema.put("title", title);
        schema.put("description", description);
        schema.put("type", "object");
        schema.put("properties", mapOf("sz", describedReference(definition, propertyDescription)));
        schema.put("additionalProperties", true);
        schema.put("definitions", packageDefinitions(definition, definitions));
        return schema;
    }

    private LinkedHashMap<String, Object> packageDefinitions(String rootDefinition,
            LinkedHashMap<String, Object> definitions) throws GenerationException {
        List<String> pending = new ArrayList<>();
        Set<String> reachable = new HashSet<>();
        pending.add(rootDefinition);
        for (int index = 0; index < pending.size(); index++) {
            String name = pending.get(index);
            if (!reachable.add(name)) {
                continue;
            }
            Object definition = definitions.get(name);
            if (definition == null) {
                throw new GenerationException("Schema definition 引用目标不存在：" + name);
            }
            Set<String> references = new HashSet<>();
            collectDefinitionReferences(definition, references);
            pending.addAll(references);
        }
        LinkedHashMap<String, Object> packaged = map();
        for (Map.Entry<String, Object> entry : definitions.entrySet()) {
            if (reachable.contains(entry.getKey())) {
                packaged.put(entry.getKey(), entry.getValue());
            }
        }
        return packaged;
    }

    private void collectDefinitionReferences(Object value, Set<String> references) throws GenerationException {
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey().equals("$ref")) {
                    if (!(entry.getValue() instanceof String reference)
                            || !reference.matches("^#/definitions/[^/]+$")) {
                        throw new GenerationException("入口打包仅支持内部 definition 引用：" + entry.getValue());
                    }
                    references.add(reference.substring("#/definitions/".length()));
                } else {
                    collectDefinitionReferences(entry.getValue(), references);
                }
            }
        } else if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                collectDefinitionReferences(item, references);
            }
        }
    }

    private LinkedHashMap<String, Object> buildDefinitions(ParsedSources parsed) throws GenerationException {
        LinkedHashMap<String, Object> definitions = map();
        definitions.put("ossSzConfig", objectDefinition(mapOf(
                "oss", describedReference("ossConfig", "S3 兼容对象存储的全局默认连接、签名、寻址风格与访问地址生成配置，供 sz-common-oss 以及未完全覆盖连接的资源场景使用。"),
                "resource", describedReference("resourceConfig", "统一资源管理配置，定义 LOCAL 根目录、上传安全策略和按 code 区分的资源场景。")), true));
        definitions.put("applicationSzConfig", objectDefinition(mapOf(
                "api-prefix", describedReference("apiPrefixConfig", "模块 API 路径前缀配置。"),
                "apiPrefix", describedReference("apiPrefixConfig", "api-prefix 的 camelCase 别名，绑定语义相同。"),
                "data-scope", describedReference("dataScopeConfig", "数据权限全局配置。"),
                "dataScope", describedReference("dataScopeConfig", "data-scope 的 camelCase 别名，绑定语义相同。"),
                "debounce", describedReference("debounceConfig", "接口防抖配置。"),
                "generator", describedReference("generatorConfig", "代码生成器工程路径与默认配置。"),
                "wechat", describedReference("wechatConfig", "微信小程序与企业微信配置。"),
                "cors", describedReference("corsConfig", "后台服务跨域 Origin 配置。")), true));
        for (ConfigClass config : parsed.classes().values()) {
            putDefinition(definitions, config.definitionName(),
                    configDefinition(config.description(), config, parsed.enums()));
        }
        putDefinition(definitions, "sceneList", sceneListDefinition());
        for (ConfigClass config : parsed.classes().values()) {
            for (FieldModel field : config.fields()) {
                if (field.type().startsWith("Map<String,") && !definitions.containsKey(field.referenceDefinition())) {
                    String valueDefinition = field.referenceDefinition()
                            .substring(0, field.referenceDefinition().length() - "Map".length());
                    putDefinition(definitions, field.referenceDefinition(),
                            mapDefinition("字符串键到 " + valueDefinition + " 配置对象的动态映射。", valueDefinition));
                }
            }
        }
        addCommonDefinitions(definitions);
        for (EnumSpec spec : ENUM_SOURCES) {
            putDefinition(definitions, spec.definitionName(), enumDefinition(spec, parsed.enums().get(spec.className())));
        }
        return definitions;
    }

    private ParsedSources parseSources(Path root) throws IOException, GenerationException {
        List<Path> sourcePaths = new ArrayList<>();
        for (SourceSpec source : CONFIG_SOURCES) {
            sourcePaths.add(requireSource(root, source.path()));
        }
        for (EnumSpec source : ENUM_SOURCES) {
            sourcePaths.add(requireSource(root, source.path()));
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new GenerationException("当前 Java 运行时未提供 JDK Compiler API");
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        LinkedHashMap<String, ConfigClass> classes = new LinkedHashMap<>();
        LinkedHashMap<String, EnumModel> enums = new LinkedHashMap<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromPaths(sourcePaths);
            JavacTask task = (JavacTask) compiler.getTask(null, fileManager, diagnostics,
                    List.of("-proc:none", "-encoding", "UTF-8"), null, files);
            List<CompilationUnitTree> units = new ArrayList<>();
            task.parse().forEach(units::add);
            List<Diagnostic<? extends JavaFileObject>> errors = diagnostics.getDiagnostics().stream()
                    .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                    .toList();
            if (!errors.isEmpty()) {
                Diagnostic<? extends JavaFileObject> first = errors.get(0);
                String source = first.getSource() == null ? "unknown" : Path.of(first.getSource().toUri()).getFileName().toString();
                throw new GenerationException("源码解析失败：" + source + ":" + first.getLineNumber() + " "
                        + first.getMessage(Locale.ROOT));
            }
            DocTrees docTrees = DocTrees.instance(task);
            LinkedHashMap<String, CompilationUnitTree> configUnits = new LinkedHashMap<>();
            LinkedHashMap<String, ClassTree> configTrees = new LinkedHashMap<>();
            for (CompilationUnitTree unit : units) {
                for (Tree declaration : unit.getTypeDecls()) {
                    if (!(declaration instanceof ClassTree classTree)) {
                        continue;
                    }
                    String name = classTree.getSimpleName().toString();
                    if (classTree.getKind() == Tree.Kind.ENUM) {
                        EnumSpec spec = enumSpec(name);
                        if (spec != null) {
                            enums.put(name, parseEnum(unit, classTree, docTrees));
                        }
                    } else if (sourceSpec(name) != null) {
                        configUnits.put(name, unit);
                        configTrees.put(name, classTree);
                    }
                }
            }
            LinkedHashMap<String, String> rootDefinitions = new LinkedHashMap<>();
            for (SourceSpec source : CONFIG_SOURCES) {
                ClassTree rootTree = configTrees.get(source.className());
                if (rootTree == null) {
                    throw new GenerationException("缺少配置类：" + source.className());
                }
                validateConfigurationPrefix(rootTree, source);
                putTypeDefinition(rootDefinitions, source.className(), source.definitionName());
            }
            for (SourceSpec source : CONFIG_SOURCES) {
                LinkedHashMap<String, String> typeDefinitions = new LinkedHashMap<>(rootDefinitions);
                collectNestedDefinitions(source, configTrees.get(source.className()), List.of(), typeDefinitions);
                parseConfigClasses(configUnits.get(source.className()), configTrees.get(source.className()), source,
                        List.of(), typeDefinitions, docTrees, classes);
            }
        }
        for (EnumSpec expected : ENUM_SOURCES) {
            if (!enums.containsKey(expected.className())) {
                throw new GenerationException("缺少枚举：" + expected.className());
            }
        }
        for (ConfigClass config : classes.values()) {
            for (FieldModel field : config.fields()) {
                EnumModel enumModel = enums.get(field.type());
                if (enumModel != null && field.defaultValue() != null
                        && enumModel.values().stream().noneMatch(value -> value.value().equals(field.defaultValue()))) {
                    throw new GenerationException("枚举默认值不存在：" + config.name() + "." + field.name() + " -> "
                            + field.defaultValue());
                }
            }
        }
        return new ParsedSources(classes, enums);
    }

    private void validateConfigurationPrefix(ClassTree classTree, SourceSpec source) throws GenerationException {
        if (source.configurationPrefix() == null) {
            return;
        }
        String actual = null;
        for (AnnotationTree annotation : classTree.getModifiers().getAnnotations()) {
            if (!annotation.getAnnotationType().toString().endsWith("ConfigurationProperties")) {
                continue;
            }
            for (ExpressionTree argument : annotation.getArguments()) {
                ExpressionTree value = argument;
                if (argument instanceof AssignmentTree assignment) {
                    String name = assignment.getVariable().toString();
                    if (!(name.equals("prefix") || name.equals("value"))) {
                        continue;
                    }
                    value = assignment.getExpression();
                }
                if (value instanceof com.sun.source.tree.LiteralTree literal
                        && literal.getValue() instanceof String prefix) {
                    actual = prefix;
                }
            }
        }
        if (!source.configurationPrefix().equals(actual)) {
            throw new GenerationException("配置前缀与生成器登记不一致：" + source.className() + " 期望 "
                    + source.configurationPrefix() + "，源码为 " + actual);
        }
    }

    private void collectNestedDefinitions(SourceSpec source, ClassTree owner, List<String> ownerPath,
            Map<String, String> typeDefinitions) throws GenerationException {
        for (Tree member : owner.getMembers()) {
            if (member instanceof ClassTree nested && nested.getKind() == Tree.Kind.CLASS) {
                List<String> path = new ArrayList<>(ownerPath);
                path.add(nested.getSimpleName().toString());
                putTypeDefinition(typeDefinitions, nested.getSimpleName().toString(), nestedDefinitionName(source, path));
                collectNestedDefinitions(source, nested, path, typeDefinitions);
            }
        }
    }

    private void parseConfigClasses(CompilationUnitTree unit, ClassTree classTree, SourceSpec source,
            List<String> classPath, Map<String, String> typeDefinitions, DocTrees docTrees,
            Map<String, ConfigClass> classes) throws GenerationException {
        String definitionName = classPath.isEmpty() ? source.definitionName() : nestedDefinitionName(source, classPath);
        String description = classPath.isEmpty() ? source.description() : nestedDescription(source, classPath);
        ConfigClass parsed = parseClass(unit, classTree, definitionName, description, typeDefinitions, docTrees);
        if (classes.putIfAbsent(definitionName, parsed) != null) {
            throw new GenerationException("配置定义名称冲突：" + definitionName);
        }
        for (Tree member : classTree.getMembers()) {
            if (member instanceof ClassTree nested && nested.getKind() == Tree.Kind.CLASS) {
                List<String> nestedPath = new ArrayList<>(classPath);
                nestedPath.add(nested.getSimpleName().toString());
                parseConfigClasses(unit, nested, source, nestedPath, typeDefinitions, docTrees, classes);
            }
        }
    }

    private void putTypeDefinition(Map<String, String> definitions, String type, String definition)
            throws GenerationException {
        String previous = definitions.putIfAbsent(type, definition);
        if (previous != null && !previous.equals(definition)) {
            throw new GenerationException("嵌套配置类型名称冲突：" + type + " -> " + previous + " / " + definition);
        }
    }

    private String nestedDefinitionName(SourceSpec source, List<String> path) {
        if (source.className().equals("ResourceProperties") && path.equals(List.of("SecurityConfig"))) {
            return "securityConfig";
        }
        StringBuilder name = new StringBuilder(lowerFirst(source.className()));
        path.forEach(name::append);
        return name.toString();
    }

    private String nestedDescription(SourceSpec source, List<String> path) {
        String qualifiedName = source.className() + "." + String.join(".", path);
        return switch (qualifiedName) {
            case "ResourceProperties.SecurityConfig" -> "全局资源上传安全策略。";
            case "ApiPrefixProperties.Module" -> "单个模块的 API 前缀覆盖配置。";
            case "WechatProperties.MiniProgramProperties" -> "微信小程序接口凭据。";
            case "WechatProperties.WorkProgramProperties" -> "企业微信自建应用配置。";
            case "GeneratorProperties.PathProperties" -> "代码生成目标路径配置。";
            case "GeneratorProperties.GlobalProperties" -> "代码生成全局默认配置。";
            case "GeneratorProperties.IgnoreTablePrefix" -> "导入表名的前缀移除规则。";
            default -> "嵌套配置对象 " + qualifiedName + "。";
        };
    }

    private Path requireSource(Path root, String relativePath) throws GenerationException {
        Path path = root.resolve(relativePath).normalize();
        if (!path.startsWith(root) || !Files.isRegularFile(path)) {
            throw new GenerationException("缺少源码文件：" + relativePath);
        }
        return path;
    }

    private ConfigClass parseClass(CompilationUnitTree unit, ClassTree classTree, String definitionName,
            String description, Map<String, String> typeDefinitions, DocTrees docTrees) throws GenerationException {
        List<FieldModel> fields = new ArrayList<>();
        for (Tree member : classTree.getMembers()) {
            if (!(member instanceof VariableTree variable)) {
                continue;
            }
            Set<Modifier> flags = variable.getModifiers().getFlags();
            if (flags.contains(Modifier.STATIC) || flags.contains(Modifier.TRANSIENT)) {
                continue;
            }
            String type = normalizeType(variable.getType().toString());
            String referenceDefinition = ensureKnownType(type, typeDefinitions,
                    classTree.getSimpleName() + "." + variable.getName());
            DocInfo docs = readDocs(unit, variable, docTrees, "字段 " + classTree.getSimpleName() + "." + variable.getName());
            validateExamples(type, docs.examples(), classTree.getSimpleName() + "." + variable.getName());
            Object defaultValue = defaultValue(variable.getInitializer(), type,
                    classTree.getSimpleName() + "." + variable.getName());
            fields.add(new FieldModel(variable.getName().toString(), type, referenceDefinition,
                    docs.description(), docs.examples(), defaultValue));
        }
        if (fields.isEmpty()) {
            throw new GenerationException("配置类没有可绑定字段：" + classTree.getSimpleName());
        }
        return new ConfigClass(classTree.getSimpleName().toString(), definitionName, description, fields);
    }

    private EnumModel parseEnum(CompilationUnitTree unit, ClassTree classTree, DocTrees docTrees) throws GenerationException {
        List<EnumValue> values = new ArrayList<>();
        String enumName = classTree.getSimpleName().toString();
        for (Tree member : classTree.getMembers()) {
            if (!(member instanceof VariableTree variable) || !(variable.getInitializer() instanceof NewClassTree)
                    || variable.getType() == null || !variable.getType().toString().equals(enumName)) {
                continue;
            }
            DocInfo docs = readDocs(unit, variable, docTrees, "枚举常量 " + enumName + "." + variable.getName());
            values.add(new EnumValue(variable.getName().toString(), docs.shortDescription(), docs.description()));
        }
        if (values.isEmpty()) {
            throw new GenerationException("枚举没有常量：" + enumName);
        }
        return new EnumModel(enumName, values);
    }

    private DocInfo readDocs(CompilationUnitTree unit, Tree tree, DocTrees docTrees, String label) throws GenerationException {
        TreePath path = TreePath.getPath(unit, tree);
        DocCommentTree comment = path == null ? null : docTrees.getDocCommentTree(path);
        if (comment == null) {
            throw new GenerationException("缺少 JavaDoc：" + label);
        }
        String body = normalizeDoc(render(comment.getFullBody()));
        String shortDescription = firstSentence(body);
        if (body.isBlank()) {
            throw new GenerationException("缺少 JavaDoc 正文：" + label);
        }
        List<Example> examples = new ArrayList<>();
        for (DocTree tag : comment.getBlockTags()) {
            if (tag instanceof UnknownBlockTagTree unknown && unknown.getTagName().equals("schemaExample")) {
                String valueAndDescription = normalizeDoc(render(unknown.getContent()));
                int separator = valueAndDescription.indexOf('|');
                if (separator <= 0 || separator == valueAndDescription.length() - 1) {
                    throw new GenerationException("非法 @schemaExample（应为 value | 说明）：" + label);
                }
                String value = valueAndDescription.substring(0, separator).trim();
                String description = valueAndDescription.substring(separator + 1).trim();
                if (value.chars().anyMatch(Character::isWhitespace)) {
                    throw new GenerationException("非法 @schemaExample value（不允许空白）：" + label);
                }
                examples.add(new Example(value, description));
            }
        }
        return new DocInfo(body, shortDescription.isBlank() ? body : shortDescription, examples);
    }

    private void validateExamples(String type, List<Example> examples, CharSequence field) throws GenerationException {
        Set<Object> values = new HashSet<>();
        for (Example example : examples) {
            if ((type.equals("Long") || type.equals("long")) && !isPlaceholder(example.value())) {
                try {
                    Long.parseLong(example.value());
                } catch (NumberFormatException exception) {
                    throw new GenerationException("非法 Long @schemaExample：" + field + " -> " + example.value());
                }
            }
            if ((type.equals("Integer") || type.equals("int")) && !isPlaceholder(example.value())) {
                try {
                    Integer.parseInt(example.value());
                } catch (NumberFormatException exception) {
                    throw new GenerationException("非法 Integer @schemaExample：" + field + " -> " + example.value());
                }
            }
            if (type.equals("DataSize") && !isPlaceholder(example.value())
                    && !example.value().matches("[+-]?[0-9]+(?:B|KB|MB|GB|TB)?")) {
                throw new GenerationException("非法 DataSize @schemaExample：" + field + " -> " + example.value());
            }
            Object jsonValue = exampleJsonValue(type, example.value());
            if (!values.add(jsonValue)) {
                throw new GenerationException("重复 @schemaExample：" + field + " -> " + jsonValue);
            }
        }
    }

    private boolean isPlaceholder(String value) {
        return value.matches("\\$\\{[^{}]+\\}");
    }

    private String firstSentence(String body) {
        int end = body.length();
        for (String marker : List.of("。", ". ", "! ", "? ")) {
            int index = body.indexOf(marker);
            if (index >= 0) {
                end = Math.min(end, index + (marker.equals("。") ? 1 : 1));
            }
        }
        return body.substring(0, end).trim();
    }

    private String render(List<? extends DocTree> trees) {
        StringBuilder text = new StringBuilder();
        for (DocTree tree : trees) {
            switch (tree.getKind()) {
                case TEXT -> text.append(((TextTree) tree).getBody());
                case CODE, LITERAL -> text.append(((LiteralTree) tree).getBody().getBody());
                case LINK, LINK_PLAIN -> {
                    LinkTree link = (LinkTree) tree;
                    if (link.getLabel().isEmpty()) {
                        text.append(link.getReference().getSignature());
                    } else {
                        text.append(render(link.getLabel()));
                    }
                }
                case REFERENCE -> text.append(((ReferenceTree) tree).getSignature());
                case ENTITY -> text.append('&').append(((EntityTree) tree).getName()).append(';');
                case START_ELEMENT -> text.append(' ');
                case END_ELEMENT -> text.append(' ');
                case ATTRIBUTE -> {
                    AttributeTree attribute = (AttributeTree) tree;
                    text.append(render(attribute.getValue()));
                }
                default -> text.append(tree.toString()).append(' ');
            }
        }
        return text.toString();
    }

    private String normalizeDoc(String value) {
        // Source wrapping must not turn a Chinese word or sentence boundary into an extra space.
        String joined = value.replaceAll("(?<=[\\p{IsHan}，。；：！？、（])[\\t ]*\\R[\\t ]*(?=[\\p{IsHan}，。；：！？、）])", "");
        return joined.replaceAll("\\s+", " ").trim();
    }

    private String normalizeType(String type) {
        return type.replaceAll("\\s+", "");
    }

    private String ensureKnownType(String type, Map<String, String> typeDefinitions, CharSequence field)
            throws GenerationException {
        String common = switch (type) {
            case "String" -> "nullableString";
            case "Boolean" -> "nullableBoolean";
            case "boolean" -> "booleanValue";
            case "Long" -> "nullableLong";
            case "long" -> "longValue";
            case "Integer" -> "nullableInteger";
            case "int" -> "integerValue";
            case "DataSize" -> "dataSize";
            case "Set<String>" -> "stringSet";
            case "CopyOnWriteArrayList<String>" -> "stringList";
            case "String[]" -> "stringArray";
            case "List<ResourceSceneConfig>" -> "sceneList";
            default -> null;
        };
        if (common != null) {
            return common;
        }
        if (type.startsWith("Map<String,") && type.endsWith(">")) {
            String valueType = type.substring("Map<String,".length(), type.length() - 1);
            String valueDefinition = typeDefinitions.get(valueType);
            if (valueDefinition != null) {
                return valueDefinition + "Map";
            }
        }
        String classDefinition = typeDefinitions.get(type);
        if (classDefinition != null) {
            return classDefinition;
        }
        EnumSpec enumType = enumSpec(type);
        if (enumType != null) {
            return enumType.definitionName();
        }
        throw new GenerationException("未知字段类型：" + field + " -> " + type);
    }

    private Object defaultValue(ExpressionTree initializer, String fieldType, CharSequence field)
            throws GenerationException {
        if (initializer == null) {
            return switch (fieldType) {
                case "boolean" -> false;
                case "long" -> 0L;
                case "int" -> 0;
                default -> null;
            };
        }
        if (initializer instanceof NewClassTree) {
            return null;
        }
        if (initializer instanceof NewArrayTree array) {
            if (!fieldType.equals("String[]") || array.getInitializers() == null) {
                return null;
            }
            List<String> values = new ArrayList<>();
            for (ExpressionTree expression : array.getInitializers()) {
                if (!(expression instanceof com.sun.source.tree.LiteralTree literal)
                        || !(literal.getValue() instanceof String value)) {
                    throw new GenerationException("无法解析字段默认值：" + field + " -> " + initializer);
                }
                values.add(value);
            }
            return values;
        }
        if (initializer instanceof com.sun.source.tree.LiteralTree literal) {
            Object value = literal.getValue();
            if (value == null) {
                return null;
            }
            if (fieldType.equals("String") && value instanceof String
                    || (fieldType.equals("Boolean") || fieldType.equals("boolean")) && value instanceof Boolean) {
                return value;
            }
            if ((fieldType.equals("Long") || fieldType.equals("long")) && value instanceof Number number) {
                return number.longValue();
            }
            if ((fieldType.equals("Integer") || fieldType.equals("int")) && value instanceof Integer) {
                return value;
            }
            throw new GenerationException("无法解析字段默认值：" + field + " -> " + initializer);
        }
        if (initializer instanceof MemberSelectTree memberSelect) {
            EnumSpec enumType = enumSpec(fieldType);
            if (enumType != null && memberSelect.getExpression().toString().equals(fieldType)) {
                return memberSelect.getIdentifier().toString();
            }
            return null;
        }
        if (initializer instanceof UnaryTree unary && (unary.getKind() == Tree.Kind.UNARY_MINUS || unary.getKind() == Tree.Kind.UNARY_PLUS)
                && unary.getExpression() instanceof com.sun.source.tree.LiteralTree literal && literal.getValue() instanceof Number number) {
            long value = number.longValue();
            long signed = unary.getKind() == Tree.Kind.UNARY_MINUS ? -value : value;
            if (fieldType.equals("Integer") || fieldType.equals("int")) {
                if (signed < Integer.MIN_VALUE || signed > Integer.MAX_VALUE) {
                    throw new GenerationException("无法解析字段默认值：" + field + " -> " + initializer);
                }
                return (int) signed;
            }
            if (fieldType.equals("Long") || fieldType.equals("long")) {
                return signed;
            }
            throw new GenerationException("无法解析字段默认值：" + field + " -> " + initializer);
        }
        if (initializer.getKind() == Tree.Kind.NULL_LITERAL) {
            return null;
        }
        return null;
    }

    private LinkedHashMap<String, Object> configDefinition(String description, ConfigClass config,
            Map<String, EnumModel> enums) throws GenerationException {
        LinkedHashMap<String, Object> properties = map();
        for (FieldModel field : config.fields()) {
            LinkedHashMap<String, Object> property = propertySchema(field, enums, false);
            String kebab = toKebabCase(field.name());
            properties.put(kebab, property);
            if (!kebab.equals(field.name())) {
                properties.put(field.name(), propertySchema(field, enums, true));
            }
        }
        LinkedHashMap<String, Object> definition = objectDefinition(properties, false);
        definition.put("description", description);
        moveBefore(definition, "description", "properties");
        return definition;
    }

    private LinkedHashMap<String, Object> propertySchema(FieldModel field, Map<String, EnumModel> enums, boolean alias)
            throws GenerationException {
        if (!field.examples().isEmpty() && !(field.type().equals("String") || field.type().equals("Long")
                || field.type().equals("long") || field.type().equals("Integer") || field.type().equals("int")
                || field.type().equals("DataSize"))) {
            throw new GenerationException("@schemaExample 仅适用于 String、整数和 DataSize：" + field.name());
        }
        String reference = field.referenceDefinition();
        LinkedHashMap<String, Object> property = map();
        if (isNestedConfig(field)) {
            property.put("anyOf", List.of(ref(reference), mapOf("type", "null")));
        } else if (!field.examples().isEmpty()) {
            LinkedHashMap<String, Object> suggestions = map();
            suggestions.put("enum", field.examples().stream().map(example -> exampleJsonValue(field.type(), example.value())).toList());
            suggestions.put("x-intellij-enum-metadata", enumMetadataForExamples(field.type(), field.examples()));
            property.put("anyOf", List.of(suggestions, ref(reference)));
            property.put("examples", field.examples().stream().map(example -> exampleJsonValue(field.type(), example.value())).toList());
        } else {
            property.put("allOf", List.of(ref(reference)));
        }
        String sourceDescription = field.description();
        String description = derivedDescription(field, enums);
        if (alias) {
            String aliasDescription = "这是 " + toKebabCase(field.name()) + " 的 camelCase 别名，绑定语义相同。";
            description += aliasDescription;
            sourceDescription += aliasDescription;
        }
        property.put("description", description);
        if (field.defaultValue() != null) {
            property.put("default", field.defaultValue());
        }
        property.put("x-intellij-html-description", htmlDescription(sourceDescription, field, enums));
        return property;
    }

    private Object exampleJsonValue(String type, String value) {
        if ((type.equals("Long") || type.equals("long")) && value.matches("[+-]?[0-9]+")) {
            return Long.parseLong(value);
        }
        if ((type.equals("Integer") || type.equals("int")) && value.matches("[+-]?[0-9]+")) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private boolean isNestedConfig(FieldModel field) {
        return !Set.of("String", "Boolean", "boolean", "Long", "long", "Integer", "int", "DataSize",
                        "Set<String>", "CopyOnWriteArrayList<String>", "String[]", "List<ResourceSceneConfig>")
                .contains(field.type())
                && !field.type().startsWith("Map<String,")
                && enumSpec(field.type()) == null;
    }

    private String derivedDescription(FieldModel field, Map<String, EnumModel> enums) {
        StringBuilder description = new StringBuilder(field.description());
        if (field.defaultValue() != null) {
            appendSentence(description, "默认值：" + field.defaultValue() + "。");
        }
        EnumModel enumModel = enums.get(field.type());
        if (enumModel != null) {
            StringBuilder candidates = new StringBuilder("候选值：");
            for (int index = 0; index < enumModel.values().size(); index++) {
                EnumValue value = enumModel.values().get(index);
                if (index > 0) {
                    candidates.append("；");
                }
                candidates.append(value.value()).append("（")
                        .append(value.shortDescription().replaceFirst("[。.!?！？]+$", "")).append("）");
            }
            candidates.append('。');
            appendSentence(description, candidates.toString());
        }
        return description.toString();
    }

    private void appendSentence(StringBuilder description, String sentence) {
        if (!description.isEmpty() && !Character.isWhitespace(description.charAt(description.length() - 1))) {
            description.append(' ');
        }
        description.append(sentence);
    }

    private String htmlDescription(String description, FieldModel field, Map<String, EnumModel> enums) {
        StringBuilder html = new StringBuilder("<p>").append(htmlEscape(description)).append("</p>");
        if (field.defaultValue() != null) {
            html.append("<p><b>默认值：</b><code>").append(htmlEscape(String.valueOf(field.defaultValue()))).append("</code></p>");
        }
        EnumModel enumModel = enums.get(field.type());
        if (enumModel != null) {
            html.append("<p><b>候选值及含义：</b></p>");
            for (EnumValue value : enumModel.values()) {
                // A separate full-width block avoids narrow table cells splitting enum labels in IDEA.
                html.append("<p><code>").append(htmlEscape(value.value())).append("</code><br>")
                        .append(htmlEscape(value.description())).append("</p>");
            }
        }
        if (!field.examples().isEmpty()) {
            html.append("<p><b>示例：</b></p><ul>");
            for (Example example : field.examples()) {
                html.append("<li><code>").append(htmlEscape(example.value())).append("</code> — ")
                        .append(htmlEscape(example.description())).append("</li>");
            }
            html.append("</ul>");
        }
        return html.toString();
    }

    private LinkedHashMap<String, Object> enumDefinition(EnumSpec spec, EnumModel enumModel) {
        List<String> values = enumModel.values().stream().map(EnumValue::value).toList();
        LinkedHashMap<String, Object> standard = map();
        standard.put("enum", values);
        standard.put("x-intellij-enum-metadata", enumMetadata(enumModel));

        LinkedHashMap<String, Object> definition = map();
        definition.put("description", spec.title() + "。IDEA 候选使用标准值 " + String.join("、", values)
                + "。Spring 枚举转换会去除全部非字母数字字符后忽略大小写匹配，trim 后的空串转换为 null；Binder 绑定带初始化值的字段时会保留该值，不能据此推断字段被清空。Schema 覆盖常用 ASCII 宽松写法和环境变量占位符，复杂 Unicode 转换仍由 Spring Binder 判定。");
        definition.put("anyOf", List.of(
                standard,
                mapOf("type", "string", "pattern", enumPattern(values)),
                ref("placeholderString"),
                mapOf("type", "null")));
        return definition;
    }

    private LinkedHashMap<String, Object> enumMetadata(EnumModel enumModel) {
        LinkedHashMap<String, Object> metadata = map();
        for (EnumValue value : enumModel.values()) {
            LinkedHashMap<String, Object> item = mapOf("description", value.shortDescription());
            if (!value.description().equals(value.shortDescription())) {
                item.put("x-intellij-html-description", "<p>" + htmlEscape(value.description()) + "</p>");
            }
            metadata.put(value.value(), item);
        }
        return metadata;
    }

    private LinkedHashMap<String, Object> enumMetadataForExamples(String type, List<Example> examples) {
        LinkedHashMap<String, Object> metadata = map();
        for (Example example : examples) {
            metadata.put(String.valueOf(exampleJsonValue(type, example.value())), mapOf("description", example.description()));
        }
        return metadata;
    }

    private String enumPattern(List<String> values) {
        List<String> patterns = new ArrayList<>();
        for (String value : values) {
            StringBuilder pattern = new StringBuilder(ENUM_SEPARATOR);
            for (int index = 0; index < value.length(); index++) {
                char character = value.charAt(index);
                if (character >= 'A' && character <= 'Z') {
                    pattern.append('[').append(character).append(Character.toLowerCase(character)).append(']');
                } else if (character >= 'a' && character <= 'z') {
                    pattern.append('[').append(Character.toUpperCase(character)).append(character).append(']');
                } else if (Character.isDigit(character)) {
                    pattern.append(character);
                }
                pattern.append(ENUM_SEPARATOR);
            }
            patterns.add(pattern.toString());
        }
        return "^(?:[\\u0000-\\u0020]*|(?:" + String.join("|", patterns) + "))$";
    }

    private void addCommonDefinitions(LinkedHashMap<String, Object> definitions) {
        definitions.put("nullableString", mapOf(
                "description", "字符串、包含 Spring 环境变量占位符的字符串或 null。",
                "type", List.of("string", "null")));
        definitions.put("placeholderString", mapOf(
                "type", "string",
                "description", "Spring 环境变量占位符，支持 ${ENV}、${ENV:default} 以及嵌入普通字符串；占位符解析后的目标类型和值合法性由 Spring Binder 检查。",
                "pattern", "\\$\\{[^{}]+\\}"));
        definitions.put("nullableBoolean", mapOf(
                "description", "布尔值、Spring 布尔字符串、环境变量占位符或 null。字符串会先去除两端 U+0000-U+0020 字符，再忽略大小写识别 true/on/yes/1 与 false/off/no/0；精确空串或仅含这些 trim 字符的字符串转换为 null。转换结果不等于对初始化字段执行清空，最终值由 Binder 与绑定目标决定。JSON 数字 1、0 不按布尔值接受。",
                "anyOf", List.of(
                        mapOf("type", "boolean"),
                        mapOf("type", "string", "pattern", "^[\\u0000-\\u0020]*(?:(?:[Tt][Rr][Uu][Ee]|[Oo][Nn]|[Yy][Ee][Ss]|1|[Ff][Aa][Ll][Ss][Ee]|[Oo][Ff][Ff]|[Nn][Oo]|0)[\\u0000-\\u0020]*)?$"),
                        ref("placeholderString"), mapOf("type", "null"))));
        definitions.put("booleanValue", mapOf(
                "description", "primitive boolean、Spring 布尔字符串或环境变量占位符。未配置字段时使用 Java 初始化值。",
                "anyOf", List.of(
                        mapOf("type", "boolean"),
                        mapOf("type", "string", "pattern", "^[\\u0000-\\u0020]*(?:[Tt][Rr][Uu][Ee]|[Oo][Nn]|[Yy][Ee][Ss]|1|[Ff][Aa][Ll][Ss][Ee]|[Oo][Ff][Ff]|[Nn][Oo]|0)[\\u0000-\\u0020]*$"),
                        ref("placeholderString"))));
        String whitespace = JAVA_WHITESPACE + "*";
        String digits = "[0-9](?:" + whitespace + "[0-9])*";
        String hexDigits = "[0-9A-Fa-f](?:" + whitespace + "[0-9A-Fa-f])*";
        String decimalLong = whitespace + "[+-]?" + whitespace + digits + whitespace;
        String hexadecimalLong = whitespace + "-?" + whitespace + "(?:(?:0" + whitespace + "[xX])|#)"
                + whitespace + hexDigits + whitespace;
        definitions.put("nullableLong", mapOf(
                "description", "Java Long、Long 字符串、环境变量占位符或 null。字符串会移除全部 Java Character.isWhitespace 空白，支持正负十进制，以及 0x/0X/# 前缀的十六进制；十六进制可带负号但不接受正号。精确空串转换为 null，Binder 会保留带初始化值的字段；仅含空白的字符串非法。64 位范围和解析溢出由 Spring Binder 检查。",
                "anyOf", List.of(mapOf("type", "integer"),
                        mapOf("type", "string", "pattern", "^(?:$|" + decimalLong + "|" + hexadecimalLong + ")$"),
                        ref("placeholderString"), mapOf("type", "null"))));
        definitions.put("longValue", mapOf(
                "description", "primitive long、Long 字符串或环境变量占位符。未配置字段时使用 Java 初始化值；范围和溢出由 Spring Binder 检查。",
                "anyOf", List.of(mapOf("type", "integer"),
                        mapOf("type", "string", "pattern", "^(?:" + decimalLong + "|" + hexadecimalLong + ")$"),
                        ref("placeholderString"))));
        definitions.put("nullableInteger", mapOf(
                "description", "Java Integer、十进制或 0x/0X/# 前缀十六进制整数字符串、环境变量占位符或 null。十六进制可带负号但不接受正号；精确空串转换为 null，32 位范围由 Spring Binder 检查。",
                "anyOf", List.of(mapOf("type", "integer"),
                        mapOf("type", "string", "pattern", "^(?:$|" + decimalLong + "|" + hexadecimalLong + ")$"),
                        ref("placeholderString"), mapOf("type", "null"))));
        definitions.put("integerValue", mapOf(
                "description", "primitive int、十进制或 0x/0X/# 前缀十六进制整数字符串、环境变量占位符。十六进制可带负号但不接受正号；未配置字段时使用 Java 初始化值。",
                "anyOf", List.of(mapOf("type", "integer"),
                        mapOf("type", "string", "pattern", "^(?:" + decimalLong + "|" + hexadecimalLong + ")$"),
                        ref("placeholderString"))));
        String unit = "(?:B|K" + whitespace + "B|M" + whitespace + "B|G" + whitespace + "B|T" + whitespace + "B)";
        String dataSize = whitespace + "[+-]?" + whitespace + digits + whitespace + "(?:" + unit + ")?" + whitespace;
        definitions.put("dataSize", mapOf(
                "description", "Spring DataSize、裸字节整数、环境变量占位符或 null。字符串会移除全部 Java Character.isWhitespace 空白，数值支持正负号，单位仅接受精确大写 B、KB、MB、GB、TB；KB 及以上按 1024 进制换算。精确空串转换为 null，字段初始化值及业务回退另由 Binder 与读取方决定；仅含空白的字符串非法。数值和换算溢出由 Spring Binder 检查。",
                "anyOf", List.of(mapOf("type", "integer"),
                        mapOf("type", "string", "pattern", "^(?:$|" + dataSize + ")$"),
                        ref("placeholderString"), mapOf("type", "null"))));
        definitions.put("stringSet", mapOf(
                "description", "字符串集合，可写成 YAML 数组、Spring 逗号分隔字符串、环境变量占位符或 null。",
                "anyOf", List.of(mapOf("type", "array", "items", mapOf("type", "string")),
                        mapOf("type", "string"), mapOf("type", "null"))));
        definitions.put("stringList", mapOf(
                "description", "字符串列表，可写成 YAML 数组、Spring 逗号分隔字符串、环境变量占位符或 null。",
                "anyOf", List.of(mapOf("type", "array", "items", mapOf("type", "string")),
                        mapOf("type", "string"), mapOf("type", "null"))));
        definitions.put("stringArray", mapOf(
                "description", "字符串数组，可写成 YAML 数组、Spring 逗号分隔字符串、环境变量占位符或 null。",
                "anyOf", List.of(mapOf("type", "array", "items", mapOf("type", "string")),
                        mapOf("type", "string"), mapOf("type", "null"))));
    }

    private LinkedHashMap<String, Object> sceneListDefinition() {
        return mapOf(
                "description", "资源场景列表，可为 YAML 数组、环境变量占位符或 null。",
                "anyOf", List.of(
                        mapOf("type", "array", "items", ref("resourceScene")),
                        ref("placeholderString"),
                        mapOf("type", "null")));
    }

    private LinkedHashMap<String, Object> describedReference(String definition, String description) {
        return mapOf("allOf", List.of(ref(definition)), "description", description);
    }

    private LinkedHashMap<String, Object> mapDefinition(String description, String valueDefinition) {
        return mapOf("description", description, "type", List.of("object", "null"),
                "additionalProperties", ref(valueDefinition));
    }

    private LinkedHashMap<String, Object> objectDefinition(Map<String, Object> properties, boolean additionalProperties) {
        return mapOf("type", "object", "properties", properties, "additionalProperties", additionalProperties);
    }

    private LinkedHashMap<String, Object> ref(String definition) {
        return mapOf("$ref", "#/definitions/" + definition);
    }

    private String toKebabCase(String value) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isUpperCase(character)) {
                result.append('-').append(Character.toLowerCase(character));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    private String htmlEscape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private EnumSpec enumSpec(String className) {
        return ENUM_SOURCES.stream().filter(spec -> spec.className().equals(className)).findFirst().orElse(null);
    }

    private SourceSpec sourceSpec(String className) {
        return CONFIG_SOURCES.stream().filter(spec -> spec.className().equals(className)).findFirst().orElse(null);
    }

    private String lowerFirst(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private void putDefinition(Map<String, Object> definitions, String name, Object definition)
            throws GenerationException {
        if (definitions.putIfAbsent(name, definition) != null) {
            throw new GenerationException("Schema definition 名称冲突：" + name);
        }
    }

    private void moveBefore(LinkedHashMap<String, Object> map, String key, String beforeKey) {
        Object value = map.remove(key);
        LinkedHashMap<String, Object> reordered = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals(beforeKey)) {
                reordered.put(key, value);
            }
            reordered.put(entry.getKey(), entry.getValue());
        }
        map.clear();
        map.putAll(reordered);
    }

    private static void writeAllAtomically(Path root, Map<Path, String> outputs) throws IOException {
        LinkedHashMap<Path, Path> staged = new LinkedHashMap<>();
        try {
            for (Map.Entry<Path, String> entry : outputs.entrySet()) {
                Path output = root.resolve(entry.getKey());
                Files.createDirectories(output.getParent());
                Path temporary = output.resolveSibling(output.getFileName() + ".tmp");
                Files.writeString(temporary, entry.getValue(), StandardCharsets.UTF_8);
                staged.put(output, temporary);
            }
            for (Map.Entry<Path, Path> entry : staged.entrySet()) {
                try {
                    Files.move(entry.getValue(), entry.getKey(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException atomicFailure) {
                    try {
                        Files.copy(entry.getValue(), entry.getKey(), StandardCopyOption.REPLACE_EXISTING);
                        Files.delete(entry.getValue());
                    } catch (IOException fallbackFailure) {
                        fallbackFailure.addSuppressed(atomicFailure);
                        throw fallbackFailure;
                    }
                }
            }
        } finally {
            for (Path temporary : staged.values()) {
                Files.deleteIfExists(temporary);
            }
        }
    }

    private static LinkedHashMap<String, Object> map() {
        return new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private static <K, V> LinkedHashMap<K, V> mapOf(Object... entries) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            map.put((K) entries[index], (V) entries[index + 1]);
        }
        return map;
    }

    private enum Mode {
        WRITE, CHECK
    }

    private record Arguments(Mode mode, Path root) {
        private static Arguments parse(String[] args) throws GenerationException {
            if (args.length < 1 || args.length > 2 || !(args[0].equals("--write") || args[0].equals("--check"))) {
                throw new GenerationException("用法：java sz-build/config-schema/OssSchemaGenerator.java (--write|--check) [repo-root]");
            }
            return new Arguments(args[0].equals("--write") ? Mode.WRITE : Mode.CHECK,
                    args.length == 2 ? Path.of(args[1]) : Path.of("."));
        }
    }

    private record SourceSpec(String path, String className, String definitionName, String description,
            String configurationPrefix) {
    }

    private record EnumSpec(String path, String className, String definitionName, String title) {
    }

    private record ParsedSources(Map<String, ConfigClass> classes, Map<String, EnumModel> enums) {
    }

    private record ConfigClass(String name, String definitionName, String description, List<FieldModel> fields) {
    }

    private record FieldModel(String name, String type, String referenceDefinition, String description,
            List<Example> examples, Object defaultValue) {
    }

    private record EnumModel(String name, List<EnumValue> values) {
    }

    private record EnumValue(String value, String shortDescription, String description) {
    }

    private record Example(String value, String description) {
    }

    private record DocInfo(String description, String shortDescription, List<Example> examples) {
    }

    private static final class GenerationException extends Exception {
        private GenerationException(String message) {
            super(Objects.requireNonNull(message));
        }
    }

    private static final class Json {
        private static String write(Object value) {
            StringBuilder output = new StringBuilder();
            append(value, output, 0);
            return output.append('\n').toString();
        }

        private static void append(Object value, StringBuilder output, int indent) {
            if (value == null) {
                output.append("null");
            } else if (value instanceof String string) {
                appendString(string, output);
            } else if (value instanceof Number || value instanceof Boolean) {
                output.append(value);
            } else if (value instanceof Map<?, ?> map) {
                appendMap(map, output, indent);
            } else if (value instanceof Iterable<?> iterable) {
                appendList(iterable, output, indent);
            } else {
                throw new IllegalArgumentException("Unsupported JSON value: " + value.getClass());
            }
        }

        private static void appendMap(Map<?, ?> map, StringBuilder output, int indent) {
            output.append('{');
            if (!map.isEmpty()) {
                output.append('\n');
                int index = 0;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    spaces(output, indent + 2);
                    appendString(entry.getKey().toString(), output);
                    output.append(": ");
                    append(entry.getValue(), output, indent + 2);
                    if (++index < map.size()) {
                        output.append(',');
                    }
                    output.append('\n');
                }
                spaces(output, indent);
            }
            output.append('}');
        }

        private static void appendList(Iterable<?> values, StringBuilder output, int indent) {
            List<Object> list = new ArrayList<>();
            values.forEach(list::add);
            output.append('[');
            if (!list.isEmpty()) {
                output.append('\n');
                for (int index = 0; index < list.size(); index++) {
                    spaces(output, indent + 2);
                    append(list.get(index), output, indent + 2);
                    if (index + 1 < list.size()) {
                        output.append(',');
                    }
                    output.append('\n');
                }
                spaces(output, indent);
            }
            output.append(']');
        }

        private static void appendString(String value, StringBuilder output) {
            output.append('"');
            for (int index = 0; index < value.length(); index++) {
                char character = value.charAt(index);
                switch (character) {
                    case '"' -> output.append("\\\"");
                    case '\\' -> output.append("\\\\");
                    case '\b' -> output.append("\\b");
                    case '\f' -> output.append("\\f");
                    case '\n' -> output.append("\\n");
                    case '\r' -> output.append("\\r");
                    case '\t' -> output.append("\\t");
                    default -> {
                        if (character < 0x20) {
                            output.append(String.format(Locale.ROOT, "\\u%04x", (int) character));
                        } else {
                            output.append(character);
                        }
                    }
                }
            }
            output.append('"');
        }

        private static void spaces(StringBuilder output, int count) {
            output.append(" ".repeat(count));
        }
    }
}
