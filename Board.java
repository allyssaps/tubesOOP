package JAVAEE;

import java.awt.*;
import javax.imageio.*;
import javax.swing.JPanel;
import java.lang.Math;
import java.util.*;
import java.io.*;
import javax.swing.*;


/* Both Player and Dog inherit Mover.  Has generic functions relevant to both*/
class Mover
{
  /* Framecount is used to count animation frames*/
  int frameCount=0;

  /* State contains the game map */
  boolean[][] state;

  /* gridSize is the size of one square in the game.
     max is the height/width of the game.
     increment is the speed at which the object moves,
     1 increment per move() call */
  int gridSize;
  int max;
  int increment;

  /* Generic constructor */
  public Mover()
  {
    gridSize=20;
    increment = 4;
    max = 400;
    state = new boolean[20][20];
    for(int i =0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        state[i][j] = false;
      }
    }
  }

  /* Updates the state information */
  public void updateState(boolean[][] state)
  {
    for(int i =0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        this.state[i][j] = state[i][j];
      }
    }
  }

  /* Determines if a set of coordinates is a valid destination.*/
  public boolean isValidDest(int x, int y)
  {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location */
    if ((((x)%20==0) || ((y)%20)==0) && 20<=x && x<400 && 20<= y && y<400 && state[x/20-1][y/20-1] )
    {
      return true;
    }
    return false;
  } 
}

/* This is the farmer object */
class Player extends Mover
{
  /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/ 
  char direction;
  char currDirection;
  char desiredDirection;

  /* Keeps track of pellets eaten to determine end of game */
  int pelletsEaten;

  /* Last location */
  int lastX;
  int lastY;
 
  /* Current location */
  int x;
  int y;
 
  /* Which pellet the farmer is on top of */
  int pelletX;
  int pelletY;

  /* teleport is true when travelling through the teleport tunnels*/
  boolean teleport;
  
  /* Stopped is set when the farmer is not moving or has been killed */
  boolean stopped = false;

  /* Constructor places farmer in initial location and orientation */
  public Player(int x, int y)
  {

    teleport=false;
    pelletsEaten=0;
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    this.lastX=x;
    this.lastY=y;
    this.x = x;
    this.y = y;
    currDirection='L';
    desiredDirection='L';
  }


  /* This function is used for demoMode.  It is copied from the Dog class.  See that for comments */
  public char newDirection()
  { 
     int random;
     char backwards='U';
     int newX=x,newY=y;
     int lookX=x,lookY=y;
     Set<Character> set = new HashSet<Character>();
    switch(direction)
    {
      case 'L':
         backwards='R';
         break;     
      case 'R':
         backwards='L';
         break;     
      case 'U':
         backwards='D';
         break;     
      case 'D':
         backwards='U';
         break;     
    }
     char newDirection = backwards;
     while (newDirection == backwards || !isValidDest(lookX,lookY))
     {
       if (set.size()==3)
       {
         newDirection=backwards;
         break;
       }
       newX=x;
       newY=y;
       lookX=x;
       lookY=y;
       random = (int)(Math.random()*4) + 1;
       if (random == 1)
       {
         newDirection = 'L';
         newX-=increment; 
         lookX-= increment;
       }
       else if (random == 2)
       {
         newDirection = 'R';
         newX+=increment; 
         lookX+= gridSize;
       }
       else if (random == 3)
       {
         newDirection = 'U';
         newY-=increment; 
         lookY-=increment;
       }
       else if (random == 4)
       {
         newDirection = 'D';
         newY+=increment; 
         lookY+=gridSize;
       }
       if (newDirection != backwards)
       {
         set.add(new Character(newDirection));
       }
     } 
     return newDirection;
  }

  /* This function is used for demoMode.  It is copied from the Dog class.  See that for comments */
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }

  /* This function is used for demoMode.  It is copied from the Dog class.  See that for comments */
  public void demoMove()
  {
    lastX=x;
    lastY=y;
    if (isChoiceDest())
    {
      direction = newDirection();
    }
    switch(direction)
    {
      case 'L':
         if ( isValidDest(x-increment,y))
         {
           x -= increment;
         }
         else if (y == 9*gridSize && x < 2 * gridSize)
         {
           x = max - gridSize*1;
           teleport = true; 
         }
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
         {
           x+= increment;
         }
         else if (y == 9*gridSize && x > max - gridSize*2)
         {
           x = 1*gridSize;
           teleport=true;
         }
         break;     
      case 'U':
         if ( isValidDest(x,y-increment))
           y-= increment;
         break;     
      case 'D':
         if ( isValidDest(x,y+gridSize))
           y+= increment;
         break;     
    }
    currDirection = direction;
    frameCount ++;
  }

  /* The move function moves the farmer for one frame in non demo mode */
  public void move()
  {
    int gridSize=20;
    lastX=x;
    lastY=y;
     
    /* Try to turn in the direction input by the user */
    /*Can only turn if we're in center of a grid*/
    if (x %20==0 && y%20==0 ||
       /* Or if we're reversing*/
       (desiredDirection=='L' && currDirection=='R')  ||
       (desiredDirection=='R' && currDirection=='L')  ||
       (desiredDirection=='U' && currDirection=='D')  ||
       (desiredDirection=='D' && currDirection=='U')
       )
    {
      switch(desiredDirection)
      {
        case 'L':
           if ( isValidDest(x-increment,y))
             x -= increment;
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= increment;
           break;     
        case 'U':
           if ( isValidDest(x,y-increment))
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= increment;
           break;     
      }
    }
    /* If we haven't moved, then move in the direction the farmer was headed anyway */
    if (lastX==x && lastY==y)
    {
      switch(currDirection)
      {
        case 'L':
           if ( isValidDest(x-increment,y))
             x -= increment;
           else if (y == 9*gridSize && x < 2 * gridSize)
           {
             x = max - gridSize*1;
             teleport = true; 
           }
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= increment;
           else if (y == 9*gridSize && x > max - gridSize*2)
           {
             x = 1*gridSize;
             teleport=true;
           }
           break;     
        case 'U':
           if ( isValidDest(x,y-increment))
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= increment;
           break;     
      }
    }

    /* If we did change direction, update currDirection to reflect that */
    else
    {
      currDirection=desiredDirection;
    }
   
    /* If we didn't move at all, set the stopped flag */    
    if (lastX == x && lastY==y)
      stopped=true;
  
    /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
    else
    {
      stopped=false;
      frameCount ++;
    }
  }

  /* Update what pellet the farmer is on top of */
  public void updatePellet()
  {
    if (x%gridSize ==0 && y%gridSize == 0)
    {
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    }
  } 
}

/* Dog class controls the dog. */
class Dog extends Mover
{ 
  /* Direction dog is heading */
  char direction;

  /* Last dog location*/
  int lastX;
  int lastY;

  /* Current dog location */
  int x;
  int y;

  /* The pellet the dog is on top of */
  int pelletX,pelletY;

  /* The pellet the dog was last on top of */
  int lastPelletX,lastPelletY;

  /*Constructor places dog and updates states*/
  public Dog(int x, int y)
  {
    direction='L';
    pelletX=x/gridSize-1;
    pelletY=x/gridSize-1;
    lastPelletX=pelletX;
    lastPelletY=pelletY;
    this.lastX = x;
    this.lastY = y;
    this.x = x;
    this.y = y;
  }

  /* update pellet status */
  public void updatePellet()
  {
    int tempX,tempY;
    tempX = x/gridSize-1;
    tempY = y/gridSize-1;
    if (tempX != pelletX || tempY != pelletY)
    {
      lastPelletX = pelletX;
      lastPelletY = pelletY;
      pelletX=tempX;
      pelletY = tempY;
    }
     
  } 
 
  /* Determines if the location is one where the dog has to make a decision*/ 
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }

  /* Chooses a new direction randomly for the dog to move */
  public char newDirection()
  { 
    int random;
    char backwards='U';
    int newX=x,newY=y;
    int lookX=x,lookY=y;
    Set<Character> set = new HashSet<Character>();
    switch(direction)
    {
      case 'L':
         backwards='R';
         break;     
      case 'R':
         backwards='L';
         break;     
      case 'U':
         backwards='D';
         break;     
      case 'D':
         backwards='U';
         break;     
    }

    char newDirection = backwards;
    /* While we still haven't found a valid direction */
    while (newDirection == backwards || !isValidDest(lookX,lookY))
    {
      /* If we've tried every location, turn around and break the loop */
      if (set.size()==3)
      {
        newDirection=backwards;
        break;
      }

      newX=x;
      newY=y;
      lookX=x;
      lookY=y;
      
      /* Randomly choose a direction */
      random = (int)(Math.random()*4) + 1;
      if (random == 1)
      {
        newDirection = 'L';
        newX-=increment; 
        lookX-= increment;
      }
      else if (random == 2)
      {
        newDirection = 'R';
        newX+=increment; 
        lookX+= gridSize;
      }
      else if (random == 3)
      {
        newDirection = 'U';
        newY-=increment; 
        lookY-=increment;
      }
      else if (random == 4)
      {
        newDirection = 'D';
        newY+=increment; 
        lookY+=gridSize;
      }
      if (newDirection != backwards)
      {
        set.add(new Character(newDirection));
      }
    } 
    return newDirection;
  }

  /* Random move function for dog */
  public void move()
  {
    lastX=x;
    lastY=y;
 
    /* If we can make a decision, pick a new direction randomly */
    if (isChoiceDest())
    {
      direction = newDirection();
    }
    
    /* If that direction is valid, move that way */
    switch(direction)
    {
      case 'L':
         if ( isValidDest(x-increment,y))
           x -= increment;
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
           x+= increment;
         break;     
      case 'U':
         if ( isValidDest(x,y-increment))
           y-= increment;
         break;     
      case 'D':
         if ( isValidDest(x,y+gridSize))
           y+= increment;
         break;     
    }
  }
}


/*This board class contains the player, dogs, pellets, and most of the game logic.*/
public class Board extends JPanel
{
  /* Initialize the images*/
  /* For JAR File*/
  /*
  Image farmerImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/farmer.jpg"));
  Image farmerUpImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/farmerup.jpg")); 
  Image farmerDownImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/farmerdown.jpg")); 
  Image farmerLeftImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/farmerleft.jpg")); 
  Image farmerRightImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/farmerright.jpg")); 
  Image dog10 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog10.jpg")); 
  Image dog20 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog20.jpg")); 
  Image dog30 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog30.jpg")); 
  Image dog40 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog40.jpg")); 
  Image dog11 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog11.jpg")); 
  Image dog21 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog21.jpg")); 
  Image dog31 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog31.jpg")); 
  Image dog41 = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/dog41.jpg")); 
  Image titleScreenImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/titleScreen.jpg")); 
  Image gameOverImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/gameOver.jpg")); 
  Image winScreenImage = Toolkit.getDefaultToolkit().getImage(Farmer.class.getResource("Pic/winScreen.jpg"));
  */
  /* For NOT JAR file*/
  Image farmerImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/farmer.png"); 
  Image farmerUpImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/farmerup.png"); 
  Image farmerDownImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/farmerdown.png"); 
  Image farmerLeftImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/farmerleft.png"); 
  Image farmerRightImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/farmerright.png"); 
  Image dog10 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog10.jpg"); 
  Image dog11 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog11.jpg"); 
  Image dog20 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog20.jpg");  
  Image dog30 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog30.jpg"); 
  Image dog40 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog40.jpg"); 
  Image dog21 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog21.jpg"); 
  Image dog31 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog31.jpg"); 
  Image dog41 = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/dog41.jpg");
  Image titleScreenImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/titleScreen.jpg"); 
  Image gameOverImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/gameOver.jpg"); 
  Image winScreenImage = Toolkit.getDefaultToolkit().getImage("D:\\Programming Exercise\\LATIHAN JAVA\\TUBES\\Pic/winScreen.jpg");

  /* Initialize the player and dogs */
  Player player = new Player(200,300);
  Dog dog1 = new Dog(180,180);
  Dog dog2 = new Dog(200,180);
  Dog dog3 = new Dog(220,180);
  Dog dog4 = new Dog(220,180);

  /* Timer is used for playing sound effects and animations */
  long timer = System.currentTimeMillis();

  /* Dying is used to count frames in the dying animation.  If it's non-zero,
     farmer is in the process of dying */
  int dying=0;
 
  /* Score information */
  int currScore;
  int highScore;

  /* if the high scores have been cleared, we have to update the top of the screen to reflect that */
  boolean clearHighScores= false;

  int numLives=2;

  /*Contains the game map, passed to player and dogs */
  boolean[][] state;

  /* Contains the state of all pellets*/
  boolean[][] pellets;

  /* Game dimensions */
  int gridSize;
  int max;

  /* State flags*/
  boolean stopped;
  boolean titleScreen;
  boolean winScreen = false;
  boolean overScreen = false;
  boolean demo = false;
  int New;

  /* Used to call sound effects */
  GameSounds sounds;

  int lastPelletEatenX = 0;
  int lastPelletEatenY=0;

  /* This is the font used for the menus */
  Font font = new Font("Monospaced",Font.BOLD, 12);

  /* Constructor initializes state flags etc.*/
  public Board() 
  {
    initHighScores();
    sounds = new GameSounds();
    currScore=0;
    stopped=false;
    max=400;
    gridSize=20;
    New=0;
    titleScreen = true;
  }

  /* Reads the high scores file and saves it */
  public void initHighScores()
  {
    File file = new File("highScores.txt");
    Scanner sc;
    try
    {
        sc = new Scanner(file);
        highScore = sc.nextInt();
        sc.close();
    }
    catch(Exception e)
    {
    }
  }

  /* Writes the new high score to a file and sets flag to update it on screen */
  public void updateScore(int score)
  {
    PrintWriter out;
    try
    {
      out = new PrintWriter("highScores.txt");
      out.println(score);
      out.close();
    }
    catch(Exception e)
    {
    }
    highScore=score;
    clearHighScores=true;
  }

  /* Wipes the high scores file and sets flag to update it on screen */
  public void clearHighScores()
  {
    PrintWriter out;
    try
    {
      out = new PrintWriter("highScores.txt");
      out.println("0");
      out.close();
    }
    catch(Exception e)
    {
    }
    highScore=0;
    clearHighScores=true;
  }

  /* Reset occurs on a new game*/
  public void reset()
  {
    numLives=2;
    state = new boolean[20][20];
    pellets = new boolean[20][20];

    /* Clear state and pellets arrays */
    for(int i=0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        state[i][j]=true;
        pellets[i][j]=true;
      }
    }

    /* Handle the weird spots with no pellets*/
    for(int i = 5;i<14;i++)
    {
      for(int j = 5;j<12;j++)
      {
        pellets[i][j]=false;
      }
    }
    pellets[9][7] = false;
    pellets[8][8] = false;
    pellets[9][8] = false;
    pellets[10][8] = false;

  }


  /* Function is called during drawing of the map.
     Whenever the a portion of the map is covered up with a barrier,
     the map and pellets arrays are updated accordingly to note
     that those are invalid locations to travel or put pellets
  */
  public void updateMap(int x,int y, int width, int height)
  {
    for (int i =x/gridSize; i<x/gridSize+width/gridSize;i++)
    {
      for (int j=y/gridSize;j<y/gridSize+height/gridSize;j++)
      {
        state[i-1][j-1]=false;
        pellets[i-1][j-1]=false;
      }
    }
  } 


  /* Draws the appropriate number of lives on the bottom left of the screen.
     Also draws the menu */
  public void drawLives(Graphics g)
  {
    g.setColor(Color.BLACK);

    /*Clear the bottom bar*/
    g.fillRect(0,max+5,600,gridSize);
    g.setColor(Color.YELLOW);
    for(int i = 0;i<numLives;i++)
    {
      /*Draw each life */
      g.fillOval(gridSize*(i+1),max+5,gridSize,gridSize);
    }
    /* Draw the menu items */
    g.setColor(Color.YELLOW);
    g.setFont(font);
    g.drawString("Reset",100,max+5+gridSize);
    g.drawString("Clear High Scores",180,max+5+gridSize);
    g.drawString("Exit",350,max+5+gridSize);
  }
  
  
  /*  This function draws the board.  The farmer board is really complicated and can only feasibly be done
      manually.  Whenever I draw a wall, I call updateMap to invalidate those coordinates.  This way the farmer
      and dogs know that they can't traverse this area */ 
  public void drawBoard(Graphics g)
  {
        g.setColor(Color.PINK);
        g.fillRect(0,0,600,600);
        g.setColor(Color.PINK);
        g.fillRect(0,0,420,420);
        
        g.setColor(Color.PINK);
        g.fillRect(0,0,20,600);
        g.fillRect(0,0,600,20);
        g.setColor(Color.WHITE);
        g.drawRect(19,19,382,382);
        g.setColor(Color.BLUE);

        g.fillRect(40,40,60,20);
          updateMap(40,40,60,20);
        g.fillRect(120,40,60,20);
          updateMap(120,40,60,20);
        g.fillRect(200,20,20,40);
          updateMap(200,20,20,40);
        g.fillRect(240,40,60,20);
          updateMap(240,40,60,20);
        g.fillRect(320,40,60,20);
          updateMap(320,40,60,20);
        g.fillRect(40,80,60,20);
          updateMap(40,80,60,20);
        g.fillRect(160,80,100,20);
          updateMap(160,80,100,20);
        g.fillRect(200,80,20,60);
          updateMap(200,80,20,60);
        g.fillRect(320,80,60,20);
          updateMap(320,80,60,20);

        g.fillRect(20,120,80,60);
          updateMap(20,120,80,60);
        g.fillRect(320,120,80,60);
          updateMap(320,120,80,60);
        g.fillRect(20,200,80,60);
          updateMap(20,200,80,60);
        g.fillRect(320,200,80,60);
          updateMap(320,200,80,60);

        g.fillRect(160,160,40,20);
          updateMap(160,160,40,20);
        g.fillRect(220,160,40,20);
          updateMap(220,160,40,20);
        g.fillRect(160,180,20,20);
          updateMap(160,180,20,20);
        g.fillRect(160,200,100,20);
          updateMap(160,200,100,20);
        g.fillRect(240,180,20,20);
        updateMap(240,180,20,20);
        g.setColor(Color.BLUE);


        g.fillRect(120,120,60,20);
          updateMap(120,120,60,20);
        g.fillRect(120,80,20,100);
          updateMap(120,80,20,100);
        g.fillRect(280,80,20,100);
          updateMap(280,80,20,100);
        g.fillRect(240,120,60,20);
          updateMap(240,120,60,20);

        g.fillRect(280,200,20,60);
          updateMap(280,200,20,60);
        g.fillRect(120,200,20,60);
          updateMap(120,200,20,60);
        g.fillRect(160,240,100,20);
          updateMap(160,240,100,20);
        g.fillRect(200,260,20,40);
          updateMap(200,260,20,40);

        g.fillRect(120,280,60,20);
          updateMap(120,280,60,20);
        g.fillRect(240,280,60,20);
          updateMap(240,280,60,20);

        g.fillRect(40,280,60,20);
          updateMap(40,280,60,20);
        g.fillRect(80,280,20,60);
          updateMap(80,280,20,60);
        g.fillRect(320,280,60,20);
          updateMap(320,280,60,20);
        g.fillRect(320,280,20,60);
          updateMap(320,280,20,60);

        g.fillRect(20,320,40,20);
          updateMap(20,320,40,20);
        g.fillRect(360,320,40,20);
          updateMap(360,320,40,20);
        g.fillRect(160,320,100,20);
          updateMap(160,320,100,20);
        g.fillRect(200,320,20,60);
          updateMap(200,320,20,60);

        g.fillRect(40,360,140,20);
          updateMap(40,360,140,20);
        g.fillRect(240,360,140,20);
          updateMap(240,360,140,20);
        g.fillRect(280,320,20,40);
          updateMap(280,320,20,60);
        g.fillRect(120,320,20,60);
          updateMap(120,320,20,60);
        drawLives(g);
  } 


  /* Draws the pellets on the screen */
  public void drawPellets(Graphics g)
  {
        g.setColor(Color.RED);
        for (int i=1;i<20;i++)
        {
          for (int j=1;j<20;j++)
          {
            if ( pellets[i-1][j-1])
            g.fillOval(i*20+8,j*20+8,4,4);
          }
        }
  }

  /* Draws one individual pellet.  Used to redraw pellets that dogs have run over */
  public void fillPellet(int x, int y, Graphics g)
  {
    g.setColor(Color.RED);
    g.fillOval(x*20+28,y*20+28,4,4);
  }

  /* This is the main function that draws one entire frame of the game */
  public void paint(Graphics g)
  {
    /* If we're playing the dying animation, don't update the entire screen.
       Just kill the farmer*/ 
    if (dying > 0)
    {
      /* Stop any farmer eating sounds */
      sounds.nomNomStop();

      /* Draw the farmer */
      g.drawImage(farmerImage,player.x,player.y,Color.BLACK,null);
      g.setColor(Color.BLACK);
      
      /* Kill the farmer */
      if (dying == 4)
        g.fillRect(player.x,player.y,20,7);
      else if ( dying == 3)
        g.fillRect(player.x,player.y,20,14);
      else if ( dying == 2)
        g.fillRect(player.x,player.y,20,20); 
      else if ( dying == 1)
      {
        g.fillRect(player.x,player.y,20,20); 
      }
     
      /* Take .1 seconds on each frame of death, and then take 2 seconds
         for the final frame to allow for the sound effect to end */ 
      long currTime = System.currentTimeMillis();
      long temp;
      if (dying != 1)
        temp = 100;
      else
        temp = 2000;
      /* If it's time to draw a new death frame... */
      if (currTime - timer >= temp)
      {
        dying--;
        timer = currTime;
        /* If this was the last death frame...*/
        if (dying == 0)
        {
          if (numLives==-1)
          {
            /* Demo mode has infinite lives, just give it more lives*/
            if (demo)
              numLives=2;
            else
            {
            /* Game over for player.  If relevant, update high score.  Set gameOver flag*/
              if (currScore > highScore)
              {
                updateScore(currScore);
              }
              overScreen=true;
            }
          }
        }
      }
      return;
    }

    /* If this is the title screen, draw the title screen and return */
    if (titleScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(titleScreenImage,0,0,Color.BLACK,null);

      /* Stop any farmer eating sounds */
      sounds.nomNomStop();
      New = 1;
      return;
    } 

    /* If this is the win screen, draw the win screen and return */
    else if (winScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(winScreenImage,0,0,Color.BLACK,null);
      New = 1;
      /* Stop any farmer eating sounds */
      sounds.nomNomStop();
      return;
    }

    /* If this is the game over screen, draw the game over screen and return */
    else if (overScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(gameOverImage,0,0,Color.BLACK,null);
      New = 1;
      /* Stop any farmer eating sounds */
      sounds.nomNomStop();
      return;
    }

    /* If need to update the high scores, redraw the top menu bar */
    if (clearHighScores)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,18);
      g.setColor(Color.YELLOW);
      g.setFont(font);
      clearHighScores= false;
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);
    }
   
    /* oops is set to true when farmer has lost a life */ 
    boolean oops=false;
    
    /* Game initialization */
    if (New==1)
    {
      reset();
      player = new Player(200,300);
      dog1 = new Dog(180,180);
      dog2 = new Dog(200,180);
      dog3 = new Dog(220,180);
      dog4 = new Dog(220,180);
      currScore = 0;
      drawBoard(g);
      drawPellets(g);
      drawLives(g);
      /* Send the game map to player and all dogs */
      player.updateState(state);
      /* Don't let the player go in the dog box*/
      player.state[9][7]=false; 
      dog1.updateState(state);
      dog2.updateState(state);
      dog3.updateState(state);
      dog4.updateState(state);
   
      /* Draw the top menu bar*/
      g.setColor(Color.YELLOW);
      g.setFont(font);
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);
      New++;
    }
    /* Second frame of new game */
    else if (New == 2)
    {
      New++;
    }
    /* Third frame of new game */
    else if (New == 3)
    {
      New++;
      /* Play the newGame sound effect */
      sounds.newGame();
      timer = System.currentTimeMillis();
      return;
    }
    /* Fourth frame of new game */
    else if (New == 4)
    {
      /* Stay in this state until the sound effect is over */
      long currTime = System.currentTimeMillis();
      if (currTime - timer >= 5000)
      {
        New=0;
      }
      else
        return;
    }
    
    /* Drawing optimization */
    g.copyArea(player.x-20,player.y-20,80,80,0,0);
    g.copyArea(dog1.x-20,dog1.y-20,80,80,0,0);
    g.copyArea(dog2.x-20,dog2.y-20,80,80,0,0);
    g.copyArea(dog3.x-20,dog3.y-20,80,80,0,0);
    g.copyArea(dog4.x-20,dog4.y-20,80,80,0,0);
    


    /* Detect collisions */
    if (player.x == dog1.x && Math.abs(player.y-dog1.y) < 10)
      oops=true;
    else if (player.x == dog2.x && Math.abs(player.y-dog2.y) < 10)
      oops=true;
    else if (player.x == dog3.x && Math.abs(player.y-dog3.y) < 10)
      oops=true;
    else if (player.x == dog4.x && Math.abs(player.y-dog4.y) < 10)
      oops=true;
    else if (player.y == dog1.y && Math.abs(player.x-dog1.x) < 10)
      oops=true;
    else if (player.y == dog2.y && Math.abs(player.x-dog2.x) < 10)
      oops=true;
    else if (player.y == dog3.y && Math.abs(player.x-dog3.x) < 10)
      oops=true;
    else if (player.y == dog4.y && Math.abs(player.x-dog4.x) < 10)
      oops=true;

    /* Kill the farmer */
    if (oops && !stopped)
    {
      /* 4 frames of death*/
      dying=4;
      
      /* Play death sound effect */
      sounds.death();
      /* Stop any farmer eating sounds */
      sounds.nomNomStop();

      /*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
      numLives--;
      stopped=true;
      drawLives(g);
      timer = System.currentTimeMillis();
    }

    /* Delete the players and dogs */
    g.setColor(Color.BLACK);
    g.fillRect(player.lastX,player.lastY,20,20);
    g.fillRect(dog1.lastX,dog1.lastY,20,20);
    g.fillRect(dog2.lastX,dog2.lastY,20,20);
    g.fillRect(dog3.lastX,dog3.lastY,20,20);
    g.fillRect(dog4.lastX,dog4.lastY,20,20);

    /* Eat pellets */
    if ( pellets[player.pelletX][player.pelletY] && New!=2 && New !=3)
    {
      lastPelletEatenX = player.pelletX;
      lastPelletEatenY = player.pelletY;

      /* Play eating sound */
      sounds.nomNom();
      
      /* Increment pellets eaten value to track for end game */
      player.pelletsEaten++;

      /* Delete the pellet*/
      pellets[player.pelletX][player.pelletY]=false;

      /* Increment the score */
      currScore += 50;

      /* Update the screen to reflect the new score */
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,20);
      g.setColor(Color.YELLOW);
      g.setFont(font);
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);

      /* If this was the last pellet */
      if (player.pelletsEaten == 173)
      {
        /*Demo mode can't get a high score */
        if (!demo)
        {
          if (currScore > highScore)
          {
            updateScore(currScore);
          }
          winScreen = true;
        }
        else
        {
          titleScreen = true;
        }
        return;
      }
    }

    /* If we moved to a location without pellets, stop the sounds */
    else if ( (player.pelletX != lastPelletEatenX || player.pelletY != lastPelletEatenY ) || player.stopped)
    {
      /* Stop any farmer eating sounds */
      sounds.nomNomStop();
    }


    /* Replace pellets that have been run over by dogs */
    if ( pellets[dog1.lastPelletX][dog1.lastPelletY])
      fillPellet(dog1.lastPelletX,dog1.lastPelletY,g);
    if ( pellets[dog2.lastPelletX][dog2.lastPelletY])
      fillPellet(dog2.lastPelletX,dog2.lastPelletY,g);
    if ( pellets[dog3.lastPelletX][dog3.lastPelletY])
      fillPellet(dog3.lastPelletX,dog3.lastPelletY,g);
    if ( pellets[dog4.lastPelletX][dog4.lastPelletY])
      fillPellet(dog4.lastPelletX,dog4.lastPelletY,g);


    /*Draw the dogs */
    if (dog1.frameCount < 5)
    {
      /* Draw first frame of dogs */
      g.drawImage(dog10,dog1.x,dog1.y,Color.BLACK,null);
      g.drawImage(dog20,dog2.x,dog2.y,Color.BLACK,null);
      g.drawImage(dog30,dog3.x,dog3.y,Color.BLACK,null);
      g.drawImage(dog40,dog4.x,dog4.y,Color.BLACK,null);
      dog1.frameCount++;
    }
    else
    {
      /* Draw second frame of dogs */
      g.drawImage(dog11,dog1.x,dog1.y,Color.BLACK,null);
      g.drawImage(dog21,dog2.x,dog2.y,Color.BLACK,null);
      g.drawImage(dog31,dog3.x,dog3.y,Color.BLACK,null);
      g.drawImage(dog41,dog4.x,dog4.y,Color.BLACK,null);
      if (dog1.frameCount >=10)
        dog1.frameCount=0;
      else
        dog1.frameCount++;
    }

    /* Draw the farmer */
    if (player.frameCount < 5)
    {
      /* Draw mouth closed */
      g.drawImage(farmerImage,player.x,player.y,Color.BLACK,null);
    }
    else
    {
      /* Draw mouth open in appropriate direction */
      if (player.frameCount >=10)
        player.frameCount=0;

      switch(player.currDirection)
      {
        case 'L':
           g.drawImage(farmerLeftImage,player.x,player.y,Color.BLACK,null);
           break;     
        case 'R':
           g.drawImage(farmerRightImage,player.x,player.y,Color.BLACK,null);
           break;     
        case 'U':
           g.drawImage(farmerUpImage,player.x,player.y,Color.BLACK,null);
           break;     
        case 'D':
           g.drawImage(farmerDownImage,player.x,player.y,Color.BLACK,null);
           break;     
      }
    }

    /* Draw the border around the game in case it was overwritten by dog movement or something */
    g.setColor(Color.WHITE);
    g.drawRect(19,19,382,382);

  }
}

