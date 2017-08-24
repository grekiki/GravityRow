import java.util.ArrayList;

class tuple{
	int x;
	int y;
	tuple(int a,int b){
		x=a;y=b;
	}
}
class r4{
	int x0;
	int y0;
	int x1;
	int y1;
	r4(int a,int b,int c,int d){
		x0=a;x1=c;y0=b;y1=d;
	}
	tuple[] walk(){
		tuple[]q=new tuple[4];
		if(x1!=x0){//garantiramo x1>x0
			for(int i=x0;i<=x1;i++){
				q[i-x0]=new tuple(i,y0+(y1-y0)*(i-x0)/3);
			}
		}else{
			for(int i=0;i<4;i++){
				q[i]=new tuple(x0,y0+(y1-y0)*i/3);
			}
		}
		return q;
	}
}
class AIeasy{
	int infty=1000000000;
	private int[][] grid={
			{1,1,1,1,1,1,1,1,1,1},
			{1,1,0,0,0,0,0,0,1,1},
			{1,0,1,0,0,0,0,1,0,1},
			{1,0,0,1,0,0,1,0,0,1},
			{1,0,0,0,2,2,0,0,0,1},
			{1,0,0,0,2,2,0,0,0,1},
			{1,0,0,1,0,0,1,0,0,1},
			{1,0,1,0,0,0,0,1,0,1},
			{1,1,0,0,0,0,0,0,1,1},
			{1,1,1,1,1,1,1,1,1,1}
			};
	ArrayList<r4> combo=new ArrayList<r4>(217);
	public AIeasy(){
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(i+3<10){
					combo.add(new r4(i,j,i+3,j));
				}
				if(j+3<10){
					combo.add(new r4(i,j,i,j+3));
				}
				if(i+3<10&&j+3<10){
					combo.add(new r4(i,j,i+3,j+3));
				}
				if(i+3<10&&j-3>=0){
					combo.add(new r4(i,j,i+3,j-3));
				}
			}
		}
	}
	int opponentScore(boolean red){
		int count=0;
		int block=2;
		int opponent=3+(red?1:0);
		int me=3+(red?0:1);
		for(r4 spam:combo){
			tuple[]q=spam.walk();
			int opp=0;
			for(tuple r:q){
				if(grid[r.x][r.y]==block||grid[r.x][r.y]==me){
					opp=0;
					break;
				}else if(grid[r.x][r.y]==opponent){
					opp++;
				} 
			}
			count+=opp==0?1:(opp==1?100:(opp==2?10000:(opp==3?1000000:100000000)));
		}
		return count;
	}
	tuple evaluate(){
		return new tuple(10*opponentScore(true)/9,opponentScore(false));//bolj obrambno
	}
	ArrayList<tuple> moves(){
		ArrayList<tuple> m=new ArrayList<tuple>();
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(grid[i][j]==1){
					m.add(new tuple(i,j));
				}
			}
		}
		return m;
	}
	tuple move(int i,int j,boolean r){
		if(grid[i][j]!=1){
			System.out.println("ERROR!!!!");
			System.out.println(1/0);
			return null;
		}else{
			int c=3+(r?0:1);
			if(i==j||i==9-j){
				grid[i][j]=c;
			}else if(i>j){
				if(i<9-j){
					grid[i][j]=c;
					if(grid[i][j+1]==0){
						grid[i][j+1]=1;
						return new tuple(i,j+1);
					}
				}else{
					grid[i][j]=c;
					if(grid[i-1][j]==0){
						grid[i-1][j]=1;
						return new tuple(i-1,j);
					}
				}
			}else{
				if(i<9-j){
					grid[i][j]=c;
					if(grid[i+1][j]==0){
						grid[i+1][j]=1;
						return new tuple(i+1,j);
					}
				}else{
					grid[i][j]=c;
					if(grid[i][j-1]==0){
						grid[i][j-1]=1;
						return new tuple(i,j-1);
					}
				}
			}
		}
		return null;
	}
	tuple bestMove(){//AI je vedno rdeč
		long t0=System.nanoTime();
		tuple solution=null;
		ArrayList<tuple> moves=moves();
		int bestPossible=-infty;
		for(tuple q:moves){
			tuple reset=move(q.x,q.y,true);
			int min=infty;
			ArrayList<tuple> moves2=moves();
			for(tuple q2:moves2){
				tuple reset2=move(q2.x,q2.y,false);
				tuple e=evaluate();
				int x=e.y-e.x;
				min=Math.min(min,x);
				if(reset2!=null)grid[reset2.x][reset2.y]=0;
				grid[q2.x][q2.y]=1;
			}
			if(min>bestPossible){
				bestPossible=min;
				solution=q;
			}
			if(reset!=null)grid[reset.x][reset.y]=0;
			grid[q.x][q.y]=1;
		}
		long t1=System.nanoTime();
		System.out.println((t1-t0)/1000000);
		return solution;
	}
}

















