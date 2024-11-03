//package com.uplus.ggumi.controller;
//
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@RestController
//public class EmbeddingController {
//
//    private final EmbeddingModel embeddingModel;
//
//    @Autowired
//    public EmbeddingController(EmbeddingModel embeddingModel) {
//        this.embeddingModel = embeddingModel;
//    }
//
//    @GetMapping("/ai/embedding")
//    public Map embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
//        return Map.of("embedding", embeddingResponse);
//    }
//}