package xnav

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.scalatest.flatspec._

class MainAppSpec extends AnyFlatSpec{

  val myInValid = new ByteArrayInputStream(
    """8
      |A -> B: 240
      |A -> C: 70
      |A -> D: 120
      |C -> B: 60
      |D -> E: 480
      |C -> E: 240
      |B -> E: 210
      |E -> A: 300
      |route A -> B
      |nearby A, 130
      |""".stripMargin.getBytes)

  val myInInValid = new ByteArrayInputStream(
    """8
      |A B: 240
      |A -> C: 70
      |route A -> B
      |nearby A, 130
      |""".stripMargin.getBytes)

  val myInInValidEdge = new ByteArrayInputStream(
    """8
      A -> B: 240
      |A -> C: 70
      |A -> D: 120
      |C -> B: 60
      |D  E 480
      |C -> E: 240
      |B -> E: 210
      |E -> A: 300
      |route A -> B
      |nearby A, 130
      |""".stripMargin.getBytes)

  val noRouteFound = new ByteArrayInputStream(
    """8
      |A -> B: 240
      |A -> C: 70
      |A -> D: 120
      |B -> D: 60
      |B -> C: 480
      |B -> E: 240
      |C -> D: 210
      |D -> E: 300
      |route B -> A
      |""".stripMargin.getBytes)


  "Main App" should "work as expected providing valid data" in {
    val myOut = new ByteArrayOutputStream
    Console.withOut(myOut) {
      Console.withIn(myInValid){
        MainApp.main(Array.empty)
      }
    }
    val expectedRes: String =
      """A -> C -> B: 130
        |C: 70,D: 120,B: 130
        |""".stripMargin

    assertResult(expectedRes) {
      myOut.toString
    }
  }

  "Main App" should "print error invalid input data if number of edge provided is not matched with the first line" in {
    val myOut = new ByteArrayOutputStream
    Console.withOut(myOut) {
      Console.withIn(myInInValid){
        MainApp.main(Array.empty)
      }
    }
    assertResult(true) {
      myOut.toString.contains("Invalid input data")
    }
  }

  "Main App" should "print error edge data if the format is not correct" in {
    val myOut = new ByteArrayOutputStream
    Console.withOut(myOut) {
      Console.withIn(myInInValidEdge){
        MainApp.main(Array.empty)
      }
    }
    assertResult(true) {
      myOut.toString.contains("Invalid edge data")
    }
  }

  "Main App" should "print no route found if no route found" in {
    val myOut = new ByteArrayOutputStream
    Console.withOut(myOut) {
      Console.withIn(noRouteFound){
        MainApp.main(Array.empty)
      }
    }
    assertResult(true) {
      myOut.toString.contains("Error: No route from")
    }
  }

}
