package com.bsuir.sirius.service;

import com.bsuir.sirius.service.nn.Layer;
import com.bsuir.sirius.service.nn.NeuralNetwork;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyzerService {
    private NeuralNetwork nn;

    @PostConstruct
    public void init() throws IOException {
        File file = new File("./nn-layers.json");
        ObjectMapper objectMapper = new ObjectMapper();

        if (!file.exists() || file.getTotalSpace() == 0) {
            try {
                boolean newFile = file.createNewFile();
                log.info("Layers file created: {}", String.valueOf(newFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Layers file not detected, learning initialized");

            nn = NeuralNetwork.learnAndInit("C:/Users/dark/Desktop/train");

            try {
                log.info("Saving data");
                FileUtils.write(file, objectMapper.writeValueAsString(nn.getLayers()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            nn = NeuralNetwork.fromLayers(objectMapper.readValue(file, Layer[].class));
        }

    }

    public Integer forwardImageBytes(byte[] bytes) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(bytes));
            log.info("Loaded image from bytearray, {}", bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage scaledImage = img;
        final int w = img.getWidth();
        final int h = img.getHeight();
        if ((w != 28) && (h != 28)) {
            scaledImage = new BufferedImage((28), (28), BufferedImage.TYPE_INT_ARGB);
            final AffineTransform at = AffineTransform.getScaleInstance(0.1, 0.1);
            final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
            scaledImage = ato.filter(img, scaledImage);
        }

        double[] input = new double[784];

        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                input[x + y * 28] = (scaledImage.getRGB(x, y) & 0xff) / 255.0;
            }
        }
        double[] doubles = nn.feedForward(input);
        System.out.println(Arrays.toString(doubles));
        List<Double> doubles1 = Arrays.stream(doubles).boxed().collect(Collectors.toList());
        return doubles1.indexOf(doubles1.stream().max(Double::compareTo).orElse(0.0));
    }
}
