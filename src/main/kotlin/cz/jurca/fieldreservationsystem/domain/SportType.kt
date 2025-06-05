package cz.jurca.fieldreservationsystem.domain

enum class SportType {
    BASKETBALL,
    BEACH_VOLLEYBALL,
    SOCCER,
    TENNIS,
    ;

    fun toApi(): cz.jurca.fieldreservationsystem.codegen.types.SportType {
        return when (this) {
            BASKETBALL -> cz.jurca.fieldreservationsystem.codegen.types.SportType.BASKETBALL
            BEACH_VOLLEYBALL -> cz.jurca.fieldreservationsystem.codegen.types.SportType.BEACH_VOLLEYBALL
            SOCCER -> cz.jurca.fieldreservationsystem.codegen.types.SportType.SOCCER
            TENNIS -> cz.jurca.fieldreservationsystem.codegen.types.SportType.TENNIS
        }
    }

    companion object {
        // todo probably not ideal to take api in account in domain
        fun fromApi(type: cz.jurca.fieldreservationsystem.codegen.types.SportType): SportType =
            when (type) {
                cz.jurca.fieldreservationsystem.codegen.types.SportType.BASKETBALL -> BASKETBALL
                cz.jurca.fieldreservationsystem.codegen.types.SportType.BEACH_VOLLEYBALL -> BEACH_VOLLEYBALL
                cz.jurca.fieldreservationsystem.codegen.types.SportType.SOCCER -> SOCCER
                cz.jurca.fieldreservationsystem.codegen.types.SportType.TENNIS -> TENNIS
            }
    }
}