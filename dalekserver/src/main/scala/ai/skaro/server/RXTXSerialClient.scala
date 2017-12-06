package ai.skaro.server

import java.io.{IOException, InputStream, OutputStream}

import gnu.io.CommPortIdentifier
import gnu.io.PortInUseException
import gnu.io.NoSuchPortException
import gnu.io.SerialPort
import org.slf4j.LoggerFactory

class RXTXSerialClient {
  val logger = LoggerFactory.getLogger(getClass)

  def initSerialPort(dev: String) = {
    try {
      val portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/" + dev)
      val commPort = portIdentifier.open("/dev/" + dev, 2000)
      val serialPort = commPort.asInstanceOf[SerialPort]
      serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
      Some(serialPort)
    } catch {
      case piu: PortInUseException =>
        logger.warn("couldn't open " + dev, piu)
        None
      case nsp: NoSuchPortException =>
        logger.warn("no port of that name, try another one! (" + dev + ")", nsp)
        None
    }
  }


  private var defaultPort = "ttyS80"
  private var serial: Option[SerialPort] = None
  private var out: Option[OutputStream] = None
  private var in: Option[InputStream] = None
  private var responseThread: Option[Thread] = None

  def setDefaultPort(newDefaultPort: String) = defaultPort = newDefaultPort

  def write(str: String) = {
    try {
      if (out.isEmpty) { reload }
      out.foreach(_.write(str.getBytes))
    } catch {
      case e: IOException =>
        logger.warn("unable to write: " + str, e)
        reload
    }
  }

  def reload = {
    destroy
    serial = initSerialPort(defaultPort)
    out = serial.map(_.getOutputStream)
    in = serial.map(_.getInputStream)
    responseThread = in.map(inputStream => new java.lang.Thread() {
      override def run() {
        val buf = new Array[Byte](1024);
        var len = -1
        do {
          // TODO / FIXME: make sure an IOException here doesn't kill everything
          len = inputStream.read(buf)
          if (len > 0) {
            logger.info(new String(buf, 0, len))
          }
        } while (len > -1)
      }
    })
    responseThread.foreach(_.start())
  }

  def destroy = {
    out.foreach(_.close())
    in.foreach(_.close())
    serial.foreach(_.close())
    responseThread.foreach(_.interrupt())
  }
}

trait RXTXSerialInit {
  val logger = LoggerFactory.getLogger(getClass)

  lazy val serialClient = new RXTXSerialClient

  def initializeSerialClient() = {
    serialClient.reload
  }

  def destroySerialClient() = {
    serialClient.destroy
  }
}