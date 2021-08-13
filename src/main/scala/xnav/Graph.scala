package xnav

abstract class AGraph {
  type Node <: INode
  type Edge <: IEdge

  abstract class IEdge {
    def a: Node
    def b: Node
  }

  abstract class INode {
    val name: String
    def connectWith(node: Node): Edge
  }

  def edges: List[Edge]
  def nodes: List[Node]
  def addNode(name: String): Node
  def getNodeByName(name: String): Option[Node]

}

class WeightedGraph(defaultWeight: Double) extends AGraph {
  type Edge = EdgeImpl with Weight
  type Node = NodeImpl

  class EdgeImpl(one: Node, other: Node) extends IEdge {
    def a = one
    def b = other
  }

  class NodeImpl(_name: String) extends INode {
    this: Node =>
    def connectWith(node: Node): Edge = {
      val edge = newEdge(this, node)
      edges = edge :: edges
      edge
    }
    override def toString:String = (nodes.length - nodes.indexOf(this == _)).toString()
    override val name: String = _name
  }

  trait Weight {
    var weight = defaultWeight
    def getWeight = weight
    def setWeight(weight: Double): Unit = {
      this.weight = weight
    }
  }
  protected def newEdge(one: Node, other: Node): Edge with Weight = new EdgeImpl(one, other) with Weight

  var nodes: List[Node] = Nil
  var edges: List[Edge] = Nil

  override def addNode(name: String): Node = {
    val mayBeNode = nodes.find(_.name == name)
    mayBeNode match {
      case Some(n) =>
        n
      case _ =>
        val node = new NodeImpl(name)
        nodes = node :: nodes
        node
    }
  }
  override def getNodeByName(name: String): Option[Node] = nodes.find(_.name == name)
}
