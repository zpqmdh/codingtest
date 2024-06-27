package com.ssafy.codingtest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.codingtest.service.CodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodeController {
    private final CodeService codeService;
    CodeController (CodeService codeService) {
        this.codeService = codeService;
    }

   @PostMapping("/submit")
    public String submitCode(@RequestBody String data) {
       ObjectMapper mapper = new ObjectMapper();
       try {
           // JSON 문자열을 JsonNode 객체로 변환
           JsonNode jsonNode = mapper.readTree(data);
           String user = jsonNode.get("user").asText();
           String code = jsonNode.get("code").asText();

           codeService.makeFile(code); // 1. Solution.java 파일 생성하고 code 저장
           if (codeService.evaluate()) { // 2. Solution.java 채점하기
               return "정답입니다 !!! " + user + "님 축하드려요 :)";
           } else {
               return "틀렸습니다 ㅠㅠ " + user + "님 힘을 내서 다시 작성해보세요.";
           }
       } catch (Exception e) {
           e.printStackTrace();
           return "예외 발생";
       }
   }
}
