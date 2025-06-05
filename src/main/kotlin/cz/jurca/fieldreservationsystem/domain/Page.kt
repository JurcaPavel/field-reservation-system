package cz.jurca.fieldreservationsystem.domain

class Page<T>(
    val totalItems: Int,
    val items: List<T>,
)