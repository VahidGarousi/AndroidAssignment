package ir.miare.feature.player.presentation


sealed class LoadableData<out T> {
    abstract val data: T?

    data object NotLoaded : LoadableData<Nothing>() {
        override val data = null
    }

    data object Loading : LoadableData<Nothing>() {
        override val data = null
    }

    data class Loaded<T>(
        override val data: T,
    ) : LoadableData<T>()

    data class Error(
        val error: Throwable,
    ) : LoadableData<Nothing>() {
        override val data = null
    }
}

val LoadableData<*>.isLoading: Boolean
    get() = this is LoadableData.Loading

fun <T> LoadableData<T>.copy(
    data: T? = this.data,
): LoadableData<T> = when (this) {
    is LoadableData.Error -> LoadableData.Error(error = error)
    is LoadableData.NotLoaded -> LoadableData.NotLoaded
    is LoadableData.Loaded<T> -> {
        if (data != null) {
            this.copy(data = data)
        } else {
            this.copy()
        }
    }

    is LoadableData.Loading -> LoadableData.Loading
}
