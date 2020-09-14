package com.example.coroutine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CoroutineTest {

    @Test
    fun `runBlockingのとき`() {
        var a: Deferred<String>? = null
        var b: Deferred<String>? = null
        try {
            runBlocking {
                println(1)
                a = async {
                    doA()
                }
                println(2)
                b = async {
                    doB()
                }
                println(3)
                val c = async {
                    val inner = innerDo()
                    println(inner)
                    inner
                }
                println(c.await())
                println(c.await())
                println(c.await())
                try {
                    println(b?.await())
                } catch (e: Throwable) {
                    println(e.message)
                }
                println(a?.await())
            }
            println(4)
        } catch (e: Exception) {
            println(e)
        } finally {
            println(a?.isCancelled)
            println(b?.isCancelled)
        }
    }

    suspend fun innerDo(): String = coroutineScope {
        println(11)
        val a = async {
            doA()
        }
        println(12)
        val b = async {
            doA()
        }
        println(13)
        try {
            throw NullPointerException("null")
        } catch (e: Exception) {
            println(e.message)
        }
        "inner"
    }

    suspend fun doA(): String {
        println("a start")
        delay(1000)
        println("a end")
        return "A"
    }

    suspend fun doB(): String {
        println("b start")
        delay(3000)
        throw IllegalStateException("aaaaaaaaaaaaaaaa")
        return "B"
    }
}
