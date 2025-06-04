package cz.jurca.fieldreservationsystem.configuration

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Primary
@Component
@Profile("local")
class LocalConfigurationProvider : ConfigurationProvider