package at.ac.tuwien.sepr.groupphase.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyAppConfig {

    @Value("${myapp.userCount}")
    private Integer userCount;

    @Getter
    @Value("${myapp.seed}")
    private Long seed;

    public Integer getUserCount() {
        //default config
        if (userCount == null) {
            return 10;
        }

        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
}
