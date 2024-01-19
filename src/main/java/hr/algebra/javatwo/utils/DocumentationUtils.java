package hr.algebra.javatwo.utils;


import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;


public class DocumentationUtils {
    public static String appendModifier( int modifiers) {
        String html = "";
        if (Modifier.isPublic(modifiers)) {
            html +="public ";
        } else if (Modifier.isPrivate(modifiers)) {
            html +="private ";
        } else if (Modifier.isProtected(modifiers)) {
            html +="protected ";
        } else if (Modifier.isFinal(modifiers)) {
            html +="final ";
        } else if (Modifier.isStatic(modifiers)) {
            html +="static ";
        }

        return html;
    }

    public static String getFullyQualifiedName(String classFile) {
        String[] classFileTokens = classFile.split("classes");
        String classFilePath = classFileTokens[1];
        String reducedClassFilePath = classFilePath.substring(1, classFilePath.lastIndexOf('.'));
        return reducedClassFilePath.replace('\\', '.');
    }


    public static void generateDocumentation() {

        String projectPath = System.getProperty("user.dir");
        Path targetPath = Path.of(projectPath, "target");

        StringBuilder headerHtml = new StringBuilder("""
                <!DOCTYPE html>
                <html>
                <head>
                <title>Clank! game documentation</title>
                </head>
                <body>
                                
                                
                """);

        try (Stream<Path> paths = Files.walk(targetPath)) {
            List<String> classFiles = paths
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".class"))
                    .filter(file -> !file.endsWith("module-info.class"))
                    .toList();

            for (String classFile : classFiles) {
                String fullyQualifiedName = getFullyQualifiedName(classFile);

                Class<?> deserializedClass = Class.forName(fullyQualifiedName);

                headerHtml.append("<h2>").append(fullyQualifiedName).append("</h2>");
                headerHtml.append("<ul>");


                Field[] classFields = deserializedClass.getDeclaredFields();
                for (Field field : classFields) {
                    headerHtml.append("<li>");
                    appendModifier(field.getModifiers());
                    headerHtml.append(field.getType().getTypeName()).append('\n');
                    headerHtml.append(field.getName()).append('\n');
                    headerHtml.append("</li>");
                }

                headerHtml.append("</ul>");
            }

            String footerHtml = """
                    </body>
                    </html>
                    """;

            String generatedHtml = headerHtml + footerHtml;


            Path documentationFilePath = Path.of("files/documentation.html");
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Documentation  generated!", "Documentation has been successfully generated!");


            Files.write(documentationFilePath, generatedHtml.getBytes());
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Error!", "An error occurred during documentation generation!");


            throw new RuntimeException(e);
        }
    }


}
