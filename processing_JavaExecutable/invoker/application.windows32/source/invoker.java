import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.jsoup.safety.*; 
import org.jsoup.helper.*; 
import org.jsoup.*; 
import org.jsoup.parser.*; 
import org.jsoup.select.*; 
import org.jsoup.nodes.*; 
import guru.ttslib.*; 
import javax.swing.*; 

import org.jsoup.*; 
import org.jsoup.helper.*; 
import org.jsoup.internal.*; 
import org.jsoup.nodes.*; 
import org.jsoup.parser.*; 
import org.jsoup.safety.*; 
import org.jsoup.select.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class invoker extends PApplet {










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
public void setup(){
  
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

public void node_edgeMap(String s){
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

public void parsePoem_nodeMap(String s){
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

public void draw(){
  background(255);
  forceDirectedGraph.draw();
  //controlPanel.draw();
}

public void mouseMoved(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseMovedAt(mouseX, mouseY);
}
public void mousePressed(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMousePressedAt(mouseX, mouseY);
  //else if(controlPanel.isIntersectingWith(mouseX, mouseY))
  //  controlPanel.onMousePressedAt(mouseX, mouseY);
}
public void mouseDragged(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseDraggedTo(mouseX, mouseY);
  //else if(controlPanel.isIntersectingWith(mouseX, mouseY))
  //  controlPanel.onMouseDraggedTo(mouseX, mouseY);
}
public void mouseReleased(){
  if(forceDirectedGraph.isIntersectingWith(mouseX, mouseY))
    forceDirectedGraph.onMouseReleased();
}

public ForceDirectedGraph createFromMaps(){
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

public ForceDirectedGraph createForceDirectedGraphFrom(String dataFilePath){
  ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
  String[] lines = loadStrings(dataFilePath);

  int numberOfNodes = PApplet.parseInt(trim(lines[0]));
  for(int i = 1; i < 1 + numberOfNodes; i++){
    String[] nodeData = splitTokens(trim(lines[i]), ",");
    int id = PApplet.parseInt(trim(nodeData[0]));
    float mass = PApplet.parseFloat(trim(nodeData[1]));
    //float mass = 1;
    //String s = trim(nodeData[2]);
    String s = "rkasndja";
    forceDirectedGraph.add(new Node(id, mass,s));
  }

  int numberOfEdges = PApplet.parseInt(trim(lines[numberOfNodes + 1]));
  for(int i = numberOfNodes + 2; i < numberOfNodes + 2 + numberOfEdges; i++){
    String[] edgeData = splitTokens(trim(lines[i]), ",");
    int id1 = PApplet.parseInt(trim(edgeData[0]));
    int id2 = PApplet.parseInt(trim(edgeData[1]));
    float edgeLength = PApplet.parseFloat(trim(edgeData[2]));
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
  public interface OnValueChangeListener{
    public abstract void onSpringConstantChangedTo(float value);
    public abstract void onCoulombConstantChangedTo(float value);
    public abstract void onDampingCoefficientChangedTo(float value);
    public abstract void onTimeStepChangedTo(float value);
  }

public class ControlPanel extends Viewport{

  private OnValueChangeListener listener;
  //private Slider springConstantSlider;
  //private Slider coulombConstantSlider;
  //private Slider dampingCoefficientSlider;
  private Slider timeStepSlider;

  public ControlPanel(OnValueChangeListener listener, float viewX, float viewY, float viewWidth, float viewHeight){
    super(viewX, viewY, viewWidth, viewHeight);
    this.listener = listener;

    float sliderViewX = viewX + viewWidth * 0.1f;
    float sliderViewWidth = viewWidth * 0.8f;
    float sliderViewHeight = viewHeight / 4.0f;
    float sliderViewY = viewY;
    //this.springConstantSlider = new Slider("Spring Constant", ForceDirectedGraph.SPRING_CONSTANT_DEFAULT, 0.1f, 0.3f);
    //this.springConstantSlider.set(sliderViewX, sliderViewY, sliderViewWidth, sliderViewHeight);
    //sliderViewY += sliderViewHeight;
    //this.coulombConstantSlider = new Slider("Coulomb Constant", ForceDirectedGraph.COULOMB_CONSTANT_DEFAULT, 0.0f, 1500.0f);
    //this.coulombConstantSlider.set(sliderViewX, sliderViewY, sliderViewWidth, sliderViewHeight);
    //sliderViewY += sliderViewHeight;
    //this.dampingCoefficientSlider = new Slider("Damping Coefficient", ForceDirectedGraph.DAMPING_COEFFICIENT_DEFAULT, 0.1f, 0.3f);
    //this.dampingCoefficientSlider.set(sliderViewX, sliderViewY, sliderViewWidth, sliderViewHeight);
    sliderViewY += sliderViewHeight;
    this.timeStepSlider = new Slider("Time Step", ForceDirectedGraph.TIME_STEP_DEFAULT, 0.1f, 2.0f);
    this.timeStepSlider.set(sliderViewX, sliderViewY, sliderViewWidth, sliderViewHeight);
  }

  public void draw(){
    noStroke();
    fill(245);
    rect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    //this.springConstantSlider.draw();
    //this.coulombConstantSlider.draw();
    //this.dampingCoefficientSlider.draw();
    this.timeStepSlider.draw();
  }

  public void onMousePressedAt(int x, int y){
    //if(this.springConstantSlider.isIntersectingWith(x,y)){
    //  this.springConstantSlider.updateValueBy(x);
    //  float value = this.springConstantSlider.getValue();
    //  this.listener.onSpringConstantChangedTo(value);
    //}else if(this.coulombConstantSlider.isIntersectingWith(x,y)){
    //  this.coulombConstantSlider.updateValueBy(x);
    //  float value = this.coulombConstantSlider.getValue();
    //  this.listener.onCoulombConstantChangedTo(value);
    //}else if(this.dampingCoefficientSlider.isIntersectingWith(x,y)){
    //  this.dampingCoefficientSlider.updateValueBy(x);
    //  float value = this.dampingCoefficientSlider.getValue();
    //  this.listener.onDampingCoefficientChangedTo(value);
    //}else 
    if(this.timeStepSlider.isIntersectingWith(x,y)){
      this.timeStepSlider.updateValueBy(x);
      float value = this.timeStepSlider.getValue();
      this.listener.onTimeStepChangedTo(value);
    }
  }
  public void onMouseDraggedTo(int x, int y){
    this.onMousePressedAt(x, y);
  }

}
public class ForceDirectedGraph extends Viewport implements OnValueChangeListener{

  private static final float TOTAL_KINETIC_ENERGY_DEFAULT = MAX_FLOAT;
  public static final float SPRING_CONSTANT_DEFAULT       = 0.1f;
  public static final float COULOMB_CONSTANT_DEFAULT      = 500.0f;
  public static final float DAMPING_COEFFICIENT_DEFAULT   = 0.2f;
  public static final float TIME_STEP_DEFAULT             = 0.01f;

  //private ArrayList<Node> nodes;
  HashMap<Integer,Node> nodes;
  private float totalKineticEnergy;
  private float springConstant;
  private float coulombConstant;
  private float dampingCoefficient;
  private float timeStep;

  private Node lockedNode;
  private Node dummyCenterNode; //for pulling the glaph to center

  public ForceDirectedGraph(){
    super();
    this.nodes = new HashMap();
    this.totalKineticEnergy = TOTAL_KINETIC_ENERGY_DEFAULT;
    this.springConstant = SPRING_CONSTANT_DEFAULT;
    this.coulombConstant = COULOMB_CONSTANT_DEFAULT;
    this.dampingCoefficient = DAMPING_COEFFICIENT_DEFAULT;
    this.timeStep = TIME_STEP_DEFAULT;

    this.lockedNode = null;
    this.dummyCenterNode = new Node(-1, 1.0f,"");
  }

  public void add(Node node){
    this.nodes.put(node.id,node);
  }

  public void addEdge(int id1, int id2, float naturalSpringLength, String wt){
    if(id1==id2){Node node1 = this.getNodeWith(id1); node1.selfRef=true;return;}
    Node node1 = this.getNodeWith(id1);
    Node node2 = this.getNodeWith(id2);
    node1.add(node2, naturalSpringLength,false);
    //node1.adj_weights.add(wt);
    node2.add(node1, naturalSpringLength,true); 
  }
  
  private Node getNodeWith(int id){
    Node node = null;
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target.getID() == id){
        node = target;
        break;
      }
    }
    return node;
  }

  public void initializeNodeLocations(){
    float maxMass = 0.0f;
    for(int i = 0; i < this.nodes.size(); i++){
      float mass = this.nodes.get(i).getMass();
      if(mass > maxMass)
        maxMass = mass;
    }
    float nodeSizeRatio;
    if(this.getWidth() < this.getHeight())
      nodeSizeRatio = this.getWidth() / (maxMass * 5.0f); //ad-hoc
    else
      nodeSizeRatio = this.getHeight() / (maxMass * 5.0f); //ad-hoc
    float offset = nodeSizeRatio * maxMass;
    float minXBound = this.getX() + offset;
    float maxXBound = this.getX() + this.getWidth() - offset;
    float minYBound = this.getY() + offset;
    float maxYBound = this.getY() + this.getHeight() - offset;

    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      float x = random(minXBound, maxXBound);
      float y = random(minYBound, maxYBound);
      float d = node.getMass() * nodeSizeRatio;
      if(d<=25) d = 25;
      node.set(x, y, d);
    }
  }

  public void draw(){
    this.totalKineticEnergy = this.calculateTotalKineticEnergy();

    strokeWeight(1.5f);
    this.drawEdges();
    for(int i = 0; i < this.nodes.size(); i++){
      this.nodes.get(i).draw();
    }
  }

  private void drawEdges(){
    stroke(51, 51, 255);
    for(int i = 0; i < this.nodes.size(); i++){
      Node node1 = this.nodes.get(i);
      if(node1.selfRef){ 
        //print("here?");
        float step = node1.diameter/5;
      arc(node1.getX()+step, node1.getY()+step, node1.diameter, node1.diameter, -PI,PI); }

      for(int j = 0; j < node1.getSizeOfAdjacents(); j++){
        Node node2 = node1.getAdjacentAt(j);
        //line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
        float midx = node1.getX()+node2.getX(); midx = midx/2;
        float midy = node1.getY() + node2.getY(); midy = midy/2;
        noFill();
        //bezier(node1.getX(), node1.getY(),midx,height/2,width/2,midy,node2.getX(), node2.getY());
        drawArrow(node1.getX(), node1.getY(),node1.diameter/2, node2.getX(), node2.getY(),node2.diameter/2);
        //pushMatrix();
        //translate(node1.getX(),node1.getY());
        //rotate(PI/2-radians(atan((node2.getY()-node1.getY())/(node2.getX()-node1.getX()))));
        ////float x = (node2.getX()-node1.getX())/2; float y = (-node1.getY()+node2.getY())/2;
        //line(10,0,5,-3);
        //line(10,0,5,3);
        //popMatrix();
      }
    }
  }

  private float calculateTotalKineticEnergy(){ //ToDo:check the calculation in terms of Math...
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target == this.lockedNode)
        continue;

      float forceX = 0.0f;
      float forceY = 0.0f;
      for(int j = 0; j < this.nodes.size(); j++){ //Coulomb's law
        Node node = this.nodes.get(j);
        if(node != target){
          float dx = target.getX() - node.getX();
          float dy = target.getY() - node.getY();
          float distance = sqrt(dx * dx + dy * dy);
          float xUnit = dx / distance;
          float yUnit = dy / distance;

          float coulombForceX = this.coulombConstant * (target.getMass() * node.getMass()) / pow(distance, 2.0f) * xUnit;
          float coulombForceY = this.coulombConstant * (target.getMass() * node.getMass()) / pow(distance, 2.0f) * yUnit;

          forceX += coulombForceX;
          forceY += coulombForceY;
        }
      }

      for(int j = 0; j < target.getSizeOfAdjacents(); j++){ //Hooke's law
        Node node = target.getAdjacentAt(j);
        float springLength = target.getNaturalSpringLengthAt(j);
        float dx = target.getX() - node.getX();
        float dy = target.getY() - node.getY();
        float distance = sqrt(dx * dx + dy * dy);
        float xUnit = dx / distance;
        float yUnit = dy / distance;

        float d = distance - springLength;

        float springForceX = -1 * this.springConstant * d * xUnit;
        float springForceY = -1 * this.springConstant * d * yUnit;

        forceX += springForceX;
        forceY += springForceY;
      }
      
    //for(int j = 0; j < target.adjacents_incoming.size(); j++){ //Hooke's law
    //    Node node = target.adjacents_incoming.get(j);
    //    float springLength = target.getNaturalSpringLengthAt(j);
    //    float dx = target.getX() - node.getX();
    //    float dy = target.getY() - node.getY();
    //    float distance = sqrt(dx * dx + dy * dy);
    //    float xUnit = dx / distance;
    //    float yUnit = dy / distance;

    //    float d = distance - springLength;

    //    float springForceX = -1 * this.springConstant * d * xUnit;
    //    float springForceY = -1 * this.springConstant * d * yUnit;

    //    forceX += springForceX;
    //    forceY += springForceY;
    //  }

      target.setForceToApply(forceX, forceY);
    }

    float totalKineticEnergy = 0.0f;
    for(int i = 0; i < this.nodes.size(); i++){
      Node target = this.nodes.get(i);
      if(target == this.lockedNode)
        continue;

      float forceX = target.getForceX();
      float forceY = target.getForceY();

      float accelerationX = forceX / target.getMass();
      float accelerationY = forceY / target.getMass();

      float velocityX = (target.getVelocityX() + this.timeStep * accelerationX) * this.dampingCoefficient;
      float velocityY = (target.getVelocityY() + this.timeStep * accelerationY) * this.dampingCoefficient;

      float x = target.getX() + this.timeStep * target.getVelocityX() + accelerationX * pow(this.timeStep, 2.0f) / 2.0f;
      float y = target.getY() + this.timeStep * target.getVelocityY() + accelerationY * pow(this.timeStep, 2.0f) / 2.0f;

      float radius = target.getDiameter() / 2.0f; //for boundary check
      if(x < this.getX() + radius)
        x = this.getX() + radius;
      else if(x > this.getX() + this.getWidth() - radius)
        x =  this.getX() + this.getWidth() - radius;
      if(y < this.getY() + radius)
        y = this.getY() + radius;
      else if(y > this.getY() + this.getHeight() - radius)
        y =  this.getX() + this.getHeight() - radius;

      target.set(x, y);
      target.setVelocities(velocityX, velocityY);
      target.setForceToApply(0.0f, 0.0f);

      totalKineticEnergy += target.getMass() * sqrt(velocityX * velocityX + velocityY * velocityY) / 2.0f;
    }
    return totalKineticEnergy;
  }

  public void onMouseMovedAt(int x, int y){
    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      if(node.isIntersectingWith(x, y))
        node.highlight();
      else
        node.dehighlight();
    }
  }
  public void onMousePressedAt(int x, int y){
    for(int i = 0; i < this.nodes.size(); i++){
      Node node = this.nodes.get(i);
      if(node.isIntersectingWith(x, y)){
        this.lockedNode = node;
        this.lockedNode.setVelocities(0.0f, 0.0f);
        break;
      }
    }
  }
  public void onMouseDraggedTo(int x, int y){
    if(this.lockedNode != null){
      float radius = this.lockedNode.getDiameter() / 2.0f; //for boundary check
      if(x < this.getX() + radius)
        x = (int)(this.getX() + radius);
      else if(x > this.getX() + this.getWidth() - radius)
        x =  (int)(this.getX() + this.getWidth() - radius);
      if(y < this.getY() + radius)
        y = (int)(this.getY() + radius);
      else if(y > this.getY() + this.getHeight() - radius)
        y =  (int)(this.getX() + this.getHeight() - radius);

      this.lockedNode.set(x, y);
      this.lockedNode.setVelocities(0.0f, 0.0f);
    }
  }
  public void onMouseReleased(){
    this.lockedNode = null;
  }

  //@Override
  public void onSpringConstantChangedTo(float value){
    this.springConstant = value;
  }

  //@Override
  public void onCoulombConstantChangedTo(float value){
    this.coulombConstant = value;
  }

  //@Override
  public void onDampingCoefficientChangedTo(float value){
    this.dampingCoefficient = value;
  }
  //@Override
  public void onTimeStepChangedTo(float value){
    this.timeStep = value;
  }

  public void dumpInformation(){
    println("--------------------");
    for(int i = 0; i < this.nodes.size(); i++)
      println(this.nodes.get(i).toString());
    println("--------------------");
  }
  
  public void drawArrow(float cx0, float cy0, float rad0, float cx1, float cy1, float rad1) {
  // These will be the points on the circles circumference
  float px0, py0, px1, py1;
  float th = 2; int ac = 0;
  // the angle of the line joining centre of circle c0 to c1
  float angle = atan2(cy1-cy0, cx1-cx0);
  px0 = cx0 + rad0 * cos(angle);
  py0 = cy0 + rad0 * sin(angle);
  px1 = cx1 + rad1 * cos(angle + PI);
  py1 = cy1 + rad1 * sin(angle + PI);
  // Calculate the arrow length and head size
  float arrowLength = sqrt((px1-px0)*(px1-px0) +(py1-py0)*(py1-py0));
  float arrowSize = 2.5f * th;
  // Setup arrow colours and thickness
  strokeWeight(th);
  stroke(ac);
  fill(ac);
  // Set the drawing matrix as if the arrow starts
  // at the origin and is along the x-axis
  pushMatrix();
  translate(px0, py0);
  rotate(angle);
  // Draw the arrow shafte
  line(0, 0, arrowLength, 0);
  //  draw the arrowhead
  beginShape(TRIANGLES);
  vertex(arrowLength, 0); // point
  vertex(arrowLength - arrowSize, -arrowSize);
  vertex(arrowLength - arrowSize, arrowSize);
  endShape();
  popMatrix();
}


}
public class Node{

  private int id;
  private float mass;
  private ArrayList<Node> adjacents;
  ArrayList<String> adj_weights;
  private ArrayList<Node> adjacents_incoming;
  private ArrayList<Float> naturalSpringLengths;
  private float x;
  private float y;
  private float diameter;
  private float velocityX;
  private float velocityY;
  private float forceX;
  private float forceY;
  private boolean isHighlighted;
  boolean selfRef = false;
  String name;
  TTS tts = new TTS();

  public Node(int id, float mass, String s){
    this.id = id;
    this.mass = mass;
    this.name = s;
    
    this.adjacents = new ArrayList<Node>();
    this.adj_weights = new ArrayList<String>();
    this.adjacents_incoming = new ArrayList<Node>();
    this.naturalSpringLengths = new ArrayList<Float>();

    this.set(-1.0f, -1.0f, -1.0f); //ad-hoc
    this.setVelocities(0.0f, 0.0f);
    this.setForceToApply(0.0f, 0.0f);
    this.isHighlighted = false;
  }

  public void add(Node adjacent, float naturalSpringLength, boolean isIncome){
    this.naturalSpringLengths.add(naturalSpringLength); //better to capture these as like key-value pairs...

    if(isIncome){this.adjacents_incoming.add(adjacent); return;}
    this.adjacents.add(adjacent);                       //the order of elements in the two ArrayLists must be the same.
  }
  public void set(float x, float y){
    this.x = x;
    this.y = y;
  }
  public void set(float x, float y, float diameter){
    this.set(x, y);
    this.diameter = diameter;
  }
  public void setVelocities(float velocityX, float velocityY){
    this.velocityX = velocityX;
    this.velocityY = velocityY;
  }
  public void setForceToApply(float forceX, float forceY){
    this.forceX = forceX;
    this.forceY = forceY;
  }

  public int getID(){
    return this.id;
  }
  public float getMass(){
    return this.mass;
  }
  public float getX(){
    return this.x;
  }
  public float getY(){
    return this.y;
  }
  public float getDiameter(){
    return this.diameter;
  }
  public float getVelocityX(){
    return this.velocityX;
  }
  public float getVelocityY(){
    return this.velocityY;
  }
  public float getForceX(){
    return this.forceX;
  }
  public float getForceY(){
    return this.forceY;
  }
  public int getSizeOfAdjacents(){
    return this.adjacents.size();
  }
  public Node getAdjacentAt(int index){
    return this.adjacents.get(index);
  }
  public float getNaturalSpringLengthAt(int index){
    return this.naturalSpringLengths.get(index);
  }

  public void draw(){

    if(this.isHighlighted){
      stroke(255, 178, 102);
      fill(255, 178, 102,50);
      if(keyPressed && key=='v'){
      //tts.setPitch(427 + noise(velocityX)*2-1);
      tts.speak(name);}
      
      //if(mousePressed){tts.speak(name);}
    }
    else{
      stroke(51, 51, 255);
      fill(102, 178, 255,50);
    }
    ellipse(this.x, this.y, this.diameter, this.diameter);

    textAlign(CENTER,CENTER);
    fill(0);    
    textSize(20);
    text(name,this.x,this.y);

    //if(this.isHighlighted){ 
      //fill(0);
      //textAlign(CENTER, BOTTOM);
      //text("id: " + this.id, this.x, this.y);
      //textAlign(CENTER, TOP);
      //text("frequency: " + this.mass, this.x, this.y);
    //}
  }

  public void highlight(){
    this.isHighlighted = true;
  }
  public void dehighlight(){
    this.isHighlighted = false;
  }
  public boolean isIntersectingWith(int x, int y){
    float r = this.diameter / 2.0f;
    if(this.x - r <= x && x <= this.x + r && this.y - r <= y && y <= this.y + r)
      return true;
    else
      return false;
  }

  //@Override
  public String toString(){
    String adjacentIDsAndNaturalLengths = "[";
    for(int i = 0; i < this.adjacents.size(); i++)
      adjacentIDsAndNaturalLengths += this.adjacents.get(i).getID() + "(" + this.naturalSpringLengths.get(i) + "),";
    adjacentIDsAndNaturalLengths += "]";
    return "ID:" + this.id +
           ",MASS:" + this.mass +
           ",ADJACENTS(NATURAL_LEGTH):" + adjacentIDsAndNaturalLengths +
           ",X:" + this.x +
           ",Y:" + this.y +
           ",DIAMETER:" + this.diameter +
           ",HIGHLIGHTED:" + this.isHighlighted;
  }

}
class Slider extends Viewport{

  private String title;
  private Knob knob;

  public Slider(String title, float initialValue, float min, float max){
    super();
    this.title = title;
    this.knob = new Knob(initialValue, min, max);
  }

  //@Override
  public void set(float viewX, float viewY, float viewWidth, float viewHeight){
    super.set(viewX, viewY, viewWidth, viewHeight);
    if(this.knob != null){
      float textHeight = textAscent() + textDescent();
      this.knob.set(viewX, viewY + textHeight, viewWidth, viewHeight / 3.0f);
    }
  }

  public void draw(){
    noStroke();
    fill(0);
    textAlign(LEFT, TOP);
    text(this.title, this.getX(), this.getY());

    this.knob.draw();
  }

  public float getValue(){
    return this.knob.getValue();
  }
  public void updateValueBy(int x){
    this.knob.updateValueBy(x);
  }

  //@Override
  public boolean isIntersectingWith(int x, int y){
    return this.knob.isIntersectingWith(x, y);
  }


  private class Knob extends Viewport{

    private float min;
    private float max;
    private float value;
    private float knobX;

    public Knob(float initialValue, float min, float max){
      super();
      this.min = min;
      this.max = max;
      this.setValue(initialValue);
    }

    //@Override
    public void set(float viewX, float viewY, float viewWidth, float viewHeight){
      super.set(viewX, viewY, viewWidth, viewHeight);
      this.setValue(this.value); //to update the tick
    }

    private void setValue(float value){
      this.value = value;
      this.knobX = (this.value - this.min) / (this.max - this.min) * this.getWidth() + this.getX();
    }
    public float getValue(){
      return this.value;
    }
    public void updateValueBy(int x){
      float value = ((float)x - this.getX()) / this.getWidth() * (this.max - this.min) + this.min;
      this.setValue(value);
    }

    public void draw(){
      fill(225, 225, 225);
      rect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

      fill(255);
      textAlign(LEFT, CENTER);
      text(this.min, this.getX(), this.getCenterY());
      textAlign(RIGHT, CENTER);
      text(this.max, this.getX() + this.getWidth(), this.getCenterY());

      stroke(0);
      line(this.knobX, this.getY(), this.knobX, this.getY() + this.getHeight());
    }

  }

}
class Viewport{

  protected float viewX;
  protected float viewY;
  protected float viewWidth;
  protected float viewHeight;
  protected float viewCenterX;
  protected float viewCenterY;
  protected boolean isHighlighted;

  public Viewport(){
    this(-1.0f, -1.0f, -1.0f, -1.0f); //ad-hoc
  }
  public Viewport(float viewX, float viewY, float viewWidth, float viewHeight){
    this.set(viewX, viewY, viewWidth, viewHeight);
    this.dehighlight();
  }

  public void set(float viewX, float viewY, float viewWidth, float viewHeight){
    this.viewX = viewX;
    this.viewY = viewY;
    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
    this.updateCenter();
  }
  public void setX(float viewX){
    this.viewX = viewX;
    this.updateCenter();
  }
  public void setY(float viewY){
    this.viewY = viewY;
    this.updateCenter();
  }
  public void setWidth(float viewWidth){
    this.viewWidth = viewWidth;
    this.updateCenter();
  }
  public void setHeight(float viewHeight){
    this.viewHeight = viewHeight;
    this.updateCenter();
  }
  private void updateCenter(){
    this.viewCenterX = this.viewX + this.viewWidth / 2.0f;
    this.viewCenterY = this.viewY + this.viewHeight / 2.0f;
  }
  public void highlight(){
    this.isHighlighted = true;
  }
  public void dehighlight(){
    this.isHighlighted = false;
  }

  public float getX(){
    return this.viewX;
  }
  public float getY(){
    return this.viewY;
  }
  public float getWidth(){
    return this.viewWidth;
  }
  public float getHeight(){
    return this.viewHeight;
  }
  public float getCenterX(){
    return this.viewCenterX;
  }
  public float getCenterY(){
    return this.viewCenterY;
  }
  public boolean isHighlighted(){
    return this.isHighlighted;
  }
  public boolean isIntersectingWith(int x, int y){
    if(this.viewX <= x && x <= this.viewX + this.viewWidth){
      if(this.viewY <= y && y <= this.viewY + this.viewHeight)
        return true;
      else
        return false;
    }else{
      return false;
    }
  }

}
  public void settings() {  size(1000, 1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "invoker" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
