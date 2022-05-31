package com.dggorbachev.todoapp.base.util

import com.dggorbachev.todoapp.base.functional.Either

inline fun <reified T> attempt(func: () -> T): Either<Throwable, T> = try {
    Either.Right(func.invoke())
} catch (e: Throwable) {
    Either.Left(e)
}

val <T> T.exhaustive: T
    get() = this