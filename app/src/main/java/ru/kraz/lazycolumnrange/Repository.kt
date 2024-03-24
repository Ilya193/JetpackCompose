package ru.kraz.lazycolumnrange

interface Repository {

    suspend fun fetchTodos(): List<TodoCloud>

    class Base(
        private val service: Service
    ) : Repository {
        override suspend fun fetchTodos(): List<TodoCloud> = service.fetchTodos()

    }

}