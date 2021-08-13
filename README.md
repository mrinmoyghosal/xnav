# Run the application
```
sbt run
```
Input commands needs to write on the StdIn.
```json
mghosal@DEMWIZE12-2 xnav % sbt run
[info] Loading global plugins from /Users/mghosal/.sbt/1.0/plugins
[info] Loading project definition from /Users/mghosal/projects/xnav/project
[info] Loading settings for project xnav from build.sbt ...
[info] Set current project to xnav (in build file:/Users/mghosal/projects/xnav/)
[info] Compiling 3 Scala sources to /Users/mghosal/projects/xnav/target/scala-2.13/classes ...
[info] Non-compiled module 'compiler-bridge_2.13' for Scala 2.13.6. Compiling...
[info]   Compilation completed in 9.339s.
[warn] 1 deprecation (since 2.13.3); re-run with -deprecation for details
[warn] one warning found
[info] running xnav.MainApp 
8
A -> B: 240
A -> C: 70
A -> D: 120
C -> B: 60
D -> E: 480
C -> E: 240
B -> E: 210
E -> A: 300
route A -> B
nearby A, 130

```
Output
```json
A -> C -> B: 130
C: 70,D: 120,B: 130
```

# Run Test
```
sbt test
```
# Implementation Details
* The solver is built using the Djikstra algorithm
* Solver function can be configured with a stop function.
* When finding shortest duration from A -> B, it uses a stop function to determine when to stop traversing
* When finding nearby places, it uses a default stop function that calculate shortest path from source node to all nodes and then filter based on the supplied minimum duration.
