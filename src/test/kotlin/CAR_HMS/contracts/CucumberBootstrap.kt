package CAR_HMS.contracts

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("contracts")
class CucumberBootstrap