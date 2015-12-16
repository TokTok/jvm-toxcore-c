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

  private def printDouble(value: Double)(out: PrintWriter): Unit = {
    out.print(Math.round(value * 100) / 100.0)
  }

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

    printDouble(count)(out)
    out.println(unit)
  }

  private def printGcStatistics(out: PrintWriter): Unit = {
    out.println("GC statistics:")
    val uptime = ManagementFactory.getRuntimeMXBean.getUptime
    out.print("    VM uptime:        "); out.print(uptime); out.println(" ms")
    val gcMxs = ManagementFactory.getGarbageCollectorMXBeans.iterator()
    while (gcMxs.hasNext) {
      val gcMx = gcMxs.next()
      out.print("  "); out.print(gcMx.getName); out.println(":")
      out.print("    Collection count: "); out.print(gcMx.getCollectionCount); out.println()
      out.print("    Collection time:  "); out.print(gcMx.getCollectionTime)
      out.print(" ms (ratio="); printDouble(gcMx.getCollectionTime.toDouble / uptime)(out); out.println(")")
    }
  }

  def printSystemInfo(out: PrintWriter): Unit = {
    val runtime = Runtime.getRuntime

    out.println(TestClient.toString)
    out.print("  Start time:           "); out.println(startTime)
    out.print("  Available processors: "); out.println(runtime.availableProcessors)
    out.print("  Free memory:          "); printMemory(runtime.freeMemory)(out)
    out.print("  Max memory:           "); printMemory(runtime.maxMemory)(out)
    out.print("  Total memory:         "); printMemory(runtime.totalMemory)(out)
    out.print("  Used (total - free):  "); printMemory(runtime.totalMemory - runtime.freeMemory)(out)
    out.print("  IPv4 address:         "); out.println(HostInfo.ipv4)
    out.print("  IPv6 address:         "); out.println(HostInfo.ipv6)
    out.println()

    printGcStatistics(out)
  }

}
