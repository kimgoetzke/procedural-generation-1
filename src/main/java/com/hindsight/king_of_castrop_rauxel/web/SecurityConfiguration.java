package com.hindsight.king_of_castrop_rauxel.web;

import static org.springframework.security.config.Customizer.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private static final String H2_CONSOLE = "/h2-console/**";

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1)
  SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher(antMatcher(H2_CONSOLE))
        .authorizeHttpRequests(r -> r.requestMatchers(antMatcher(H2_CONSOLE)).permitAll())
        .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher(H2_CONSOLE)))
        .headers(headers -> headers.frameOptions(withDefaults()).disable())
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/**")
        .authorizeHttpRequests(r -> r.anyRequest().authenticated())
        .httpBasic(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    var testPlayer1 =
        User.builder()
            .username("player1")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
    var testPlayer2 =
        User.builder()
            .username("player2")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
    return new InMemoryUserDetailsManager(testPlayer1, testPlayer2);
  }
}
