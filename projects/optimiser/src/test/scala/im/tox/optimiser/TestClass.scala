package im.tox.optimiser

object TestClass {

  def add(int: Int): Int = {
    var y = int
    y += 1
    y += 2
    y += 3

    y
  }

  def main(args: Array[String]): Unit = {
    val x = add(5)
    println(x)
  }

}
