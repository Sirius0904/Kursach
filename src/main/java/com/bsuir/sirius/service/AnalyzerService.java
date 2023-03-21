package com.bsuir.sirius.service;

import com.bsuir.sirius.service.nn.NeuralNetwork;
import lombok.SneakyThrows;
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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyzerService {
    private NeuralNetwork nn;

    @PostConstruct
    public void init() throws IOException {
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
        this.nn = new NeuralNetwork(0.001, sigmoid, dsigmoid, 784, 512, 128, 32, 10);
        int samples = 58168;
        BufferedImage[] images = new BufferedImage[samples];
        int[] digits = new int[samples];
        File[] imagesFiles = new File("C:/Users/dark/Desktop/train").listFiles();
        for (int i = 0; i < samples; i++) {
            images[i] = ImageIO.read(imagesFiles[i]);
            digits[i] = Integer.parseInt(imagesFiles[i].getName().charAt(10) + "");
        }

        double[][] inputs = new double[samples][784];
        for (int i = 0; i < samples; i++) {
            for (int x = 0; x < 28; x++) {
                for (int y = 0; y < 28; y++) {
                    inputs[i][x + y * 28] = (images[i].getRGB(x, y) & 0xff) / 255.0;
                }
            }
        }

        int epochs = 100;
        for (int i = 1; i < epochs; i++) {
            int right = 0;
            double errorSum = 0;
            int batchSize = 100;
            for (int j = 0; j < batchSize; j++) {
                int imgIndex = (int) (Math.random() * samples);
                double[] targets = new double[10];
                int digit = digits[imgIndex];
                targets[digit] = 1;

                double[] outputs = nn.feedForward(inputs[imgIndex]);
                int maxDigit = 0;
                double maxDigitWeight = -1;
                for (int k = 0; k < 10; k++) {
                    if (outputs[k] > maxDigitWeight) {
                        maxDigitWeight = outputs[k];
                        maxDigit = k;
                    }
                }
                if (digit == maxDigit) right++;
                for (int k = 0; k < 10; k++) {
                    errorSum += (targets[k] - outputs[k]) * (targets[k] - outputs[k]);
                }
                nn.backpropagation(targets);
            }
            log.info("epoch: " + i + ". correct: " + right + ". error: " + errorSum);
        }

    }

    public Integer feedImage(BufferedImage bufferedImage){
        double[] input = new double[784];

        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                input[x + y * 28] = (bufferedImage.getRGB(x, y) & 0xff) / 255.0;
            }
        }
        double[] doubles = nn.feedForward(input);
        System.out.println(Arrays.toString(doubles));
        List<Double> doubles1 = Arrays.stream(doubles).boxed().collect(Collectors.toList());
        return doubles1.indexOf(doubles1.stream().max(Double::compareTo).orElse(0.0));
    }
}
