package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.ImageData;
import com.bsuir.sirius.exception.SiriusProcessingImageException;
import com.bsuir.sirius.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageValidatorService {

    private List<String> hashedImages;
    private final ImageDataRepository imageDataRepository;

    @PostConstruct
    public void init() {
        hashedImages = imageDataRepository
                .findAll()
                .stream()
                .filter(imageData -> new File(imageData.getPath()).exists())
                .map(imageData -> {
                    File file = new File(imageData.getPath());
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(ImageIO.read(file), "png", baos);
                        return String.valueOf(Arrays.hashCode(Base64.getEncoder().encode(baos.toByteArray())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        log.info("{{Validator Service}} Images has been hashed, hashlist size: {} ", hashedImages.toArray().length);
    }


    public void validateAndCache(BufferedImage bufferedImage) throws SiriusProcessingImageException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String bytes = String.valueOf(Arrays.hashCode(Base64.getEncoder().encode(baos.toByteArray())));
        if (hashedImages.contains(bytes)) {
            throw new SiriusProcessingImageException("The same image already uploaded");
        } else {
            hashedImages.add(bytes);
        }
    }

}
