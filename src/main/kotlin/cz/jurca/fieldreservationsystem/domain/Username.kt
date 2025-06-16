package cz.jurca.fieldreservationsystem.domain

@JvmInline
value class Username(val value: String) {
    init {
        require(value.isNotBlank()) { "Username cannot be blank" }
        require(value.length <= 50) { "Username cannot be longer than 50 characters" }
    }
}