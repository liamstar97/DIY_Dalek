import ai.skaro.server._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with RXTXSerialInit {

  override def init(context: ServletContext) {
    initializeSerialClient()
    context.mount(new DalekServlet, "/*")
  }

  override def destroy(context: ServletContext) {
    destroySerialClient()
    super.destroy(context)
  }
}
