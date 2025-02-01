package devkor.ontime_back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// 부하테스트를 위해 추가한 컨트롤러
// 로드밸런서가 GET '/health' API를 이용해 인스턴스가 정상적으로 동작하는지 확인함
@RestController
public class AppController {


    @GetMapping("/health")
    public String healthCheck() {
        return "Success Health Check";
    }
}