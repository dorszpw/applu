package pl.coddev.applu.enums

enum class WidgetActions {
    TEXTFIELD_BUTTON, BUTTON1, BUTTON2, BUTTON3, BUTTON4, BUTTON5, BUTTON6, BUTTON7, BUTTON8,
    BUTTON_CLEAR, BUTTON_CLEAR_ALL, BUTTON_UNINSTALL, BUTTON_LAUNCH, OTHER, BUTTON_SELECTOR,
    ADDED_NEW_APP, BUTTON_LASTAPP1, BUTTON_LASTAPP2, BUTTON_LASTAPP3, BUTTON_LASTAPP4, BUTTON_LASTAPP5,
    BUTTON_LASTAPP6, BUTTON_LASTAPP7, BUTTON_LASTAPP8, NO_ACTION;

    operator fun next(): WidgetActions {
        val ordinal = if (ordinal + 1 >= values().size) 0 else ordinal + 1
        return values()[ordinal]
    }
}