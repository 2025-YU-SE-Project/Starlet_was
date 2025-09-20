package com.example.starlet_be.openai

data class OpenAiResDto(
    val choices : List<Choice>
){
    data class Choice(
        val message : Message
    ){
        data class Message(
            val role : String,
            val content : String
        )
    }
}
