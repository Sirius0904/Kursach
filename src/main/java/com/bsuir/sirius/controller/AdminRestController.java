package com.bsuir.sirius.controller;

import com.bsuir.sirius.service.AdminService;
import com.bsuir.sirius.to.rest.response.UserTransactionListResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {
    private final AdminService adminService;


    @GetMapping(value = {"/transactions/{username}", "/transactions"})
    public ResponseEntity<UserTransactionListResponseTO> getTransactions(@PathVariable(required = false) String username){
        return ResponseEntity.ok(adminService.getUserTransactions(username));
    }
}
