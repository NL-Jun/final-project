package com.example.finalproject.ui.data

data class TodoData(
    var taskId: String,
    var task: String,
    var description: String = "" //set a default value of an empty string to handle cases of description might not given initially
)
