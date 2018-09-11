package fi.vm.yti.comments.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "fi.vm.yti.comments.*")
@EnableJpaRepositories("fi.vm.yti.comments.*")
@EnableTransactionManagement
@EntityScan("fi.vm.yti.comments.*")
public class CommentsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CommentsApplication.class, args);
    }
}