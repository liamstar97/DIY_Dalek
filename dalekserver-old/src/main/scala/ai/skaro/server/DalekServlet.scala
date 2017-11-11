package ai.skaro.server

import org.scalatra._

class DalekServlet extends ScalatraServlet {
  import Speed._
  import SerialPortIO._

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
    incrR
    decrL
    drive
    views.html.hello()
  }

  get("/stop") {
    right = 0
    left = 0
    drive
    views.html.hello()
  }

  get("/updateStep/:step") {
    updateSTEP(params("step").toInt)
    views.html.hello()
  }

  get("/updateMin/:min") {
    updateMINVAL(params("min").toInt)
    views.html.hello()
  }

  get("/updateSerial/:port") {
    updateSerialPort(params("port"))
    views.html.hello()
  }
}

object Speed {
  var right = 0
  var left = 0
  var STEP = 20
  var MINVAL = 50
  def updateMINVAL(min: Int) = { MINVAL = min }
  def updateSTEP(step: Int) = { STEP = step }
  def incrL = {
    left = 
      if (left >= 100 - STEP) { 100 } else
      if (left > MINVAL - STEP) { left + STEP } else 
      if (left < MINVAL && left >= 0) { MINVAL } else 
      if (left < 0 && left > -MINVAL) { 0 } else
      if (left < -(MINVAL + STEP)) { left + STEP } else {
        -100
      }
  }
  def incrR = {
    right = 
      if (right >= 100 - STEP) { 100 } else
      if (right > MINVAL - STEP) { right + STEP } else 
      if (right < MINVAL && right >= 0) { MINVAL } else 
      if (right < 0 && right > -MINVAL) { 0 } else
      if (right < -(MINVAL + STEP)) { right + STEP } else {
        -100
      }
  }
  def decrL = {
    left = 
      if (left >= 100 - STEP) { left - STEP } else
      if (left > MINVAL - STEP) { left - STEP } else 
      if (left < MINVAL && left >= 0) { 0 } else 
      if (left < 0 && left > -MINVAL) { -MINVAL } else
      if (left < -(MINVAL + STEP) && left > -100 + STEP) { left - STEP } else {
        -100
      }
  }
  def decrR = {
    right = 
      if (right >= 100 - STEP) { right - STEP } else
      if (right > MINVAL - STEP) { right - STEP } else 
      if (right < MINVAL && right >= 0) { 0 } else 
      if (right < 0 && right > -MINVAL) { -MINVAL } else
      if (right < -(MINVAL + STEP) && right > -100 + STEP) { right - STEP } else {
        -100
      }
  }
}
  

object SerialPortIO {
  import gnu.io.CommPortIdentifier
  import gnu.io.SerialPort
  import Speed._

  var defaultPort = "ttyS80"
  var serial = serialPort(defaultPort)
  var serialOut = serial.getOutputStream

  def drive = {
    serialOut.write(s"drive r $right\n".getBytes)
    serialOut.write(s"drive l $left\n".getBytes)
  }
  def updateSerialPort(newPort: String) = { defaultPort = newPort }
  def serialPort(dev: String) = {
    val portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/" + dev)
    val commPort = portIdentifier.open("whatever", 2000)
    val serialPort = commPort.asInstanceOf[SerialPort]
    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
    consolePrintThread(serialPort).start()
    serialPort  
  }
  def consolePrintThread(serialPort: SerialPort) = {
    val in = serialPort.getInputStream
    val outThread = new java.lang.Thread() { 
      override def run() { 
        val buf = new Array[Byte](1024); 
        var len = -1; 
        do { 
          len = in.read(buf); 
          if (len > 0) {
            System.out.print(new String(buf, 0, len))
          } 
        } while (len > -1); 
      } 
    }
    outThread
  }
}
