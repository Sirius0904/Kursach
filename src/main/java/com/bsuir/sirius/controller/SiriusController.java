package com.bsuir.sirius.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiriusController {

    @GetMapping(value = "/")
    public String getIndexPage(){
        return "index";
    }

    @GetMapping(value = "/collection")
    public String getPathPage(){
        return "collection";
    }

    @GetMapping(value = "/profile")
    public String getProfilePage(){return "profile"; }

    @GetMapping(value = "/pinkColl")
    public String getFirstPage(){
        return "pinkColl";
    }

    @GetMapping(value = "/darkColl")
    public String getSecondPage(){
        return "darkColl";
    }

    @GetMapping(value = "/girlColl")
    public String getThirdPage(){return "girlColl";}

}

