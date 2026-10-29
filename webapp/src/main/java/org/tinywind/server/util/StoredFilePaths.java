package org.tinywind.server.util;

import java.nio.file.Files;
import java.nio.file.Path;

public final class StoredFilePaths {
    private StoredFilePaths() {
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank() || name.equals(".") || name.equals("..")
                || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
                || name.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid stored file name");
        }
    }

    public static Path resolve(String directory, String name) {
        validateName(name);
        Path root = Path.of(directory).toAbsolutePath().normalize();
        Path file = root.resolve(name).normalize();
        if (!file.startsWith(root) || file.equals(root) || Files.isSymbolicLink(file)) {
            throw new IllegalArgumentException("File must remain in the storage directory");
        }
        return file;
    }
}
