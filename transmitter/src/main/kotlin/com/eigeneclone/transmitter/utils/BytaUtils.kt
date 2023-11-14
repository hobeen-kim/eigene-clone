package com.eigeneclone.transmitter.utils

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import java.util.function.Consumer
import java.util.zip.GZIPOutputStream

class BytaUtils {

    companion object {

        fun getBytaWithNewlines(data: List<String?>): ByteArray {
            ByteArrayOutputStream().use { baos ->
                PrintStream(baos, true, "utf-8").use { ps ->
                    data.forEach(Consumer { d: String? -> ps.println(d) })
                    ps.flush()
                    return baos.toByteArray()
                }
            }
        }

        fun compressByGzip(data: ByteArray): ByteArray {
            ByteArrayOutputStream(data.size).use { baos ->
                GZIPOutputStream(baos).use { gos ->
                    gos.write(data)
                    gos.finish()
                    return baos.toByteArray()
                }
            }
        }
    }
}