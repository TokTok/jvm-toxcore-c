package im.tox.client

import java.io.PrintWriter
import java.lang.management.ManagementFactory
import java.net.URL

import codes.reactive.scalatime.Instant

/**
 * Contains some information about the host the client is running on.
 */
object HostInfo {

  private val startTime = Instant()

  val ipv4 = new Curl(new URL("http://64.182.208.184/"))
  val ipv6 = new Curl(new URL("http://[2604:7780:200:300::60]/"))

  private def printMemory(memory: Double)(out: PrintWriter): Unit = {
    val (count, unit) =
      if (memory > 1024 * 1024 * 1024) {
        (memory / 1024 / 1024 / 1024, "G")
      } else if (memory > 1024 * 1024) {
        (memory / 1024 / 1024, "M")
      } else if (memory > 1024) {
        (memory / 1024, "K")
      } else {
        (memory, "B")
      }

    out.print(Math.round(count * 100) / 100.0)
    out.println(unit)
  }

  def printSystemInfo(out: PrintWriter): Unit = {
    val runtime = Runtime.getRuntime
    val memory = ManagementFactory.getMemoryMXBean
    val memoryUsage = memory.getHeapMemoryUsage

    out.println(TestClient.toString)
    out.print("  Start time:           "); out.println(startTime)
    out.print("  Available processors: "); out.println(runtime.availableProcessors)
    out.print("  Free memory:          "); printMemory(runtime.freeMemory)(out)
    out.print("  Max memory:           "); printMemory(runtime.maxMemory)(out)
    out.print("  Total memory:         "); printMemory(runtime.totalMemory)(out)
    out.print("  Used (total - free):  "); printMemory(runtime.totalMemory - runtime.freeMemory)(out)
    out.print("  Usage (MemoryUsage):  "); out.println(memoryUsage)
    out.print("  IPv4 address:         "); out.println(HostInfo.ipv4)
    out.print("  IPv6 address:         "); out.println(HostInfo.ipv6)
  }

}
