package com.example.starlet_be.openai

interface ModerationService {
    fun moderateText(text : String) : Boolean
}