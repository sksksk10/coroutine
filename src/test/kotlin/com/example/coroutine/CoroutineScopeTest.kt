package com.example.coroutine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.jupiter.api.Test

class CoroutineScopeTest {

    @Test
    fun `coroutineScopeでawaitでtry-catch`() = runBlocking {
        var a: Deferred<String>? = null
        var b: Deferred<String>? = null
        try {
            coroutineScope {
                a = async { doA() }
                b = async { doB() }
                try {
                    println(b?.await())
                } catch (e: Throwable) {
                    println(e.message) // キャッチしても伝播する キャンセルされる
                }
                println(a?.await()) // なので入らない
            }
        } catch (e: Exception) {
            println(e) // 入る
        } finally {
            println("a is cancelled? " + a?.isCancelled) // true
            println("b is cancelled? " + b?.isCancelled) // true
        }
    }

    @Test
    fun `coroutineScopeでasync内でtry-catch`() = runBlocking {
        var a: Deferred<String>? = null
        var b: Deferred<String>? = null
        try {
            coroutineScope {
                a = async { doA() }
                b = async {
                    try {
                        doB()
                    } catch (e: Throwable) {
                        e.message ?: "BでERROR" // 伝播しない 握りつぶす（さほど重要でないのを呼び出して継続させたいときとか）
                    }
                }
                println(b?.await())
                println(a?.await()) // なので入る
            }
        } catch (e: Exception) {
            println(e) // 入らない
        } finally {
            println("a is cancelled? " + a?.isCancelled) // false
            println("b is cancelled? " + b?.isCancelled) // false
        }
    }

    @Test
    fun `supervisorScopeでawaitでtry-catch`() = runBlocking {
        var a: Deferred<String>? = null
        var b: Deferred<String>? = null
        try {
            supervisorScope {
                a = async { doA() }
                b = async { doB() }
                try {
                    println(b?.await())
                } catch (e: Throwable) {
                    println(e.message) // 伝播しない bだけキャンセル
                }
                println(a?.await()) // なので入る
            }
        } catch (e: Exception) {
            println(e)
        } finally {
            println("a is cancelled? " + a?.isCancelled) // false
            println("b is cancelled? " + b?.isCancelled) // true
        }
    }

    suspend fun doA(): String {
        println("A start")
        delay(3000)
        println("A end")
        return "A"
    }

    suspend fun doB(): String {
        println("B start")
        delay(1000)
        error("Bでエラー発生")
        return "B"
    }
}
