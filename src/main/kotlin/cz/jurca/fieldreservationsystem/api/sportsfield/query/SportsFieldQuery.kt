package cz.jurca.fieldreservationsystem.api.sportsfield.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.cache.CacheProvider
import cz.jurca.fieldreservationsystem.cache.SPORTS_FIELD_KEY
import cz.jurca.fieldreservationsystem.codegen.types.NotFoundError
import cz.jurca.fieldreservationsystem.codegen.types.SportsField
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldResult
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportsFieldId

@DgsComponent
class SportsFieldQuery(private val validator: IdValidator, private val cacheProvider: CacheProvider) {
    @DgsQuery
    suspend fun sportsField(
        @InputArgument id: Int,
    ): SportsFieldResult =
        // TODO think of a nicer way to cache
        cacheProvider.get(SPORTS_FIELD_KEY + id.toString(), SportsField::class.java)
            ?: when (val idResult = validator.existsSportsField(id)) {
                is SportsFieldId -> {
                    cacheProvider.put(SPORTS_FIELD_KEY + id.toString(), idResult.getDetail().toApi())
                }

                is SportFieldNotFound -> NotFoundError({ "Sports field with id $id not found" })
            }
}