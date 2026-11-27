package CAR_HMS.infrastructure.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@Profile("!contracts")
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {

        http.authorizeHttpRequests { auth ->

            auth.requestMatchers(
                HttpMethod.GET,
                "/api/hikes",
                "/api/auth/csrf"
            ).permitAll()

            auth.requestMatchers(
                HttpMethod.POST,
                "/api/auth/login",
                "/api/hikers/register"
            ).permitAll()

            auth.requestMatchers(
                "/api/admin/**"
            ).hasRole("ADMINISTRATOR")

            auth.anyRequest().authenticated()
        }

        http.formLogin { login ->

            login.loginProcessingUrl("/api/auth/login")

            login.usernameParameter("email")
            login.passwordParameter("password")

            login.successHandler { _, response, _ ->
                response.status =
                    HttpServletResponse.SC_NO_CONTENT
            }

            login.failureHandler { _, response, _ ->
                response.status =
                    HttpServletResponse.SC_UNAUTHORIZED
            }
        }

        http.logout { logout ->

            logout.logoutUrl("/api/auth/logout")

            logout.invalidateHttpSession(true)
            logout.clearAuthentication(true)
            logout.deleteCookies("JSESSIONID")

            logout.logoutSuccessHandler { _, response, _ ->
                response.status =
                    HttpServletResponse.SC_NO_CONTENT
            }
        }

        http.exceptionHandling { exceptions ->

            exceptions.authenticationEntryPoint {
                _, response, _ ->

                response.status =
                    HttpServletResponse.SC_UNAUTHORIZED
            }

            exceptions.accessDeniedHandler {
                _, response, _ ->

                response.status =
                    HttpServletResponse.SC_FORBIDDEN
            }
        }

        http.requestCache { cache ->
            cache.disable()
        }

        return http.build()
    }
}