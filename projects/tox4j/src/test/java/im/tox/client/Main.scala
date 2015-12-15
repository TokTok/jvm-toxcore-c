package im.tox.client

case object Main extends App {
  ToxClientOptions(args)(ProfileManager.run)
}
