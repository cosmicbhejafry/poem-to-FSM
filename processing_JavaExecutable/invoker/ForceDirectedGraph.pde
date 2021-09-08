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
  float arrowSize = 2.5 * th;
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
