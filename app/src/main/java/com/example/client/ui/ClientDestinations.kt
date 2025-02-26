package com.example.client.ui

sealed interface ClientDestination {
    val route: String
}

data object HomeScreen : ClientDestination {
    override val route: String = "home"
}

data object GetAllScreen : ClientDestination {
    override val route: String = "get_all"
}

data object Top10BySubjectScreen : ClientDestination {
    override val route: String = "top_10_by_subject"
}

data object Top10BySumScreen : ClientDestination {
    override val route: String = "top_10_by_sum"
}

data object SearchScreen : ClientDestination {
    override val route: String = "search"
}