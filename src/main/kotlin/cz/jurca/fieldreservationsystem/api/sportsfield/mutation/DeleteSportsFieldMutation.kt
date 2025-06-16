package cz.jurca.fieldreservationsystem.api.sportsfield.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import cz.jurca.fieldreservationsystem.api.toApi
import cz.jurca.fieldreservationsystem.codegen.types.DeleteSportsFieldResult
import cz.jurca.fieldreservationsystem.domain.IdValidator
import cz.jurca.fieldreservationsystem.domain.ProvidesLoginUser
import cz.jurca.fieldreservationsystem.domain.SportFieldNotFound
import cz.jurca.fieldreservationsystem.domain.SportsFieldId
import cz.jurca.fieldreservationsystem.domain.error.ApiNotFoundError
import cz.jurca.fieldreservationsystem.domain.error.ApiNotManagerOrAdminError
import cz.jurca.fieldreservationsystem.domain.error.ApiNotResourceOwnerError
import cz.jurca.fieldreservationsystem.domain.error.NotManagerOrAdminError
import cz.jurca.fieldreservationsystem.domain.error.NotResourceOwnerError
import cz.jurca.fieldreservationsystem.platform.cache.CacheProvider
import cz.jurca.fieldreservationsystem.platform.cache.SPORTS_FIELD_KEY

@DgsComponent
class DeleteSportsFieldMutation(private val userProvider: ProvidesLoginUser, private val idValidator: IdValidator, private val cacheProvider: CacheProvider) {
    @DgsMutation
    suspend fun deleteSportsField(
        @InputArgument id: Int,
    ): DeleteSportsFieldResult =
        when (val validationResult = idValidator.existsSportsField(id)) {
            is SportFieldNotFound -> ApiNotFoundError({ "Sports field with id $id not found" })
            is SportsFieldId -> {
                val loginUser = userProvider.getLoginUser().getOrThrow()
                validationResult.delete(loginUser).fold(
                    ifLeft = { error ->
                        when (error) {
                            is NotManagerOrAdminError -> ApiNotManagerOrAdminError({ error.message })
                            is NotResourceOwnerError -> ApiNotResourceOwnerError({ error.message })
                        }
                    },
                    ifRight = { success ->
                        cacheProvider.evict(SPORTS_FIELD_KEY + id.toString())
                        success.toApi("Sports field deleted successfully by ${loginUser.username.value}")
                    },
                )
            }
        }
}