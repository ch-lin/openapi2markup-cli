package org.c6.openapi2markup.cli;

import io.airlift.airline.*;
import io.github.swagger2markup.OpenAPI2MarkupConverter;
import io.github.swagger2markup.OpenSchema2MarkupConfig;
import io.github.swagger2markup.config.builder.OpenAPI2MarkupConfigBuilder;
import io.github.swagger2markup.utils.URIUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Paths;

@Command(name = "convert", description = "Converts a OpenAPI 3.0 JSON or YAML file into Markup documents.")
public class Application implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Inject
    public HelpOption helpOption;

    @Option(name = {"-i", "--swaggerInput"}, required = true, description = "Swagger input. Can either be a URL or a file path.")
    public String swaggerInput;

    @Option(name = {"-d", "--outputDir"}, description = "Output directory. Converts the Swagger specification into multiple files.")
    public String outputDir;

    @Option(name = {"-f", "--outputFile"}, description = "Output file. Converts the Swagger specification into one file.")
    public String outputFile;

    public static void main(String[] args) {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("swagger2markup")
                .withDescription("Converts a Swagger JSON or YAML file into Markup documents")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, Application.class);

        Cli<Runnable> gitParser = builder.build();

        gitParser.parse(args).run();
    }

    public void run() {
        OpenAPI2MarkupConverter.Builder converterBuilder = OpenAPI2MarkupConverter.from(URIUtils.create(swaggerInput));
        OpenAPI2MarkupConverter converter = converterBuilder.build();

        if (StringUtils.isNotBlank(outputFile)) {
            converter.toFile(Paths.get(outputFile).toAbsolutePath());
        } else if (StringUtils.isNotBlank(outputDir)) {
            converter.toFolder(Paths.get(outputDir).toAbsolutePath());
        } else {
            throw new IllegalArgumentException("Either outputFile or outputDir option must be used");
        }
    }
}
