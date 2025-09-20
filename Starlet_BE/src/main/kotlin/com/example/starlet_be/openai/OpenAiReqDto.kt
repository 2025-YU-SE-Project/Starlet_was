package com.example.starlet_be.openai

data class OpenAiReqDto(
    val model : String,
    val message : List<String>
){
    data class Message(
        val role : String,
        val content : String
    )
}
