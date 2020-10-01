package com.priceline.hotels.hollow;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * This is a stop-gap solution, while we find a better way to do this (mavenize?)
 *
 * Pass the following program arguments
 *
 * [0] -> directory of schema files.
 * example: /Users/rsugirtharaj/code/pl-connect-htl-hollow/src/main/schemas
 *
 * [1] -> directory of output generated files.
 * example: /Users/rsugirtharaj/Desktop
 *
 * [2] -> the main API classname.
 * example: HotelMerchandisingAPI
 *
 * [3] -> package name of generated files.
 * com.priceline.hotels.merchandising.hollow.merchandisingdata.generated
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // a way to filter related schema files
        final String schemaFileEndWithPattern = "merchandising-data.hs";

        String schemaFileContent = Files.list(Paths.get(args[0]))
                .filter(path -> path.endsWith(schemaFileEndWithPattern))
                .flatMap(Main::readLinesFromPath)
                .collect(Collectors.joining());

        HollowWriteStateEngine stateEngine = HollowWriteStateCreator
                .createWithSchemas(HollowSchemaParser.parseCollectionOfSchemas(schemaFileContent));

        new HollowAPIGenerator.Builder()
                .withDestination(args[1])
                .withAPIClassname(args[2])
                .withPackageName(args[3])
                .withDataModel(stateEngine)
                .build()
        .generateSourceFiles();
    }

    private static Stream<String> readLinesFromPath(Path path) {
        try {
            return Files.readAllLines(path).stream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
