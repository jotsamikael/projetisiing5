package com.example.agriweb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        return new AgriwebUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .antMatchers("/users/**").hasAnyAuthority("admin")
               .antMatchers("/categories/**").hasAnyAuthority("admin","editor")
               .antMatchers("/reviews/**").hasAnyAuthority("admin","assitant")
               .antMatchers("/customers/**").hasAnyAuthority("admin", "salesperson")
               .antMatchers("/orders/**").hasAnyAuthority("admin", "salesperson","delivery")
               .antMatchers("/reports/**").hasAnyAuthority("admin", "salesperson")
               .antMatchers("/settings/**").hasAnyAuthority("admin")






               .anyRequest().authenticated()
               .and()
               .formLogin()
               .usernameParameter("email")
                  .loginPage("/login")
                    .defaultSuccessUrl("/",true)
                   .permitAll()
                     .and().logout().permitAll()
                      .and().rememberMe().key("AgriWeb")
       .tokenValiditySeconds(1*24*60*60); //one day in seconds


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/img/**", "/js/**").antMatchers("https://maxcdn.bootstrapcdn.com/");
    }
}
