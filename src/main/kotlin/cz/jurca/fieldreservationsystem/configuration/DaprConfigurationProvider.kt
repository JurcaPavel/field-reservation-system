package cz.jurca.fieldreservationsystem.configuration

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test & !local")
class DaprConfigurationProvider : ConfigurationProvider