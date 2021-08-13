package xnav

import scala.collection.mutable._

class Solver[G <: WeightedGraph](graph: G) {
  type Node = G#Node
  type Edge = G#Edge

  type StopCondition = (Set[Node], Map[Node, Double], Map[Node, Node]) => Boolean

  val defaultStopCondition: StopCondition = (_, _, _) => true
  var stopCondition = defaultStopCondition

  def compute(start: Node):
    (Map[Node, Double], Map[Node, Node]) = {
      var queue: Set[Node] = new HashSet()
      var settled: Set[Node] = new HashSet()
      val distance: Map[Node, Double] = new HashMap()
      val path: Map[Node, Node] = new HashMap()
      queue += start
      distance(start) = 0
      while(!queue.isEmpty && stopCondition(settled, distance, path)) {
        val u = extractMinimumDuration(queue, distance)
        settled += u
        relaxNeighbors(u, queue, settled, distance, path)
      }
      (distance, path)
  }

  protected def extractMinimumDuration[T](Q: Set[T], D: Map[T, Double]): T = {
    var u = Q.head
    Q.foreach((node) =>  if(D(u) > D(node)) u = node)
    Q -= u
    u
  }

  protected def relaxNeighbors(u: Node, Q: Set[Node], S: Set[Node],
                               D: Map[Node, Double], P: Map[Node, Node]): Unit = {
    for(edge <- graph.edges.filter(_.a == u) ) {
      var v = edge.b
      if(!S.contains(v)) {
        if(!D.contains(v) || D(v) > D(u) + edge.getWeight) {
          D(v) = D(u) + edge.getWeight
          P(v) = u
          Q += v
        }
      }
    }
  }
}