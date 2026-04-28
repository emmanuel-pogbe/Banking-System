package com.pogbe.bankingsystem.utils;

import com.pogbe.bankingsystem.exceptions.custom.FileHandlingException;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FilesValidatorUtils {
    private static Logger LOG = LoggerFactory.getLogger(FilesValidatorUtils.class);
    public static boolean isValidFile(MultipartFile file, Long maxSize, String fileType) {
        if (file == null || file.getContentType() == null) {
            return false;
        }
        if (file.getSize() > maxSize) {
            return false;
        }
        final Tika tikaOb = new Tika();
        try {
            String fileString = tikaOb.detect(file.getInputStream());
            LOG.info("File type from files validator: {}",fileString);
            return fileString.contains(fileType);
        } catch (IOException e) {
            throw new FileHandlingException("Error while reading file");
        }

    }
}
