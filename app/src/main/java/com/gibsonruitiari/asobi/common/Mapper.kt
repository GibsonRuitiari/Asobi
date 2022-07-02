package com.gibsonruitiari.asobi.common

interface Mapper<in Input,out Output>{
    fun map(input: Input):Output
}

