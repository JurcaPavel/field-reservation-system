package cz.jurca.fieldreservationsystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FieldReservationSystemApplication

fun main(args: Array<String>) {
    runApplication<FieldReservationSystemApplication>(*args)
}