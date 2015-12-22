package im.tox.sbt

/**
 * Derived from https://github.com/pfn/android-sdk-plugin/blob/master/src/NativeFinder.scala.
 */
import java.io.FileInputStream
import java.lang.reflect.Method
import javassist.util.proxy.{MethodFilter, MethodHandler, ProxyFactory}

import org.objectweb.asm._
import sbt._

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf"))
object NativeClassFinder extends ((Logger, Set[File]) => Map[String, Seq[String]]) {

  private final class Handler extends MethodHandler {

    private var currentClassName: Option[String] = None // scalastyle:ignore var.field
    var nativeList: Map[String, Seq[String]] = Map.empty // scalastyle:ignore var.field

    @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
    override def invoke(self: AnyRef, thisMethod: Method, proceed: Method, args: Array[AnyRef]): AnyRef = {
      thisMethod.getName match {
        case "visit" =>
          if (args.length > 2) {
            val className = args(2).asInstanceOf[String].replaceAll("/", ".")
            currentClassName = Some(className)
          }
        case "visitMethod" =>
          val access = args(0).asInstanceOf[Int]
          val name = args(1).asInstanceOf[String]
          if ((access & Opcodes.ACC_NATIVE) != 0) {
            currentClassName match {
              case None =>
                sys.error("Found method outside class")
              case Some(enclosingClassName) =>
                nativeList.get(enclosingClassName) match {
                  case None =>
                    nativeList += ((enclosingClassName, Seq(name)))
                  case Some(names) =>
                    nativeList += ((enclosingClassName, name +: names))
                }
            }
          }
        case methodName =>
          sys.error(s"Unhandled method: $methodName")
      }
      null
    }

  }

  override def apply(log: Logger, classes: Set[File]): Map[String, Seq[String]] = {
    val handler = new Handler

    val factory = new ProxyFactory()
    factory.setSuperclass(classOf[ClassVisitor])
    factory.setFilter(new MethodFilter {
      override def isHandled(m: Method): Boolean = Seq("visit", "visitMethod").contains(m.getName)
    })

    factory.create(Array(classOf[Int]), Array(Integer.valueOf(Opcodes.ASM4)), handler) match {
      case x: ClassVisitor =>
        classes.toSeq.sorted foreach { entry =>
          val in = new FileInputStream(entry)
          val r = new ClassReader(in)
          r.accept(x, 0)
          in.close()
        }

        handler.nativeList
    }
  }

}
