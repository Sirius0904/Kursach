package com.bsuir.sirius.controller;

import com.bsuir.sirius.entity.Commentary;
import com.bsuir.sirius.entity.Image;
import com.bsuir.sirius.entity.ImageData;
import com.bsuir.sirius.entity.UserData;
import com.bsuir.sirius.repository.CommentaryRepository;
import com.bsuir.sirius.repository.ImageDataRepository;
import com.bsuir.sirius.repository.ImageRepository;
import com.bsuir.sirius.repository.UserDataRepository;
import com.bsuir.sirius.to.rest.request.CommentTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SiriusRestController {
    private final ImageRepository imageRepository;
    private final UserDataRepository userDataRepository;
    private final CommentaryRepository commentaryRepository;

    private final ImageDataRepository imageDataRepository;


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
}