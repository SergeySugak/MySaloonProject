package com.app.mscorebase.common

interface Validator<T> {
    fun validate(value: T): ActionResult<Boolean>
}