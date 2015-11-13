package im.tox.tox4j.bench

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.picklers.Implicits._
import im.tox.tox4j.core._
import im.tox.tox4j.core.exceptions.ToxNewException
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl}
import org.scalameter.api._

import scala.collection.immutable
import scala.util.Random

/**
 * The two test base classes [[MemoryReport]] and [[TimingReport]] are based on this common class. It provides some
 * basic utility methods and the default base configuration. See the companion object for all the common dimensions used
 * throughout the benchmarks. Don't inherit from this class directly. Use its two subclasses, instead.
 *
 * See [[Confidence]] for notes on confidence levels in the benchmarks. Our benchmarks generally have a test granularity
 * of 10, meaning that we evenly divide the axis into 10 samples. For example for testing [[ToxCore.bootstrap]], we run
 * it with 10, 20, 30, ..., 100 iterations. We aim for the smallest number to take more than 1ms and the largest to take
 * less than 100ms. If the timing is on average somewhere between 10 and 100ms, you generally get useful results with
 * [[Confidence.normal]].
 *
 * Some methods are very fast and may need more iterations to give good results, since commodity computer timers often
 * have flaky timing at high resolutions. Some methods may take too much memory when ran with high values of their
 * dimensions. In such cases, consider using a higher [[Confidence]] configuration or simply increasing the value of
 * [[exec.benchRuns]] for a specific test.
 *
 * We generally aim for a confidence level of 95% and a confidence interval of less than 5% in each direction. Higher
 * confidence usually gives no better insight. Lower levels may turn out to be very flaky and hinder effective
 * regression testing.
 */
abstract class ToxBenchBase extends Bench.OfflineRegressionReport {

  protected def confidence = Confidence.normal

  final override def defaultConfig: Context = Context.empty ++ Context(
    verbose -> false,
    reports.resultDir -> "target/benchmarks",
    exec.jvmflags -> List("-Djava.library.path=" + sys.props("java.library.path")),
    exec.reinstantiation.frequency -> 1000,
    exec.reinstantiation.fullGC -> true
  ) ++ confidence

  /**
   * Helper methods to run with tupled generators. Tupled [[Gen]]s run with a cross product of their arguments, so use
   * these with care. You can easily run into combinatorial explosion.
   */
  def using[A, B](a: Gen[A], b: Gen[B]): Using[(A, B)] = using(Gen.crossProduct(a, b))
  def using[A, B, C](a: Gen[A], b: Gen[B], c: Gen[C]): Using[(A, B, C)] = using(Gen.crossProduct(a, b, c))
  def using[A, B, C, D](a: Gen[A], b: Gen[B], c: Gen[C], d: Gen[D]): Using[(A, B, C, D)] = using(Gen.crossProduct(a, b, c, d))

  /**
   * The same as the above, but adding [[toxInstance]] as last element of the tuple.
   */
  def usingTox[A](a: Gen[A]): Using[(A, ToxCore[Unit])] = using(a, toxInstance)
  def usingTox[A, B](a: Gen[A], b: Gen[B]): Using[(A, B, ToxCore[Unit])] = using(a, b, toxInstance)
  def usingTox[A, B, C](a: Gen[A], b: Gen[B], c: Gen[C]): Using[(A, B, C, ToxCore[Unit])] = using(a, b, c, toxInstance)

  /**
   * The same as the above, but adding [[toxAvInstance]] as last element of the tuple.
   */
  def usingToxAv[A](a: Gen[A]): Using[(A, ToxAv[Unit])] = using(a, toxAvInstance)
  def usingToxAv[A, B](a: Gen[A], b: Gen[B]): Using[(A, B, ToxAv[Unit])] = using(a, b, toxAvInstance)
  def usingToxAv[A, B, C](a: Gen[A], b: Gen[B], c: Gen[C]): Using[(A, B, C, ToxAv[Unit])] = using(a, b, c, toxAvInstance)

}

object ToxBenchBase {

  /**
   * We keep a private PRNG for various generators.
   */
  private val random = new Random

  /**
   * Set [[ToxOptions.startPort]] to a lower number so we have more ports to choose from. This reduces the chance of a
   * [[ToxNewException.Code.PORT_ALLOC]] error occurring before all old [[ToxCore]] instances have been garbage
   * collected.
   */
  private val toxOptions = ToxOptions(startPort = 30000)

  /**
   * Create a number of valid Tox Addresses. These addresses are guaranteed to pass checksum tests, but are very
   * unlikely to be owned by actual people.
   *
   * @param sz The number of Tox Addresses to generate.
   * @return A [[Seq]] of Tox Addresses in [[Byte]] arrays.
   */
  def friendAddresses(sz: Int): Seq[ToxFriendAddress] = {
    for (_ <- 0 until sz) yield {
      ToxCoreFactory.withTox(_.getAddress)
    }
  }

  /**
   * Create a number of valid public keys. These pass the validity tests in toxcore, but may not have private key
   * counterparts.
   *
   * @param sz The number of public keys to generate.
   * @return A [[Seq]] containing public keys in [[Byte]] arrays.
   */
  def friendKeys(sz: Int): Seq[ToxPublicKey] = {
    for (_ <- 0 until sz) yield {
      val key = Array.ofDim[Byte](ToxCoreConstants.PublicKeySize)
      // noinspection SideEffectsInMonadicTransformation
      random.nextBytes(key)
      // Key needs the last byte to be 0 or toxcore will complain about checksums.
      key(key.length - 1) = 0
      ToxPublicKey.unsafeFromByteArray(key)
    }
  }

  /**
   * Make a [[ToxCore]] instance with the common [[toxOptions]], set a name and status message, and add a number of
   * imaginary friends.
   *
   * @param friendCount The number of friends to add.
   * @return A new [[ToxCore]] instance with a name, status message, and friendCount friends.
   */
  def makeToxWithFriends(friendCount: Int): ToxCore[Unit] = {
    val tox = ToxCoreFactory.make[Unit](toxOptions)
    tox.setName(ToxNickname.unsafeFromByteArray(Array.ofDim(ToxCoreConstants.MaxNameLength)))
    tox.setStatusMessage(ToxStatusMessage.unsafeFromByteArray(Array.ofDim(ToxCoreConstants.MaxStatusMessageLength)))
    friendKeys(friendCount) foreach tox.addFriendNorequest
    tox
  }

  /**
   * The same as [[makeToxWithFriends]], but only adds a single friend.
   *
   * The [[toxInstance]] generator uses this to create its values. Use this if you have more specific needs than what
   * the generator provides (e.g. if you need to mutate its state).
   *
   * @return A new [[ToxCore]] instance with a name, status message, and 1 friend.
   */
  def makeTox(): ToxCore[Unit] = {
    makeToxWithFriends(1)
  }

  // Base generators.

  /**
   * Generator for a [[ToxCore]] instance. Note that this instance may be shared between many test runs, so your test
   * should not mutate it. If it does, it needs to ensure that it returns to an equivalent state as before the test
   * began. In particular, if you add friends, you need to ensure that you remove all but 1 friends on tearDown.
   */
  val toxInstance = Gen.single("tox")(classOf[ToxCoreImpl[Unit]]).map(_ => makeTox()).cached

  /**
   * Generator for a [[ToxAv]] instance.
   */
  val toxAvInstance = toxInstance.map(tox => new ToxAvImpl[Unit](tox.asInstanceOf[ToxCoreImpl[Unit]]): ToxAv[Unit]).cached

  /**
   * Helper function to create a range axis evenly divided into 10 samples. The range starts with `upto / 10` and ends
   * with `upto`.
   *
   * This method requires the end value to be divisible by 10. If you have specific needs that require an indivisible
   * end number, use [[Gen.range]] directly.
   *
   * @param axisName The name of the dimension.
   * @param upto The highest value this generator will produce.
   * @return A generator from `upto / 10` to `upto`.
   */
  def range(axisName: String)(upto: Int): Gen[Int] = {
    require(upto % 10 == 0)
    Gen.range(axisName)(upto / 10, upto, upto / 10)
  }

  val nodes = range("nodes")(100)
  val instances = range("instances")(100)

  def friends: Int => Gen[Int] = range("friends")
  val friends1k = friends(1000)
  val friends10k = friends(10000)

  def iterations: Int => Gen[Int] = range("iterations")
  val iterations1k = iterations(1000)
  val iterations10k = iterations(10000)
  val iterations100k = iterations(100000)
  val iterations1000k = iterations(1000000)

  val nameLengths = Gen.range("name length")(0, ToxCoreConstants.MaxNameLength, 8)
  val statusMessageLengths = Gen.range("status message length")(0, ToxCoreConstants.MaxStatusMessageLength, 100)

  // Derived generators

  /**
   * A caching function object to create a single Tox instance with a number of friends. The caching ensures that for
   * each friend count, there is exactly one instance with that number of friends.
   *
   * Experiments have shown that this custom caching takes 2.3GB for a 10-step range of 1000-10000 friends instead of
   * 2.7GB when using [[org.scalameter.Gen.cached]]. It is also about 15% faster.
   *
   * Do not mutate objects returned by this function.
   */
  object toxWithFriends extends (Int => ToxCore[Unit]) with Serializable {
    /**
     * [[immutable.HashMap]] was chosen here for its semantics, not efficiency. A [[Vector]][([[Int]], [[ToxCore]])] or
     * an [[Array]] would possibly be faster, but the map is easier to use.
     */
    @transient
    private var toxesWithFriends = immutable.HashMap.empty[Int, ToxCore[Unit]]

    override def apply(sz: Int): ToxCore[Unit] = {
      toxesWithFriends.get(sz) match {
        case Some(tox) =>
          tox
        case None =>
          val tox = makeToxWithFriends(sz)
          toxesWithFriends = toxesWithFriends.updated(sz, tox)
          tox
      }
    }
  }

  val toxWithFriends1k = friends1k map toxWithFriends
  val toxWithFriends10k = friends10k map toxWithFriends

  /**
   * Extract a random friend list from a Tox instance. If limit is 0 or omitted, extract the entire friend list. If
   * limit is non-zero, a slice of the friend list is taken with at most that size.
   *
   * The friend list is randomly shuffled before it is returned, so each time this function is called, you will get a
   * different list.
   *
   * @param limit Maximum number of friends to return. To get the same number on every run, choose at most the minimum
   *              number of friends generated by your chosen generator.
   * @param tox The Tox instance to extract the friends from.
   * @return A pair containing the passed Tox instance and a random slice of the friend list.
   */
  def toxAndFriendNumbers(limit: Int = 0)(tox: ToxCore[Unit]): (Seq[Int], ToxCore[Unit]) = {
    val friendList = random.shuffle(tox.getFriendList.toSeq)
    if (limit != 0) {
      (friendList.slice(0, limit), tox)
    } else {
      (friendList, tox)
    }
  }

  /**
   * The same as [[toxAndFriendNumbers]] but returns the friends' public keys instead of friend numbers.
   *
   * @param limit Maximum number of friends to return. To get the same number on every run, choose at most the minimum
   *              number of friends generated by your chosen generator.
   * @param tox The Tox instance to extract the friends from.
   * @return A pair containing the passed Tox instance and a random slice of the friend list.
   */
  def toxAndFriendKeys(limit: Int)(tox: ToxCore[Unit]): (Seq[ToxPublicKey], ToxCore[Unit]) = {
    toxAndFriendNumbers(limit)(tox) match {
      case (friendList, _) => (friendList map tox.getFriendPublicKey, tox)
    }
  }

  /**
   * Produces [[instances]] valid Tox save data arrays as produced by [[ToxCore.getSavedata]].
   */
  val toxSaves = {
    for (sz <- instances) yield {
      for (_ <- 0 until sz) yield {
        ToxOptions(saveData = SaveDataOptions.ToxSave(makeTox().getSavedata))
      }
    }
  }

  val names = nameLengths.map(Array.ofDim[Byte]).map(ToxNickname.unsafeFromByteArray)
  val statusMessages = statusMessageLengths.map(Array.ofDim[Byte]).map(ToxStatusMessage.unsafeFromByteArray)

}
