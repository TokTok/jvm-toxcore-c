package im.tox.optimiser

object TestClass {

  def add(int: Int): Seq[Int] = {
    var x = List(1, 2, 3)

    var y = int
    y += 1
    y += 1
    y += 1
    x ::= y

    x
  }

  def main(args: Array[String]): Unit = {
    val x = add(5)
    println(x)
  }

}
