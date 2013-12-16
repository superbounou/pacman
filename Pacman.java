import java.awt.*;

import javax.swing.*;
import java.awt.event.*; 
import java.util.*;
import java.awt.image.*;


public class Pacman 
{
	static final long serialVersionUID = 0 ;
	
	private static JFrame win ;
	private static Thread gameThread;

	
	private static WindowListener closer = new WindowAdapter()
	{
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	};
	
	public String getAppletInfo()
	{
		return("PacMan - by Nicolas Bounoughaz");
	}
	
	private static class GameLoop implements Runnable, KeyListener 
	{
		
		private static Canvas gui;
		
		private static Random rand ;
		private static ArrayList<int[]> beans, superbeans, ghosts, intersections ;	
		private static int xpacman, ypacman, tempo, level = 1, life = 3, score = 0, modscore = 10000 ;
		private static Image pac0, pac1, pac2, pac3, piclife, picwall, picghostr, picghosty, picghostv, picghostg, picghostafraid, picghostnaked;
		private static byte mode = 0, direction, nextdirection, pacmanspeed = 2, demo = 1 ;
		private static Font font ;
		
		
		////21 * 18
		private final static byte[] laby = {	0,2,0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0,
											 	0,1,1,1,1,0,0,0,0,0,4,0,0,0,0,0,1,1,1,1,0,
											 	0,0,0,0,0,0,1,1,1,0,2,0,1,1,1,0,0,0,0,0,2,
											 	0,1,1,1,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,0,
											 	0,1,1,1,1,1,0,1,1,0,1,0,1,1,0,1,1,1,1,1,0,
											 	0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
											 	1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,
											 	1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,
											 	1,0,1,0,1,0,1,9,9,9,9,9,9,9,1,0,1,0,1,0,1,
											 	1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,
											 	1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1,
											 	0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,
											 	0,1,1,1,1,0,1,1,1,1,0,1,1,1,1,0,1,1,1,1,0,
											 	0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,
											 	0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,
											 	0,1,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,
											 	0,1,1,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,0,
											 	0,0,0,2,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0
											},
											
									speeds = { 1,2,4,5 };							 	
		
		private void init()
		{
			
			direction = 10 ;
			nextdirection = 10;	
			bbb = 0 ;
			if(demo==0)
			{
				xpacman = 210;
				ypacman = 270;
			}
			else
			{
				xpacman = -100;
				ypacman = -100;
			}
			if(superbeans==null)
				superbeans = new ArrayList<int[]>();
			else
				superbeans.clear();
			if(beans==null)
				beans = new ArrayList<int[]>();
			else
				beans.clear();
			if(ghosts==null)
				ghosts = new ArrayList<int[]>();
			else
				ghosts.clear();
			
			int i;
			for(i=0;i<level+1;i++)
			{
				int t[] = new int[6];
				t[0] = 210 ;
				t[1] = 170 ;
				t[2] = (i+1>((level+1)/2)?0:1);
				t[3] = (i+1>((level+1)/2)?1:0) ;
				t[4] = 0 ;
				t[5] = speeds[i%4];
				ghosts.add(t);
			}
			
		}

		private void reload()
		{
			life -- ;
			direction = 10 ;
			nextdirection = 10;
			bbb = 1;
			xpacman = 210;
			ypacman = 270;
			
		}
		
		private void nextLevel()
		{
			level = level + 1;
			tempo = 0 ;
			mode = 0;
			bbb = 0;
			init();
		}
		
		public GameLoop(Canvas canvas) 
		{
			gui = canvas ;
			rand = new Random();
			font = new Font("Courier", Font.BOLD,18);
			level = 55;
			picghostr = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghostr.gif"));//.getImage();
			picghosty = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghosty.gif"));//.getImage();
			picghostg = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghostg.gif"));//.getImage();
			picghostv = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghostv.gif"));//.getImage();
			picghostafraid = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghostafraid.gif"));//.getImage();
			picghostnaked = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/ghostnaked.gif"));//.getImage();
			pac0 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/pacman0.gif"));//.getImage();
			pac1 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/pacman1.gif"));//.getImage();
			pac2 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/pacman2.gif"));//.getImage();
			pac3 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/pacman3.gif"));//.getImage();
			picwall = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/wall.gif"));//.getImage();
			piclife = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/pacmanlife.gif"));
			intersections = new ArrayList<int[]>();
			int i = 0;
			int t[] = new int[3];
			for(int y=0;y<360;y = y + 20)
				for(int x=0;x<420;x = x + 20)
				{
					if(laby[i]==0 || laby[i]==3//dégueulasse :x 
					  && (!(i==20 || i==41|| i==62|| i==83|| i==104|| i==125|| i==146|| i==167|| i==188|| i==209|| i==230|| i==251|| i==272|| i==293|| i==314|| i==335|| i==356|| i==377)
							&& laby[(i+21>=laby.length?177:i+21)]=='0' && laby[(i+1>=laby.length?177:i+1)]==0)
					 || (i%21!=0 && laby[(i-20<=0?177:i-21)]==0 && laby[(i-1<=0?177:i-1)]==0)
					 || (!(i==20 || i==41|| i==62|| i==83|| i==104|| i==125|| i==146|| i==167|| i==188|| i==209|| i==230|| i==251|| i==272|| i==293|| i==314|| i==335|| i==356|| i==377) 
							&& laby[(i-20<=0?177:i-21)]=='0' && laby[(i+1>=laby.length?177:i+1)]==0)
					 || (i%21!=0 && laby[(i+21>=laby.length?177:i+21)]==0 && laby[(i-1<=0?177:i-1)]==0)  	
					)
					{
						t = new int[3];
						t[0] = x ;
						t[1] = y ;
						t[2] = (laby[i]==3?1:0);
						intersections.add(t);
					}
					i ++ ;
				}
			init(); 
		}
		
		private boolean isLegal(int xp,int yp)
		{
			if(xp<10 || xp>410 || yp>350 || yp<10 )
				return false ;
			int i = 0;
			for(int y=0;y<360;y = y + 20)
				for(int x=0;x<420;x = x + 20)
				{
					if(laby[i]==1 || laby[i]==4)
					{
						int x2, x3, x4, y1, y3, y4 ;
						x3 = xp - 9;
						y3 = yp + 9 ;
						x4 = xp + 9 ;
						y4 = yp - 9 ;
						y1 = y+20;
						x2 = x+20;
						if((x2>=x3)&&(x4>=x)&&(y<=y3)&&(y4<=y1))
							return false;
					}
					i ++ ;
				}
			return true ;
		}
		
		private int bbb = 0 ;
		
		public void run()
		{
			gui.createBufferStrategy(2);
			BufferStrategy strategy = gui.getBufferStrategy();
			long  starttime;
			try
			{	
				while(true)
				{ 
					starttime = System.currentTimeMillis();
					starttime += 37;
					Graphics g = strategy.getDrawGraphics();
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
					/** Désactivation de l'anti-aliasing */
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					/** Demande de rendu rapide */
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
					g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
					g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
					g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
					
					
					//Dessin du score et du fond
					g2.setColor(Color.BLACK);
					g2.fillRect(0,0,450,430);
					g2.setColor(Color.WHITE);
					g2.setFont(font);
				
					if(demo==0)
					{
						g2.drawString("Vie  ", 10,390);					
						g2.drawString("Score ", 150,390);
						g2.drawString(Integer.toString(score), 270,390);
					}
					//dessin des murs et des graines
					int i = 0, b = 0;
					if(demo==0)
						for(i=0;i<life;i++)
							g2.drawImage(piclife,(i*20)+50,375,20,20,null);
					i = 0;
					b = 0;
					int yu[] ;
					for(int y=0;y<360;y = y + 20)
						for(int x=0;x<420;x = x + 20)
						{
							if(laby[i]==1 || laby[i]==3)
								g2.drawImage(picwall,x,y,20,20,null);
							else if(laby[i]==4)
								g2.drawImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("img/chabal.gif")),x,y,20,20,null);
							else if(laby[i]==0)
							{
								if(bbb==0 && demo==0)
								{
									int t[] = new int[2];
									t[0] = x + 10;
									t[1] = y + 10 ;
									beans.add(t);
									b ++ ;
								}
							}
							else if(laby[i]==2)
							{
								if(bbb==0)
								{
									int t[] = new int[2];
									t[0] = x + 10;
									t[1] = y + 10 ;
									superbeans.add(t);
									b ++ ;
								}
							}
							i ++ ;
						}
					bbb = 1 ;
					g2.setColor(new Color(205,198,151));
					for(int pop=0;pop<superbeans.size();pop++)
					{
						yu = superbeans.get(pop);
						if(yu[0]==xpacman && yu[1]==ypacman)
						{
							superbeans.remove(pop);
							score = score + 50 ;
							mode = 1 ;
						    tempo = 120 ;
						}
						else if(bbb==1)
							g2.fillRect(yu[0]-4,yu[1]-4,6,6);
					}
					for(int pop=0;pop<beans.size();pop++)
					{
						yu = beans.get(pop);
						g2.fillRect(yu[0],yu[1],2,2);
					}	
					for(int pop=0;pop<beans.size();pop++)
					{
						yu = beans.get(pop);
						if(yu[0]==xpacman && yu[1]==ypacman)
						{
							beans.remove(pop);
							score = score + 10 ;
						}
					}
					byte out = 0 ;
					
					if(demo==0 && score>=modscore && score<modscore+10000)
					{
						modscore = modscore + 10000;
						life ++ ;
					}
					
					for(int oil=0;oil<ghosts.size();oil++)
					{
						
						int popo[] = ghosts.get(oil);
						int x1, x2, x3, x4, y1, y2, y3, y4 ;
						x1 = xpacman - 8 ;
						y1 = ypacman + 8 ;
						x2 = xpacman + 8 ;
						y2 = ypacman - 8 ;
						x3 = popo[0] - 8 ;
						y3 = popo[1] + 8 ;
						x4 = popo[0] + 8 ;
						y4 = popo[1] - 8 ;
						if(mode==1 && popo[4]==2)
							popo[4] = 2 ;
						else if(mode==1)
							popo[4] = 1 ;
						else
							popo[4] = 0 ;
						if((x2>=x3)&&(x4>=x1)&&(y2<=y3)&&(y4<=y1))//collision avec un fantome
						{
							if(mode==1)
							{
								score = score + 100 ;
								popo[4] = 2 ;
							}
							else
								if(life==1)
								{
									g2.setColor(new Color(187,183,83));
									g2.fillRect(70,70,300,200);						
									g2.setColor(new Color(99,96,78));
									g2.fillRect(68,68,300,200);
									g2.setColor(Color.BLACK);
									g2.drawString("GAME OVER",170,100);
									g2.drawString("Your score",160,150);
									g2.drawString(Integer.toString(score),150,200);
									g2.dispose();
									strategy.show();
									Thread.sleep(3000);
									demo = 1 ;
									level = 55;
									life = 3;
									bbb = 0 ;
									score = 0 ;
									init();
								}
								else
								{
									Thread.sleep(1200);
									ghosts.clear();
									for(i=0;i<level+1;i++)
									{
										int t[] = new int[6];
										t[0] = 210 ;
										t[1] = 170 ;
										t[2] = (i+1>((level+1)/2)?0:1);
										t[3] = (i+1>((level+1)/2)?1:0) ;
										t[4] = 0 ;
										t[5] = speeds[i%4];
										ghosts.add(t);
									}
									reload();
									out = 1;
								}
						}
						if(out==0)
						{
							for(int u=0;u<intersections.size();u++)
							{
								int inter[] = intersections.get(u);
								
								if((inter[0]==popo[0]-10)&&(inter[1]==popo[1]-10))
								{
									boolean yes = true ;
									while(yes)
									{
										int kol = Math.abs(rand.nextInt()%4);
										if(kol == 0 && isLegal(popo[0]-popo[5],popo[1]) && popo[3]!=0)
										{
											popo[3] = 1 ; popo[2]= kol ; yes = false ;
										}		 
										else if(kol == 1 && isLegal(popo[0]+popo[5],popo[1]) && popo[3]!=1)
										{
											popo[3] = 0 ; popo[2]= kol ; yes = false ;
										}
										else if(kol == 2 && isLegal(popo[0],popo[1]+popo[5]) && popo[3]!=2)
										{
											popo[3] = 3 ; popo[2]= kol ; yes = false ;
										}
										else if(kol == 3 && isLegal(popo[0],popo[1]-popo[5]) && popo[3]!=3)
										{
											popo[3] = 2 ; popo[2]= kol ; yes = false ;
										}
									}
								}
							}				
							switch(popo[2])
							{
								case 0 : { popo[0] = popo[0]-popo[5]; }
										 break ;
								case 1 : { popo[0] = popo[0]+popo[5];}
										 break ;
								case 2 : { popo[1] = popo[1]+popo[5]; } 
										 break ;
								case 3 : { popo[1] = popo[1]-popo[5]; }
							}
							
							if(ghosts.size()>0 && oil<ghosts.size())
								ghosts.set(oil,popo);
							if(popo[4]==0)
							{
								if(popo[5]==1)
									g2.drawImage(picghostr,popo[0]-10,popo[1]-10,20,20,null);
								else if(popo[5]==2)
									g2.drawImage(picghosty,popo[0]-10,popo[1]-10,20,20,null);
								else if(popo[5]==4)
									g2.drawImage(picghostg,popo[0]-10,popo[1]-10,20,20,null);
								else if(popo[5]==5)
									g2.drawImage(picghostv,popo[0]-10,popo[1]-10,20,20,null);
							}
							else if(popo[4]==1)
								g2.drawImage(picghostafraid,popo[0]-10,popo[1]-10,20,20,null);
							else if(popo[4]==2)
								g2.drawImage(picghostnaked,popo[0]-10,popo[1]-10,20,20,null);
						}
					}
					if(demo==0)
					{
						if(mode==1 && tempo>0)
							tempo -- ;
						else
							mode = 0;
						
						if(nextdirection==direction)
							nextdirection = 10;
						if(!(nextdirection==10))
						{
							if(nextdirection==0 && isLegal(xpacman-speeds[pacmanspeed],ypacman))
							{
								direction = 0;
								nextdirection = 10;
							}
							else if(nextdirection==1 && (isLegal(xpacman+speeds[pacmanspeed],ypacman)))
							{
								direction = 1;
								nextdirection = 10;
							}
							else if(nextdirection==2 && (isLegal(xpacman,ypacman+speeds[pacmanspeed])))
							{
								direction = 2;
								nextdirection = 10;
							}
							else if(nextdirection==3 && (isLegal(xpacman,ypacman-speeds[pacmanspeed])))
							{
								direction = 3;
								nextdirection = 10 ;
							}
						}		
						if(direction==0 && isLegal(xpacman-speeds[pacmanspeed],ypacman))
						{
							g2.drawImage(pac0,xpacman-10,ypacman-10,20,20,null);
							xpacman = xpacman - speeds[pacmanspeed];
						}
						else if(direction==1 && isLegal(xpacman+speeds[pacmanspeed],ypacman))
						{
							g2.drawImage(pac1,xpacman-10,ypacman-10,20,20,null);
							xpacman = xpacman + speeds[pacmanspeed];
						}
						else if(direction==2 && isLegal(xpacman,ypacman+speeds[pacmanspeed]))
						{
							g2.drawImage(pac2,xpacman-10,ypacman-10,20,20,null);
							ypacman = ypacman + speeds[pacmanspeed];
						}
						else if(direction==3&& isLegal(xpacman,ypacman-speeds[pacmanspeed]))
						{
							g2.drawImage(pac3,xpacman-10,ypacman-10,20,20,null);
							ypacman = ypacman - speeds[pacmanspeed];
						}
						else
							g2.drawImage(pac3,xpacman-10,ypacman-10,20,20,null);
						if(beans.size()==0 && demo==0)
						{
							g2.setColor(new Color(187,183,83));
							g2.fillRect(123,71,200,150);						
							g2.setColor(new Color(99,96,78));
							g2.fillRect(120,68,200,150);
							g2.setColor(Color.BLACK);
							g2.drawString("Bravo !",190,100);
							g2.drawString("Prochain level",150,150);
							g2.drawString("Level " + (level+1),180,190);
							g2.dispose();
							strategy.show();
							Thread.sleep(2500);
							nextLevel();
						}
					}
					if(demo==1)
					{
						g2.setColor(new Color(187,183,83));
						g2.fillRect(70,70,300,200);						
						g2.setColor(new Color(99,96,78));
						g2.fillRect(68,68,300,200);
						g2.setColor(Color.BLACK);
						g2.drawString("PacMan ©",180,100);
						g2.drawString("by Nicolas Bounoughaz",100,130);
						g2.drawString("superbounou@gmail.com",100,150);
						g2.drawString("Press X to start",130,200);
						
					}
					g2.dispose();
					strategy.show();
					Thread.sleep(Math.max(0, starttime-System.currentTimeMillis()));
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		
		public void keyPressed(KeyEvent e)
		{
			
			switch(e.getKeyCode())
			{
				case 37 : if(isLegal(xpacman-speeds[2],ypacman)) direction = 0; else nextdirection = 0;
						  break;
				case 39 : if(isLegal(xpacman+speeds[2],ypacman)) direction = 1; else nextdirection = 1;
						  break;
				case 40 : if(isLegal(xpacman,ypacman+speeds[2])) direction = 2; else nextdirection = 2;
						  break;
				case 38 : if(isLegal(xpacman,ypacman-speeds[2])) direction = 3; else nextdirection = 3;
						  break;
				case 88 : if(demo==1){demo = 0 ;level=1;init();}
			}
		}
	
	    public void keyReleased(KeyEvent e)
	    {
	    }

	    public void keyTyped(KeyEvent e)
	    {	
	    }	
	}
	
	public static void main(String argv[]) 
	{
		win = new JFrame("Pacman");
		win.setResizable(false);
		Canvas gui = new Canvas();	
		win.getContentPane().add(gui);
		win.setSize(425,435);
		win.setLocation(300,160);
		win.setVisible(true); 
		win.addWindowListener(closer);
		GameLoop gameloop = new GameLoop(gui);
		gameThread = new Thread(gameloop);
		win.addKeyListener(gameloop);
		gameThread.setPriority(Thread.MAX_PRIORITY);
		gameThread.start();
	}
}