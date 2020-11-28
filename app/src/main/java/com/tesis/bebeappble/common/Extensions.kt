package com.tesis.bebeappble.common

fun FloatArray.isInitialized() : Boolean{
    for ( float in this ){
        if (float != 0.0f){
            return true
        }
    }
    return false
}