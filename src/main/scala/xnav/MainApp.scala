package xnav

import scala.collection.mutable._
import scala.io.StdIn
import scala.util.{Success, Try}

object MainApp extends App {

  // Initialize the weighted graph with default weight 1
  val graph = new WeightedGraph(1)
  println("Please input your text. Leaving an empty line will indicate end of the input.")

  // Get all the inputs from StdIn
  @inline def defined(line: String) = { line != null && line.nonEmpty }
  val allLines = Iterator.continually(StdIn.readLine).takeWhile(defined(_)).toList

  // Find the number of edges from line 1 and add all nodes and edges to the graph
  Try(allLines.head.toInt) match {
    case Success(noOfEdges) =>
      loadAllNodesAndEdges(noOfEdges, allLines)
      // Get all the queries and process one by one
      val allQueries = allLines.slice(noOfEdges + 1, allLines.size)
      runAllQueries(allQueries)
    case _ =>
      println("First line shoube an Int indicating total number of edges")
  }

  // print the shortest path
  def printPath[G <: AGraph](start: G#Node, target: G#Node,
                             distance: Map[G#Node, Double], path: Map[G#Node, G#Node]): Unit = {
    var shortest = List(target)
    if(path.size > 0 && path.contains(target)) {
      while (shortest.head != start) {
        shortest ::= path(shortest.head)
      }
      println(shortest.map(_.name).mkString(" -> ")+": "+distance(target).toInt)
    } else {
      println(s"Error: No route from ${start.name} to ${target.name}")
    }
  }

  def loadAllNodesAndEdges(noOfEdges: Int, allLines: List[String]) = {
    (1 to noOfEdges).map {
      i =>
        Try(allLines(i)) match {
          case Success(edge) =>
            val splittedEdge = edge.split(":")
            (splittedEdge.headOption, splittedEdge.lastOption) match {
              case (Some(edge), Some(duration)) =>
                val nodes = edge.split("->").map(_.trim)
                (nodes.headOption, nodes.lastOption, duration.toDoubleOption) match {
                  case (Some(node1), Some(node2), Some(dDuration)) =>
                    val n1 = graph.addNode(node1)
                    val n2 = graph.addNode(node2)
                    n1.connectWith(n2).setWeight(dDuration)
                  case _ =>
                    println("Invalid edge data")
                }
              case _ =>
                println("Invalid edge data")
            }
          case _ =>
            println("Invalid input data")
        }


    }
  }

  def runAllQueries(allQueries: List[String]) = {
    allQueries.foreach {
      item =>
        val isRouteQuery = item.startsWith("route")
        val isNearByQuery = item.startsWith("nearby")
        (isRouteQuery, isNearByQuery) match {
          case (true, false) =>
            // Route query finds shortest path from the start node and stops when it finds a target node
            val path = item.replace("route", "").split("->").map(_.trim)
            val node1 = graph.getNodeByName(path.head)
            val node2 = graph.getNodeByName(path.last)
            (node1, node2) match {
              case (Some(n1), Some(n2)) =>
                val (start, target) = (n1, n2)
                val solver = new Solver[graph.type](graph)
                solver.stopCondition = (S, D, P) => !S.contains(target)
                val (distance, paths) = solver.compute(start)
                printPath[graph.type](start, target, distance, paths)
              case _ =>
                println(s"Error: No route from ${path.head} to ${path.last}")
            }
          case (false, true) =>
            // Nearby queries find minimum distance to every node and then filter based on minimun duration
            val q = item.replace("nearby", "").split(",").map(_.trim)
            val startNode = q.head
            val duration = q.last.toDouble
            val solver = new Solver[graph.type](graph)
            val node = graph.getNodeByName(startNode).get
            val (distance, paths) = solver.compute(node)
            val nearbyPlaces = distance
              .filter(_._1.name != startNode)
              .filter(item => item._2 <= duration)
              .map(item => (item._1.name, item._2))
              .toSeq.sortWith((i,j) => i._2 < j._2)
              .map(item => item._1 +": "+item._2.toInt)
              .mkString(",")
            println(nearbyPlaces)
          case _ =>
            println("Please enter a valid query")
        }
    }
  }
}




