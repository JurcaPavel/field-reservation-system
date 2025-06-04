package cz.jurca.fieldreservationsystem

import io.kotest.core.spec.style.AnnotationSpec

abstract class BaseTest : AnnotationSpec() {
    @Suppress("ktlint:standard:function-naming")
    protected fun Given(description: String = "") {
        description.isEmpty()
    }

    @Suppress("ktlint:standard:function-naming")
    protected fun When(description: String = "") {
        description.isEmpty()
    }

    @Suppress("ktlint:standard:function-naming")
    protected fun Then(description: String = "") {
        description.isEmpty()
    }

    @Suppress("ktlint:standard:function-naming")
    protected fun And(description: String = "") {
        description.isEmpty()
    }
}