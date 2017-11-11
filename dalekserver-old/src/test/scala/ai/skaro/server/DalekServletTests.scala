package ai.skaro.server

import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike

class DalekServletTests extends ScalatraSuite with FunSuiteLike {

  addServlet(classOf[DalekServlet], "/*")

  test("GET / on DalekServlet should return status 200"){
    get("/"){
      status should equal (200)
    }
  }

}
