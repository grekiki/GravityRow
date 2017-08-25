import java.util.Vector;
import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet{
	data d;
	colour[] map;
	boolean reset=false;
	int dx;
	int dy;
	PFont f;
	int difficulty=2;
	Vector<animation> gravity=new Vector<animation>();
	public static void main(String[] args){
		PApplet.main("Main");
	}
	public void settings(){
		fullScreen();
	}
	public void setup(){
		frameRate(60);
		reset=false;
		f=createFont("Consolas",60,true);
		dx=width;
		dy=height;
		background(0,0,0);
		d=new data();
		map=new colour[5];
		map[0]=new colour(255,255,255);
		map[1]=new colour(128,128,128);
		map[2]=new colour(128,0,128);
		map[3]=new colour(255,0,0);
		map[4]=new colour(0,0,255);
	}
	public int tri(int n){
		double d=(Math.sqrt(1+4*n)-1)/2;
		return (int)Math.round(d);
	}
	public void aiMove() {
		if(difficulty==1) {
			
		}else if(difficulty==2) {
			tuple t=d.smart.bestMoveMedium();
			delay(400);
			gravity.addElement(d.attemptPlacement(t.x,t.y));
		}else if(difficulty==3) {
			tuple t=d.smart.bestMoveHard();
			delay(200);
			gravity.addElement(d.attemptPlacement(t.x,t.y));
		}else if(difficulty==4) {
			tuple t=d.smart.bestMoveHard();
			gravity.addElement(d.attemptPlacement(t.x,t.y));
		}
	}
	public void draw(){
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				colour c=map[d.grid[i][j]];
				fill(c.r,c.g,c.b);
				rect(i*dx/10,j*dy/10,dx/10,dy/10);
			}
		}
		for(int i=0; i<gravity.size(); i++){
			animation q=gravity.get(i);
			if(q.done>=q.work){
				d.fplace(q.x2,q.y2,q.red);
				gravity.removeElement(q);
			}else{
				fill(q.red?255:0,0,q.red?0:255);
				float x=(q.x1+(q.x2-q.x1)*((float)q.done)/q.work)*dx/10;
				float y=(q.y1+(q.y2-q.y1)*((float)q.done)/q.work)*dy/10;
				rect(x,y,dx/10,dy/10);
				q.done+=1+tri(q.done);
			}
		}
		//ai debug
		tuple t=d.smart.evaluate();
		textFont(f);
		fill(255,255,0);
		text(t.x+" "+t.y+" "+(t.y-t.x),50,50);
		// /ai debug
		if(d.winner()!=-1){
			textFont(f);
			fill(127,127,127);
			rect(dx/2-400,dy/2-60,800,120);
			fill(0,0,0);
			text("Zmagal je igralec "+d.winner(),dx/2-300,dy/2);
			reset=true;
		}
		if(d.redToMove&&gravity.size()==0&&!reset){
			aiMove();
		}
	}
	public void mousePressed(){
		if(reset){
			setup();
		}else {
		int x=mouseX;
		int y=mouseY;
		int i=10*x/dx;
		int j=10*y/dy;
		gravity.addElement(d.attemptPlacement(i,j));
		}
	}
	public void keyPressed() {
		int r=key-'a';
		if(r==0) {
			System.out.println("break");
		}
	}
}
class animation{
	int x1,x2,y1,y2;
	int work,done;
	boolean red;
	animation(int a,int b,int c,int d,boolean e){
		x1=a;
		x2=c;
		y1=b;
		y2=d;
		work=100*(x2-x1)*(x2-x1)+100*(y2-y1)*(y2-y1)+1;
		done=0;
		red=e;
	}
}
class colour{
	int r;
	int g;
	int b;
	colour(int a,int x,int c){
		r=a;
		g=x;
		b=c;
	}
}
class data{
	int[][] grid={{1,0,0,0,0,0,0,0,0,1},{0,1,0,0,0,0,0,0,1,0},{0,0,1,0,0,0,0,1,0,0},{0,0,0,1,0,0,1,0,0,0},{0,0,0,0,2,2,0,0,0,0},{0,0,0,0,2,2,0,0,0,0},{0,0,0,1,0,0,1,0,0,0},{0,0,1,0,0,0,0,1,0,0},{0,1,0,0,0,0,0,0,1,0},{1,0,0,0,0,0,0,0,0,1}};
	AI smart=new AI(0,10,1000,100000,0.45);
	
	boolean[][]filled=new boolean[10][10];
	boolean redToMove=true;
	animation attemptPlacement(int i,int j){
		if(grid[i][j]>=2){
			return new animation(-1,-1,-1,-1,!redToMove);
		}
		if(i==j||i==9-j){
			place(i,j);
			redToMove=!redToMove;
			return new animation(i,j,i,j,!redToMove);
		}
		if(i>j){
			if(i<9-j){
				for(int q=0; q<=j; q++){
					if(place(i,q)){
						redToMove=!redToMove;
						return new animation(i,j,i,q,!redToMove);
					}
				}
			}else{
				for(int q=9; q>=i; q--){
					if(place(q,j)){
						redToMove=!redToMove;
						return new animation(i,j,q,j,!redToMove);
					}
				}
			}
		}else{
			if(i<9-j){
				for(int q=0; q<=i; q++){
					if(place(q,j)){
						redToMove=!redToMove;
						return new animation(i,j,q,j,!redToMove);
					}
				}
			}else{
				for(int q=9; q>=j; q--){
					if(place(i,q)){
						redToMove=!redToMove;
						return new animation(i,j,i,q,!redToMove);
					}
				}
			}
		}
		return new animation(-1,-1,-1,-1,!redToMove);
		
	}
	boolean place(int i,int j){
		if(grid[i][j]>=2){
			return false;
		}
		if(filled[i][j])return false;
		filled[i][j]=true;
		return true;
	}
	void fplace(int i,int j,boolean r){
		if(i!=-1&&j!=-1){
			grid[i][j]=r? 3 : 4;
			smart.move(i,j,r);
		}
	}
	int winner(){
		int c=3;
		for(int i=0; i<7; i++){
			for(int j=0; j<10; j++){
				if(grid[i][j]==c&&grid[i+1][j]==c&&grid[i+2][j]==c&&grid[i+3][j]==c){
					return 1;
				}
			}
		}
		for(int i=0; i<10; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i][j+1]==c&&grid[i][j+2]==c&&grid[i][j+3]==c){
					return 1;
				}
			}
		}
		for(int i=0; i<7; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i+1][j+1]==c&&grid[i+2][j+2]==c&&grid[i+3][j+3]==c){
					return 1;
				}
			}
		}
		for(int i=3; i<10; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i-1][j+1]==c&&grid[i-2][j+2]==c&&grid[i-3][j+3]==c){
					return 1;
				}
			}
		}
		c=4;
		for(int i=0; i<7; i++){
			for(int j=0; j<10; j++){
				if(grid[i][j]==c&&grid[i+1][j]==c&&grid[i+2][j]==c&&grid[i+3][j]==c){
					return 2;
				}
			}
		}
		for(int i=0; i<10; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i][j+1]==c&&grid[i][j+2]==c&&grid[i][j+3]==c){
					return 2;
				}
			}
		}
		for(int i=0; i<7; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i+1][j+1]==c&&grid[i+2][j+2]==c&&grid[i+3][j+3]==c){
					return 2;
				}
			}
		}
		for(int i=3; i<10; i++){
			for(int j=0; j<7; j++){
				if(grid[i][j]==c&&grid[i-1][j+1]==c&&grid[i-2][j+2]==c&&grid[i-3][j+3]==c){
					return 2;
				}
			}
		}
		return -1;
	}
}




