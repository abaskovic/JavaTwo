package hr.algebra.javatwo.utils;


import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;


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


}
