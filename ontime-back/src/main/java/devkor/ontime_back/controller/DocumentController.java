package devkor.ontime_back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @GetMapping("/terms")
    public String getTerms() {
        return "이용약관 내용 여기에 작성";
    }

    @GetMapping("/privacy")
    public String getPrivacyPolicy() {
        return "개인정보처리방침 내용 여기에 작성";
    }
}
