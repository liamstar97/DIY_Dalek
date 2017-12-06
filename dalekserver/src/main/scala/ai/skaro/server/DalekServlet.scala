package ai.skaro.server

import org.scalatra._

class DalekServlet extends ScalatraServlet with RXTXSerialInit {
  import Speed._

  get("/") {
    views.html.hello()
  }

  get("/driveForward") {
    incrR
    incrL
    drive
    views.html.hello()
  }

  get("/driveBack") {
    decrR
    decrL
    drive
    views.html.hello()
  }

  get("/driveLeft") {
    decrL
    incrR
    drive
    views.html.hello()
  }

  get("/driveRight") {
    decrR
    incrL
    drive
    views.html.hello()
  }

  get("/stop") {
    setStopSpeeds
    drive
    views.html.hello()
  }

  get("/updateSerial/:port") {
    serialClient.setDefaultPort(params("port"))
    views.html.hello()
  }

  def drive = {
    serialClient.write(s"drive r $right\n")
    serialClient.write(s"drive l $left\n")
  }
}

object Speed {
  import math._
  val speeds = Array(-100, -90, -80, -70, -60, 0, 60, 70, 80, 90, 100)
  val stopSpeedIdx = speeds.length / 2
  val maxSpeedIdx = speeds.length - 1

  var rIdx = stopSpeedIdx
  var lIdx = stopSpeedIdx

  def setStopSpeeds = { rIdx = stopSpeedIdx; lIdx = stopSpeedIdx }
  def right = speeds(rIdx)
  def left = speeds(lIdx)
  def incrL = lIdx = min(maxSpeedIdx, lIdx + 1)
  def incrR = rIdx = min(maxSpeedIdx, rIdx + 1)
  def decrL = lIdx = max(0, lIdx - 1)
  def decrR = rIdx = max(0, rIdx - 1)
}