package pl.coddev.applu.enums

enum class AppSelectorStatus {
    ALL, SYSTEM, USER;

    operator fun next(): AppSelectorStatus {
        val ordinal = if (ordinal + 1 >= values().size) 0 else ordinal + 1
        return values()[ordinal]
    }
}
