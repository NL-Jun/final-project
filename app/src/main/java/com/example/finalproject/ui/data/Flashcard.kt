package com.example.finalproject.ui.data



import java.io.Serializable

data class Flashcard(
    val term: String = "",
    val definition: String = "",
    var id: String? = null,
    var isDefinitionVisible: Boolean = false

) : Serializable

