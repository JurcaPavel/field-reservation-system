package cz.jurca.fieldreservationsystem.api.sportsfield.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldResult
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportsFieldId

@DgsComponent
class SportsFieldQuery(private val validator: IdValidator) {
    @DgsQuery
    suspend fun sportsField(id: Int): SportsFieldResult =
        when (val idResult = validator.existsSportsField(id)) {
            is SportsFieldId -> idResult.getDetail().toApi()
            is SportFieldNotFound -> NotFoundError({ "Sports field with id $id not found" })
        }
}