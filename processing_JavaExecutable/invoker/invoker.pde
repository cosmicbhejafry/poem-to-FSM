import org.jsoup.safety.*;
import org.jsoup.helper.*;
import org.jsoup.*;
import org.jsoup.parser.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;
import guru.ttslib.*;
import javax.swing.*;

//https://www.local-guru.net/blog/pages/ttslib
//http://www.frontiernerds.com/text-to-speech-in-processing
//https://github.com/zshiba/visualization/blob/master/src/force_directed_graph/ForceDirectedGraph.pde

final String DATA_FILE_PATH = "./data.csv";
/*
 *  data.csv frmat:
 *  By assuming the line index is base 0.
 *  a. Line#(0): NumberOfNodes
 *  b. Line#(1)~#(NumberOfNodes): NodeIndex,NodeMass
 *  c. Line#(NumberOfNodes + 1): NumberOfEdges
 *  d. Line#(NumberOfNodes + 1)~#(NumberOfNodes + 1 + NumberOfEdges): Node1Index,Node2Index,NaturalSpringLength
 */

ForceDirectedGraph forceDirectedGraph;
//ControlPanel controlPanel;
HashMap<String,nodeMapHelp> nodeMap = new HashMap();
HashMap<Integer,ArrayList<edgeMapHelp>> edgeMap = new HashMap();
//HashMap<Integer,
boolean didWork = true;
void setup(){
  size(1000, 1000);
  int canvasWidth = width;
  int canvasHeight = height;
 String entry = JOptionPane.showInputDialog("Enter a PoetryFoundation poem URL \n MUST BE TEXT NOT EMBEDDED IMAGE/PDF: ");  
  
  try {
    String url = "https://www.poetryfoundation.org/poetrymagazine/poems/150759/haiku-5d549a16309f8";
    //url = "https://www.poetryfoundation.org/poetrymagazine/poems/156321/chocolate-61095e8dc16af";
    url = "https://www.poetryfoundation.org/poems/151653/you-fit-into-me";
    if(entry!=null && entry.length()>0){url=entry;}
    print("trying..");
    Document doc = Jsoup.connect(url).get();
    doc.select("br").after("\n");
    doc.select("p").before("\n");
    doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
    doc.select("br").append("\\n");
    doc.select("p").prepend("\\n\\n");

    Elements els = doc.getElementsByClass("o-poem");
    for(Element el : els){
      //print(el.text());
      //print(el.wholeText());      
      //el.html().replaceAll("\\\\n", "\n");
      String s = el.text();
      parsePoem_nodeMap(s);
      node_edgeMap(s);
    //  for(int keyv : edgeMap.keySet()){
    //    print(" " + keyv + " ");
    //    print("wt: " + edgeMap.get(keyv).wt + " ");
    //    print("nxt: " + edgeMap.get(keyv).nextNode + " ");
    //    print(" \n");
    //  }
    //for(String keyv : nodeMap.keySet()){
    //    print(" " + keyv + " " + nodeMap.get(keyv).nodeID);
    //    //print("wt: " + edgeMap.get(keyv).wt + " ");
    //    //print("nxt: " + nodeMap.get(keyv).nextNode + " ");
    //    print(" \n");
    //  }
      //s = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
      //saveStrings("nouns.txt", new String[]{s});
      forceDirectedGraph = createFromMaps();
      forceDirectedGraph.set(0.0f, 0.0f, (float)canvasWidth * 1.0f, (float)canvasHeight);
    forceDirectedGraph.initializeNodeLocations();
    }
  }
  catch (IOException e) {
    print("failed?");
    didWork=false;
    println(e.getMessage());
    exit();
    //forceDirectedGraph = createForceDirectedGraphFrom(DATA_FILE_PATH);
  }

  //controlPanel = new ControlPanel(forceDirectedGraph, forceDirectedGraph.getX() + forceDirectedGraph.getWidth(), 0.0f, (float)canvasWidth * 0.2f, (float)canvasHeight);
}

void node_edgeMap(String s){
  int id = 0;
  String[] lines = split(s,"\\n");
  int i = 0; int i_inElse = 0;
  //String nextNode = "";
  //String currNode = "";
  int nextNodeID=0;int currNodeID=0;
  //String whichEdgeWt = "";
  int whichEdge = 0; //1 for linebreak, 2 for para break
  while(i<=lines.length-3){
    String line = lines[i];

    line = line.trim();
    line = line.replaceAll("-"," ");
    line = line.replaceAll("[^a-zA-Z ]","").toLowerCase();

    if(line.length()==0){
      print("parabreak" + i + " ");
      whichEdge=2;
    } 
    else{

        String[] textLine = split(line," ");

        if(i_inElse>0){
          //print("here");
          nextNodeID=nodeMap.get(textLine[0]).nodeID;
          edgeMapHelp temp = new edgeMapHelp(nextNodeID);
          if(whichEdge==2){ 
             temp.wt="paragraph";
          } else{
          temp.wt="line";
          }
          if(edgeMap.containsKey(currNodeID)){
            ArrayList<edgeMapHelp> tempL = edgeMap.get(currNodeID);
            tempL.add(temp);
            edgeMap.put(currNodeID,tempL);
          } else{
            ArrayList<edgeMapHelp> tempL = new ArrayList();
            tempL.add(temp);
            edgeMap.put(currNodeID,tempL);
          }
  
         //print("between id " + currNodeID + " and " + nextNodeID);
         currNodeID=nextNodeID;
        }

        for(int k = 0; k < textLine.length-1; k++){
          currNodeID = nodeMap.get(textLine[k]).nodeID;
          nextNodeID = nodeMap.get(textLine[k+1]).nodeID;
         edgeMapHelp temp = new edgeMapHelp(nextNodeID);
         //edgeMap.put(currNodeID,temp);
                   if(edgeMap.containsKey(currNodeID)){
            ArrayList<edgeMapHelp> tempL = edgeMap.get(currNodeID);
            tempL.add(temp);
            edgeMap.put(currNodeID,tempL);
          } else{
            ArrayList<edgeMapHelp> tempL = new ArrayList();
            tempL.add(temp);
            edgeMap.put(currNodeID,tempL);
          }

        }
        currNodeID=(int) nextNodeID;
        i_inElse++;
      }
      i++;
    }  
}

void parsePoem_nodeMap(String s){
  int id = 0;
  String[] lines = split(s,"\\n");
  int i = 0;
  String currNode = "";

  while(i<=lines.length-3){
    String line = lines[i];
    i++;
    line = line.trim();
    line = line.replaceAll("-"," ");
    line = line.replaceAll("[^a-zA-Z ]","").toLowerCase();
    if(line.length()==0){print("parabreak");
    } 
    else{
      //println(line);
      String[] textLine = split(line," ");
      for(int k = 0; k < textLine.length; k++){
        currNode = textLine[k];
          if(nodeMap.containsKey(currNode)){
            int frq = nodeMap.get(currNode).freq;
            nodeMapHelp temp = new nodeMapHelp(frq+1,nodeMap.get(currNode).nodeID);
            nodeMap.put(currNode,temp);
          } else{
            nodeMapHelp temp = new nodeMapHelp(1,id);      
            nodeMap.put(currNode,temp);
            id++;
          }
      }
    }
  }
  
}

void draw(){
  background(255);
  forceDirectedGraph.draw();
  //controlPanel.draw();
}

void mouseMoved(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseMovedAt(mouseX, mouseY);
}
void mousePressed(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMousePressedAt(mouseX, mouseY);
  //else if(controlPanel.isIntersectingWith(mouseX, mouseY))
  //  controlPanel.onMousePressedAt(mouseX, mouseY);
}
void mouseDragged(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseDraggedTo(mouseX, mouseY);
  //else if(controlPanel.isIntersectingWith(mouseX, mouseY))
  //  controlPanel.onMouseDraggedTo(mouseX, mouseY);
}
void mouseReleased(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseReleased();
}

ForceDirectedGraph createFromMaps(){
  ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();

  for(String keyv : nodeMap.keySet()){
    int id = nodeMap.get(keyv).nodeID;
    float mass = nodeMap.get(keyv).freq;
    if(mass>7){mass=5;}
    forceDirectedGraph.add(new Node(id, mass,keyv));
  }

  for(int id1 : edgeMap.keySet()){
    ArrayList<edgeMapHelp> edgz = edgeMap.get(id1);
    for(edgeMapHelp helpEdge : edgz){
      int id2 = helpEdge.nextNode;
      String wtstr = helpEdge.wt;
      float edgeLength = 25;
      //print(forceDirectedGraph.nodes.get(id1).name);
      //print(" to "); 
      //print(forceDirectedGraph.nodes.get(id2).name);
      //print("\n");
      forceDirectedGraph.addEdge(id1, id2, edgeLength,wtstr);
    }
  }
  return forceDirectedGraph;
}

ForceDirectedGraph createForceDirectedGraphFrom(String dataFilePath){
  ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
  String[] lines = loadStrings(dataFilePath);

  int numberOfNodes = int(trim(lines[0]));
  for(int i = 1; i < 1 + numberOfNodes; i++){
    String[] nodeData = splitTokens(trim(lines[i]), ",");
    int id = int(trim(nodeData[0]));
    float mass = float(trim(nodeData[1]));
    //float mass = 1;
    //String s = trim(nodeData[2]);
    String s = "rkasndja";
    forceDirectedGraph.add(new Node(id, mass,s));
  }

  int numberOfEdges = int(trim(lines[numberOfNodes + 1]));
  for(int i = numberOfNodes + 2; i < numberOfNodes + 2 + numberOfEdges; i++){
    String[] edgeData = splitTokens(trim(lines[i]), ",");
    int id1 = int(trim(edgeData[0]));
    int id2 = int(trim(edgeData[1]));
    float edgeLength = float(trim(edgeData[2]));
    //print(id1);print(" "); print(id2); print(" ");
    forceDirectedGraph.addEdge(id1, id2, edgeLength,"");
    //forceDirectedGraph.addEdge(id1, id1, edgeLength,"");
  }
  return forceDirectedGraph;
}

class nodeMapHelp{
  int freq; 
  int nodeID;
  //String wt;
  nodeMapHelp(int f, int i){
    freq = f; 
    nodeID = i;
  }
}

class edgeMapHelp{
  int nextNode;
  String wt;
  edgeMapHelp(int nxt){
    nextNode = nxt;
    wt="";
  }
}

//https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
