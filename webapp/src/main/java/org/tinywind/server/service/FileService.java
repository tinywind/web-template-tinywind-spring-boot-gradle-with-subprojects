package org.tinywind.server.service;

import org.tinywind.server.model.entity.FileEntity;
import org.tinywind.server.model.form.FileForm;
import org.tinywind.server.model.form.FileUploadForm;
import org.tinywind.server.repository.FileRepository;
import org.tinywind.server.util.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author tinywind
 * @since 2018-01-14
 */
@Slf4j
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
        final File fileDirectory = new File(fileLocation);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
            log.info("create fileLocation: " + fileDirectory.getAbsolutePath());
        }

        final String filePath = System.currentTimeMillis() + "_" + System.nanoTime() + "_" + fileName;
        final File file = new File(fileLocation, filePath);
        try (final FileOutputStream writer = new FileOutputStream(file)) {
            writer.write(bytes);
            writer.flush();

            final FileForm form = new FileForm(fileName, filePath, (long) bytes.length);
            fileRepository.insert(form);
            return form.getId();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            boolean deleted = file.delete();
            if (!deleted) log.error("Failed: delete file: " + file.getAbsolutePath());

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
        return new File(fileLocation, path);
    }

    public File file(UUID id) {
        final FileEntity file = fileRepository.findOne(id);
        return new File(fileLocation, file.getPath());
    }

    public File file(FileEntity file) {
        return new File(fileLocation, file.getPath());
    }
}
