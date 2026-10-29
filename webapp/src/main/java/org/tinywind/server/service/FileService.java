package org.tinywind.server.service;

import org.tinywind.server.model.entity.FileEntity;
import org.tinywind.server.model.form.FileForm;
import org.tinywind.server.model.form.FileUploadForm;
import org.tinywind.server.repository.FileRepository;
import org.tinywind.server.util.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.LinkOption;
import org.tinywind.server.util.StoredFilePaths;
import java.io.IOException;
import java.util.UUID;

/**
 * @author tinywind
 * @since 2018-01-14
 */
@RequiredArgsConstructor
@Service
public class FileService extends BaseService {
    public static final String FILE_PATH = "files/download";
    public static final String FILE_REQUEST_PARAM_KEY = "file";

    private final FileRepository fileRepository;

    @Value("${user-data.application.file.location}")
    private String fileLocation;

    public String url(UUID fileId) {
        if (fileId == null)
            return null;

        return url(fileRepository.findOne(fileId));
    }

    public String url(FileEntity fileEntity) {
        if (fileEntity == null)
            return null;

        return url(fileEntity.getPath());
    }

    public String url(String path) {
        if (path == null)
            return null;

        return baseUrl() + "?" + UrlUtils.encodeQueryParams(FILE_REQUEST_PARAM_KEY, path);
    }

    public String baseUrl() {
        return ("/" + FILE_PATH + "/").replaceAll("[/]+", "/");
    }

    public UUID save(String fileName, byte[] bytes) throws IOException {
        StoredFilePaths.validateName(fileName);
        final Path directory = Path.of(fileLocation).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        final String filePath = UUID.randomUUID() + "_" + fileName;
        final Path file = StoredFilePaths.resolve(fileLocation, filePath);
        try (final java.io.OutputStream writer = Files.newOutputStream(file,
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
            writer.write(bytes);
            writer.flush();
            final FileForm form = new FileForm(fileName, filePath, (long) bytes.length);
            fileRepository.insert(form);
            return form.getId();
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(file);
            throw e;
        }
    }

    public UUID save(FileUploadForm form) throws IOException {
        return save(form.getFileName(), Base64.decodeBase64(form.getData()));
    }

    public UUID save(MultipartFile file) throws IOException {
        return save(file.getOriginalFilename(), file.getBytes());
    }

    public File file(String path) {
        return StoredFilePaths.resolve(fileLocation, path).toFile();
    }

    public File file(UUID id) {
        final FileEntity file = fileRepository.findOne(id);
        return file(file.getPath());
    }

    public File file(FileEntity file) {
        return file(file.getPath());
    }
}
