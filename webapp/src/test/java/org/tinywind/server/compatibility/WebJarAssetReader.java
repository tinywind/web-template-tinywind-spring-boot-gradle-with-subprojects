package org.tinywind.server.compatibility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class WebJarAssetReader {
    public static void main(String[] args) throws IOException {
        try (ZipFile war = new ZipFile(args[0])) {
            String libraryPath = "WEB-INF/lib/" + args[1];
            ZipEntry library = war.getEntry(libraryPath);
            if (library == null) {
                throw new FileNotFoundException(libraryPath);
            }
            try (ZipInputStream jar = new ZipInputStream(war.getInputStream(library))) {
                for (ZipEntry entry; (entry = jar.getNextEntry()) != null;) {
                    if (entry.getName().equals(args[2])) {
                        jar.transferTo(System.out);
                        return;
                    }
                }
            }
        }
        throw new FileNotFoundException(args[2]);
    }
}
