package com.bsuir.sirius.controller;

import com.bsuir.sirius.entity.Commentary;
import com.bsuir.sirius.entity.Image;
import com.bsuir.sirius.entity.ImageData;
import com.bsuir.sirius.entity.UserData;
import com.bsuir.sirius.repository.CommentaryRepository;
import com.bsuir.sirius.repository.ImageDataRepository;
import com.bsuir.sirius.repository.ImageRepository;
import com.bsuir.sirius.repository.UserDataRepository;
import com.bsuir.sirius.service.AnalyzerService;
import com.bsuir.sirius.to.rest.request.AnalyzeImageTO;
import com.bsuir.sirius.to.rest.request.CommentTO;
import com.bsuir.sirius.to.rest.response.AnalyzeImageResponseTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SiriusRestController {
    private final ImageRepository imageRepository;
    private final UserDataRepository userDataRepository;
    private final CommentaryRepository commentaryRepository;

    private final ImageDataRepository imageDataRepository;

    private final AnalyzerService analyzerService;


    @PostMapping("/gallery/image/comment")
    public ResponseEntity<?> saveImageComment(@RequestBody CommentTO commentTO, Principal principal) {
        UserData userDataByBaseUserUsername = userDataRepository.getUserDataByBaseUserUsername(principal.getName());

        Commentary commentary = new Commentary();

        commentary.setText(commentTO.getText());
        commentary.setAuthor(userDataByBaseUserUsername.getBaseUser());

        Optional<Image> image = imageRepository.findById(Long.valueOf(commentTO.getImageId()));

        if (image.isPresent()) {
            ImageData imageData1 = image.get().getImageData();
            imageData1.getCommentaryList().add(commentary);
            commentary.setImageData(imageData1);
            imageRepository.saveAndFlush(image.get());
        }

        return ResponseEntity.ok("");
    }


    @PostMapping("/ai/image")
    public AnalyzeImageResponseTO analyzeImage(@RequestBody AnalyzeImageTO image) throws Exception{
        String base64Image = image.getBytes().split(",")[1];
        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        final int w = img.getWidth();
        final int h = img.getHeight();
        BufferedImage scaledImage = new BufferedImage((w / 10),(h / 10), BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance(0.1, 0.1);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(img, scaledImage);
        ImageIO.write(scaledImage, "PNG", new File("D:/image.png"));


        return new AnalyzeImageResponseTO(analyzerService.feedImage(scaledImage));
    }
}
