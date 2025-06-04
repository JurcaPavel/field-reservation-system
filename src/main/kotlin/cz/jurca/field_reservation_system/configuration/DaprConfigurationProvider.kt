package cz.jurca.field_reservation_system.configuration

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test & !local")
class DaprConfigurationProvider : ConfigurationProvider {
}