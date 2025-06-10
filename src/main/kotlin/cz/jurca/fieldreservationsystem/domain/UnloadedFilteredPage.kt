package cz.jurca.fieldreservationsystem.domain

import cz.jurca.fieldreservationsystem.codegen.types.SortByDirection
import cz.jurca.fieldreservationsystem.codegen.types.SportsFieldSortByField

class UnloadedFilteredPage<ITEM, FILTER, SORT_BY>(
    val pageSize: Int,
    val pageNumber: Int,
    val filters: FILTER?,
    val sortBy: SORT_BY?,
    private val dataLoader: suspend (page: UnloadedFilteredPage<ITEM, FILTER, SORT_BY>) -> Page<ITEM>,
) {
    suspend fun getData(): Page<ITEM> = dataLoader.invoke(this)

    fun getOffset(): Int = pageSize * (pageNumber - 1)

    class SportsFieldSortBy(
        val `field`: SportsFieldSortByField,
        val direction: SortByDirection,
    )
}