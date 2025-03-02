package tn.esprit.pidev.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir; // e.g., C:\wamp64\www\Uploads\Attendances

    public String storeFile(String fileName, byte[] imageData) throws IOException {
        String filePath = uploadDir + File.separator + fileName;
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            FileCopyUtils.copy(Objects.requireNonNull(imageData), fos);
        }
        return filePath;
    }
}
