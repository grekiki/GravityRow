import java.util.ArrayList;
class tuple {
	int x;
	int y;
	tuple(int a, int b) {
		x = a;
		y = b;
	}
}
class r4p {
	int x0;
	int y0;
	int x1;
	int y1;
	int red;
	int blue;
	int block;
	int empty;
	int score(boolean r, int a, int b, int c, int d) {
		int me = r ? red : blue;
		int opp = r ? blue : red;
		if (me > 0 || block > 0) {
			return 0;
		}
		return opp == 0 ? 0 : (opp == 1 ? a : (opp == 2 ? b : (opp == 3 ? c : (r ? 100000000 : d))));
	}
	r4p(int a, int b, int c, int d) {
		x0 = a;
		x1 = c;
		y0 = b;
		y1 = d;
		red = 0;
		blue = 0;
		block = 0;
		empty = 0;
	}
	tuple[] walk() {
		tuple[] q = new tuple[4];
		if (x1 != x0) {// garantiramo x1>x0
			for (int i = x0; i <= x1; i++) {
				q[i - x0] = new tuple(i, y0 + (y1 - y0) * (i - x0) / 3);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				q[i] = new tuple(x0, y0 + (y1 - y0) * i / 3);
			}
		}
		return q;
	}
}
class AI {
	int[] settings = new int[4];
	double attack;
	int infty = 1000000000;
	boolean fm = true;// first move
	private int[][] grid = { 
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 0, 0, 0, 0, 0, 0, 1, 1 },
			{ 1, 0, 1, 0, 0, 0, 0, 1, 0, 1 },
			{ 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
			{ 1, 0, 0, 0, 2, 2, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 2, 2, 0, 0, 0, 1 },
			{ 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
			{ 1, 0, 1, 0, 0, 0, 0, 1, 0, 1 },
			{ 1, 1, 0, 0, 0, 0, 0, 0, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
	ArrayList<r4p> combo = new ArrayList<r4p>(217);
	ArrayList<Integer>[][] pointers = new ArrayList[10][10];
	void invert() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				if(grid[i][j]==3) {
					grid[i][j]=4;
				}else if(grid[i][j]==4) {
					grid[i][j]=3;
				}
			}
		}
	}
	public AI(int a, int b, int cc, int d, double e) {
		settings[0] = a;
		settings[1] = b;
		settings[2] = cc;
		settings[3] = d;
		attack = e;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				pointers[i][j] = new ArrayList<Integer>();
				if (i + 3 < 10) {
					combo.add(new r4p(i, j, i + 3, j));
				}
				if (j + 3 < 10) {
					combo.add(new r4p(i, j, i, j + 3));
				}
				if (i + 3 < 10 && j + 3 < 10) {
					combo.add(new r4p(i, j, i + 3, j + 3));
				}
				if (i + 3 < 10 && j - 3 >= 0) {
					combo.add(new r4p(i, j, i + 3, j - 3));
				}
			}
		}
		for (int i = 0; i < combo.size(); i++) {
			r4p q = combo.get(i);
			for (tuple p : q.walk()) {
				pointers[p.x][p.y].add(i);
				int c = grid[p.x][p.y];
				if (c == 0) {
					q.empty++;
				} else if (c == 1) {
					q.empty++;
				} else if (c == 2) {
					q.block++;
				} else if (c == 3) {
					q.red++;
				} else if (c == 4) {
					q.blue++;
				} else {
					int t = 1 / 0;
				}
			}
		}
	}
	int opponentScore(boolean red) {
		int count = 0;
		for (r4p spam : combo) {
			count += spam.score(red, settings[0], settings[1], settings[2], settings[3]);
		}
		return count;
	}
	tuple evaluate() {
		return new tuple((int) Math.round(2 * (1 - attack) * opponentScore(true)),
				(int) Math.round(2 * attack * opponentScore(false)));// bolj obrambno
	}
	int eval() {// + dobro za r,- dobro za m
		tuple p = evaluate();
		return p.y - p.x;
	}
	ArrayList<tuple> moves() {
		ArrayList<tuple> m = new ArrayList<tuple>();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (grid[i][j] == 1) {
					m.add(new tuple(i, j));
				}
			}
		}
		return m;
	}
	tuple move(int i, int j, boolean r) {
		if (grid[i][j] != 1) {
			System.out.println("ERROR!!!!");
			System.out.println(1 / 0);
			return null;
		} else {
			// popravimo pointerje
			for (int p : pointers[i][j]) {
				combo.get(p).empty--;
				if (r) {
					combo.get(p).red++;
				} else {
					combo.get(p).blue++;
				}
			}
			// račun novega mesta...
			int c = 3 + (r ? 0 : 1);
			if (i == j || i == 9 - j) {
				grid[i][j] = c;
			} else if (i > j) {
				if (i < 9 - j) {
					grid[i][j] = c;
					if (grid[i][j + 1] == 0) {
						grid[i][j + 1] = 1;
						return new tuple(i, j + 1);
					}
				} else {
					grid[i][j] = c;
					if (grid[i - 1][j] == 0) {
						grid[i - 1][j] = 1;
						return new tuple(i - 1, j);
					}
				}
			} else {
				if (i < 9 - j) {
					grid[i][j] = c;
					if (grid[i + 1][j] == 0) {
						grid[i + 1][j] = 1;
						return new tuple(i + 1, j);
					}
				} else {
					grid[i][j] = c;
					if (grid[i][j - 1] == 0) {
						grid[i][j - 1] = 1;
						return new tuple(i, j - 1);
					}
				}
			}
		}
		return null;// ni nove poteze
	}
	void reverseMove(int i, int j, tuple removeOne) {
		if (removeOne != null) {
			grid[removeOne.x][removeOne.y] = 0;
		}
		boolean r = grid[i][j] == 3;
		grid[i][j] = 1;
		for (int p : pointers[i][j]) {
			combo.get(p).empty++;
			if (r) {
				combo.get(p).red--;
			} else {
				combo.get(p).blue--;
			}
		}
	}
	tuple bestMoveEasy() {
		if (fm) {
			fm = false;
			return fm();
		}
		long t0 = System.nanoTime();
		tuple solution = null;
		ArrayList<tuple> moves = moves();
		int bestPossible = -infty;
		for (tuple q : moves) {
			tuple reset = move(q.x, q.y, true);
			int min = eval();
			if (min > bestPossible) {
				bestPossible = min;
				solution = q;
			}
			reverseMove(q.x, q.y, reset);
		}
		long t1 = System.nanoTime();
		//System.out.println((t1 - t0) / 1000000);
		return solution;
	}
	tuple bestMoveMedium() {
		if (fm) {
			fm = false;
			return fm();
		}
		long t0 = System.nanoTime();
		tuple solution = null;
		ArrayList<tuple> moves = moves();
		int bestPossible = -infty;
		for (tuple q : moves) {
			tuple reset = move(q.x, q.y, true);
			if(winner()==1) {
				reverseMove(q.x, q.y, reset);
				return q;
			}
			int min = infty;
			ArrayList<tuple> moves2 = moves();
			for (tuple q2 : moves2) {
				tuple reset2 = move(q2.x, q2.y, false);
				min = Math.min(min, eval());
				reverseMove(q2.x, q2.y, reset2);
			}
			if (min > bestPossible) {
				bestPossible = min;
				solution = q;
			}
			reverseMove(q.x, q.y, reset);
		}
		long t1 = System.nanoTime();
		//System.out.println((t1 - t0) / 1000000);
		return solution;
	}
	boolean useless(tuple p) {
		for (int i = Math.max(p.x - 2, 0); i <= Math.min(9, p.x + 2); i++) {
			for (int j = Math.max(p.y - 2, 0); j <= Math.min(9, p.y + 2); j++) {
				if (grid[i][j] > 2) {
					return false;
				}
			}
		}
		return true;
	}
	tuple bestMoveHard() {
		if (fm) {
			fm = false;
			return fm();
		}
		long t0 = System.nanoTime();
		tuple solution = null;
		ArrayList<tuple> moves = moves();
		int bestPossible = -infty;
		for (tuple q : moves) {
			if (useless(q))
				continue;
			tuple reset = move(q.x, q.y, true);
			if(winner()==1) {
				reverseMove(q.x, q.y, reset);
				return q;
			}
			int min = infty;
			ArrayList<tuple> moves2 = moves();
			for (tuple q2 : moves2) {
				if (useless(q2))
					continue;
				tuple reset2 = move(q2.x, q2.y, false);
				if(winner()==2) {
					reverseMove(q2.x, q2.y, reset2);
					min=-infty;
					break;
				}
				int max = -infty;
				ArrayList<tuple> moves3 = moves();
				for (tuple q3 : moves3) {
					if (useless(q3))
						continue;
					tuple reset3 = move(q3.x, q3.y, true);
					if(winner()==1) {
						reverseMove(q3.x, q3.y, reset3);
						max=infty;
						break;
					}
					int min2 = infty;
					ArrayList<tuple> moves4 = moves();
					for (tuple q4 : moves4) {
						if (useless(q4))
							continue;
						tuple reset4 = move(q4.x, q4.y, false);
						int x = eval();
						min2 = Math.min(min2, x);
						reverseMove(q4.x, q4.y, reset4);
					}
					max = Math.max(max, min2);
					reverseMove(q3.x, q3.y, reset3);
				}
				min = Math.min(min, max);
				reverseMove(q2.x, q2.y, reset2);
			}
			if (min > bestPossible) {
				bestPossible = min;
				solution = q;
			}
			reverseMove(q.x, q.y, reset);
		}
		long t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1000000 + " " + bestPossible);
		return solution;
	}
	int bm() {
		ArrayList<tuple> moves = moves();
		int bestPossible = -infty;
		for (tuple q : moves) {
			if (useless(q))
				continue;
			tuple reset = move(q.x, q.y, true);
			int min = infty;
			ArrayList<tuple> moves2 = moves();
			for (tuple q2 : moves2) {
				if (useless(q2))
					continue;
				tuple reset2 = move(q2.x, q2.y, false);
				int max = -infty;
				ArrayList<tuple> moves3 = moves();
				for (tuple q3 : moves3) {
					if (useless(q3))
						continue;
					tuple reset3 = move(q3.x, q3.y, true);
					int min2 = infty;
					ArrayList<tuple> moves4 = moves();
					for (tuple q4 : moves4) {
						if (useless(q4))
							continue;
						tuple reset4 = move(q4.x, q4.y, false);
						int x = eval();
						min2 = Math.min(min2, x);
						reverseMove(q4.x, q4.y, reset4);
					}
					max = Math.max(max, min2);
					reverseMove(q3.x, q3.y, reset3);
				}
				min = Math.min(min, max);
				reverseMove(q2.x, q2.y, reset2);
			}
			if (min > bestPossible) {
				bestPossible = min;
			}
			reverseMove(q.x, q.y, reset);
		}
		return bestPossible;
	}
	tuple bestMoveVeryHard() {
		if (fm) {
			fm = false;
			return fm();
		}
		long t0 = System.nanoTime();
		double[] aggr = { 0.2, 0.45, 0.55, 0.7 };
		tuple[]agg=new tuple[4];
		for (int i=0;i<4;i++) {
			attack=aggr[i];
			tuple solution = null;
			ArrayList<tuple> moves = moves();
			int bestPossible = -infty;
			for (tuple q : moves) {
				if (useless(q))
					continue;
				tuple reset = move(q.x, q.y, true);
				if(winner()==1) {
					reverseMove(q.x, q.y, reset);
					return q;
				}
				int min = infty;
				ArrayList<tuple> moves2 = moves();
				for (tuple q2 : moves2) {
					if (useless(q2))
						continue;
					tuple reset2 = move(q2.x, q2.y, false);
					if(winner()==2) {
						reverseMove(q2.x, q2.y, reset2);
						min=-infty;
						break;
					}
					int max = -infty;
					ArrayList<tuple> moves3 = moves();
					for (tuple q3 : moves3) {
						if (useless(q3))
							continue;
						tuple reset3 = move(q3.x, q3.y, true);
						if(winner()==1) {
							reverseMove(q3.x, q3.y, reset3);
							max=infty;
							break;
						}
						int min2 = infty;
						ArrayList<tuple> moves4 = moves();
						for (tuple q4 : moves4) {
							if (useless(q4))
								continue;
							tuple reset4 = move(q4.x, q4.y, false);
							int x = eval();
							min2 = Math.min(min2, x);
							reverseMove(q4.x, q4.y, reset4);
						}
						max = Math.max(max, min2);
						reverseMove(q3.x, q3.y, reset3);
					}
					min = Math.min(min, max);
					reverseMove(q2.x, q2.y, reset2);
				}
				if (min > bestPossible) {
					bestPossible = min;
					solution = q;
				}
				reverseMove(q.x, q.y, reset);
			}
			agg[i]=solution;
		}
		int max=infty;
		tuple best=null;
		attack=0.45;
		int p=-1;
		for(int i=0;i<4;i++) {
			tuple t=agg[i];
			tuple reset = move(t.x,t.y, true);
			invert();
			int score=bm();
			if(score<max) {
				max=score;
				best=t;
				p=i;
			}
			invert();
			reverseMove(t.x,t.y,reset);
		}
		System.out.println(p);
		long t1 = System.nanoTime();
		//System.out.println((t1 - t0) / 1000000);
		return best;
	}
	tuple fm() {
		tuple[]q= {new tuple(0,3),new tuple(0,4),new tuple(0,5),new tuple(0,6),new tuple(9,3),new tuple(9,4),new tuple(9,5),new tuple(9,6),
				new tuple(3,0),new tuple(4,0),new tuple(5,0),new tuple(6,0),new tuple(3,9),new tuple(4,9),new tuple(5,9),new tuple(6,9)};
		int qq=(int)Math.floor(q.length*Math.random());
		return q[qq];
	}
	int winner() {
		int c = 3;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 10; j++) {
				if (grid[i][j] == c && grid[i + 1][j] == c
						&& grid[i + 2][j] == c && grid[i + 3][j] == c) {
					return 1;
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i][j + 1] == c
						&& grid[i][j + 2] == c && grid[i][j + 3] == c) {
					return 1;
				}
			}
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i + 1][j + 1] == c
						&& grid[i + 2][j + 2] == c && grid[i + 3][j + 3] == c) {
					return 1;
				}
			}
		}
		for (int i = 3; i < 10; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i - 1][j + 1] == c
						&& grid[i - 2][j + 2] == c && grid[i - 3][j + 3] == c) {
					return 1;
				}
			}
		}
		c = 4;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 10; j++) {
				if (grid[i][j] == c && grid[i + 1][j] == c
						&& grid[i + 2][j] == c && grid[i + 3][j] == c) {
					return 2;
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i][j + 1] == c
						&& grid[i][j + 2] == c && grid[i][j + 3] == c) {
					return 2;
				}
			}
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i + 1][j + 1] == c
						&& grid[i + 2][j + 2] == c && grid[i + 3][j + 3] == c) {
					return 2;
				}
			}
		}
		for (int i = 3; i < 10; i++) {
			for (int j = 0; j < 7; j++) {
				if (grid[i][j] == c && grid[i - 1][j + 1] == c
						&& grid[i - 2][j + 2] == c && grid[i - 3][j + 3] == c) {
					return 2;
				}
			}
		}
		return -1;
	}
}



























