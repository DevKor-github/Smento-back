package devkor.ontime_back.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseInitialization {

    @PostConstruct
    public void initialize() {
        try {
//            FileInputStream serviceAccount =
//                    new FileInputStream("C:/Users/junbeom/Desktop/24-2/Devkor/Ontime/ontime-back/src/main/resources/ontime-push-firebase-adminsdk-gnpxs-7d098872ff.json");

            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("ontime-push-firebase-adminsdk-gnpxs-7d098872ff.json");
            if (serviceAccount == null) {
                throw new FileNotFoundException("Resource not found: ontime-push-firebase-adminsdk-gnpxs-7d098872ff.json");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
