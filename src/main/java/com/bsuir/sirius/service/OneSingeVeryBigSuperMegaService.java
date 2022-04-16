package com.bsuir.sirius.service;

import com.bsuir.sirius.dao.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OneSingeVeryBigSuperMegaService {
     private final ImageCollectionRepository imageCollectionRepository;
     private final ImageDataRepository imageDataRepository;
     private final ImageRepository imageRepository;
     private final TransactionHistoryRepository transactionHistoryRepository;
     private final UserDataRepository userDataRepository;



}
