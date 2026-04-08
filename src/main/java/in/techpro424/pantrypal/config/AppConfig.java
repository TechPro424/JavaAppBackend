package in.techpro424.pantrypal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {


        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/api/**").permitAll()
                .anyRequest().permitAll()
        );


        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
        );

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}