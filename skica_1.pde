void setup() {
    fullScreen();
    background(0, 0, 0);
    d=new data();
    map=new colour[5];
    map[0]=new colour(255, 255, 255);
    map[1]=new colour(128, 128, 128);
    map[2]=new colour(128, 0, 128);
    map[3]=new colour(255, 0, 0);
    map[4]=new colour(0, 0, 255);
}
data d;
colour[]map;
int dx=1440;
int dy=900;
void draw() {
    for (int i=0; i<10; i++) {
        for (int j=0; j<10; j++) {
            colour c=map[d.grid[i][j]];
            fill(c.r, c.g, c.b);
            rect(i*dx/10, j*dy/10, dx/10, dy/10);
        }
    }
    if(d.winner()!=-1){
         println("Zmagal je igrales" +d.winner());
         exit();
    }
}
void mousePressed() {
    int x=mouseX;
    int y=mouseY;
    int i=10*x/dx;
    int j=10*y/dy;
    d.attemptPlacement(i, j);
}
class colour {
    int r;
    int g;
    int b;
    colour(int a, int x, int c) {
        r=a;
        g=x;
        b=c;
    }
}
class data {
    int[][]grid={
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
        {0, 1, 0, 0, 0, 0, 0, 0, 1, 0}, 
        {0, 0, 1, 0, 0, 0, 0, 1, 0, 0}, 
        {0, 0, 0, 1, 0, 0, 1, 0, 0, 0}, 
        {0, 0, 0, 0, 2, 2, 0, 0, 0, 0}, 
        {0, 0, 0, 0, 2, 2, 0, 0, 0, 0}, 
        {0, 0, 0, 1, 0, 0, 1, 0, 0, 0}, 
        {0, 0, 1, 0, 0, 0, 0, 1, 0, 0}, 
        {0, 1, 0, 0, 0, 0, 0, 0, 1, 0}, 
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    };
    boolean redToMove = true;
    void attemptPlacement(int i, int j) {
        if (i==j||i==9-j) {
            place(i, j);
            return;
        }
        if (i>j) {
            if (i<9-j) {
                for (int q=0; q<=j; q++) {
                    if (place(i, q)) {
                        break;
                    }
                }
            } else {
                for (int q=9; q>=i; q--) {
                    if (place(q, j)) {
                        break;
                    }
                }
            }
        } else {
            if (i<9-j) {
                for (int q=0; q<=j; q++) {
                    if (place(q, j)) {
                        break;
                    }
                }
            } else {
                for (int q=9; q>=i; q--) {
                    if (place(i, q)) {
                        break;
                    }
                }
            }
        }
    }
    boolean place(int i, int j) {
        if (grid[i][j] >= 2) {
            return false;
        }
        grid[i][j] = redToMove ? 3 : 4;
        redToMove = !redToMove;
        return true;
    }
    int winner() {
        int c=3;
        for (int i=0; i<5; i++) {
            for (int j=0; j<9; j++) {
                if (grid[i][j]==c&&grid[i+1][j]==c&&grid[i+2][j]==c&&grid[i+3][j]==c) {
                    return 1;
                }
            }
        }
        for (int i=0; i<9; i++) {
            for (int j=0; j<5; j++) {
                if (grid[i][j]==c&&grid[i][j+1]==c&&grid[i][j+2]==c&&grid[i][j+3]==c) {
                    return 1;
                }
            }
        }
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                if (grid[i][j]==c&&grid[i+1][j+1]==c&&grid[i+2][j+2]==c&&grid[i+3][j+3]==c) {
                    return 1;
                }
            }
        }
        for (int i=4; i<9; i++) {
            for (int j=4; j<9; j++) {
                if (grid[i][j]==c&&grid[i-1][j-1]==c&&grid[i-2][j-2]==c&&grid[i-3][j-3]==c) {
                    return 1;
                }
            }
        }
        c=4;
        for (int i=0; i<5; i++) {
            for (int j=0; j<9; j++) {
                if (grid[i][j]==c&&grid[i+1][j]==c&&grid[i+2][j]==c&&grid[i+3][j]==c) {
                    return 2;
                }
            }
        }
        for (int i=0; i<9; i++) {
            for (int j=0; j<5; j++) {
                if (grid[i][j]==c&&grid[i][j+1]==c&&grid[i][j+2]==c&&grid[i][j+3]==c) {
                    return 2;
                }
            }
        }
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                if (grid[i][j]==c&&grid[i+1][j+1]==c&&grid[i+2][j+2]==c&&grid[i+3][j+3]==c) {
                    return 2;
                }
            }
        }
        for (int i=4; i<9; i++) {
            for (int j=4; j<9; j++) {
                if (grid[i][j]==c&&grid[i-1][j-1]==c&&grid[i-2][j-2]==c&&grid[i-3][j-3]==c) {
                    return 2;
                }
            }
        }
        return -1;
    }
}