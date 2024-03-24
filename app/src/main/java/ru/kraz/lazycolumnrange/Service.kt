package ru.kraz.lazycolumnrange

import retrofit2.http.GET

interface Service {
    @GET("todos")
    suspend fun fetchTodos(): List<TodoCloud>
}