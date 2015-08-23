/* @org.eclipse.vjet.dsf.resource.utils.CodeGen("VjoGenerator") */
vjo.ctype('backtrack.EightQueen') //< public
.needs('org.eclipse.vjet.vjo.java.lang.System')
.props({
    BOARD_SIZE:8, //< public final int
    EMPTY:false, //< public final boolean
    QUEEN:true, //< public final boolean
    MOVES:4, //< public final int
    //> public void main(String[] s)
    main:function(s){
        var main=new this(); //<EightQueen
        main.placeQueens(0);
        main.displayBoard();
    }
})
.protos({
    board:null, //< boolean[][]
    horizontal:null, //< int[]
    vertical:null, //< int[]
    queens:0, //< public int
    //> public constructs()
    constructs:function(){
        this.board=vjo.createArray(false, this.vj$.EightQueen.BOARD_SIZE, this.vj$.EightQueen.BOARD_SIZE);
        for (var row=0;row<this.board.length;row++){
            for (var col=0;col<this.board[row].length;col++){
                this.board[row][col]=this.vj$.EightQueen.EMPTY;
            }
        }
        this.horizontal=vjo.createArray(0, this.vj$.EightQueen.MOVES);
        this.vertical=vjo.createArray(0, this.vj$.EightQueen.MOVES);
        this.horizontal[0]=-1;
        this.vertical[0]=1;
        this.horizontal[1]=1;
        this.vertical[1]=-1;
        this.horizontal[2]=-1;
        this.vertical[2]=-1;
        this.horizontal[3]=1;
        this.vertical[3]=1;
    },
    //> public boolean placeQueens()
    //> private boolean placeQueens(int column)
    placeQueens:function(){
        if(arguments.length===0){
            return this.placeQueens_0_0_EightQueen_ovld();
        }else if(arguments.length===1){
            return this.placeQueens_1_0_EightQueen_ovld(arguments[0]);
        }
    },
    //> protected boolean placeQueens_0_0_EightQueen_ovld()
    placeQueens_0_0_EightQueen_ovld:function(){
        return this.placeQueens(0);
    },
    //> private boolean placeQueens_1_0_EightQueen_ovld(int column)
    placeQueens_1_0_EightQueen_ovld:function(column){
        if(column>=this.vj$.EightQueen.BOARD_SIZE){
            return true;
        }else {
            var queenPlaced=false; //<boolean
            var row=0; //<int
            while(!queenPlaced && row<this.vj$.EightQueen.BOARD_SIZE){
                if(this.isUnderAttack(row,column)){
                    ++row;
                }else {
                    this.setQueen(row,column);
                    queenPlaced=this.placeQueens(column+1);
                    if(!queenPlaced){
                        this.removeQueen(row,column);
                        ++row;
                    }
                }
            }
            return queenPlaced;
        }
    },
    //> private void removeQueen(int row,int column)
    removeQueen:function(row,column){
        this.board[row][column]=this.vj$.EightQueen.EMPTY;
        this.vj$.System.out.printf("queen REMOVED from [%d][%d]\n",row,column);
        --this.queens;
    },
    //> private void setQueen(int row,int column)
    setQueen:function(row,column){
        this.board[row][column]=this.vj$.EightQueen.QUEEN;
        this.vj$.System.out.printf("queen PLACED in [%d][%d]\n",row,column);
        ++this.queens;
    },
    //> public boolean isUnderAttack(int row,int col)
    isUnderAttack:function(row,col){
        var condition=false; //<boolean
        for (var column=0;column<this.vj$.EightQueen.BOARD_SIZE;column++){
            if((this.board[row][column]===true)){
                condition=true;
            }
        }
        for (var row_=0;row_<this.board.length;row_++){
            if(this.board[row_][col]===true){
                condition=true;
            }
        }
        for (var row_=row,col_=col;row_>=0 && col_<8;row_+=this.horizontal[0],col_+=this.vertical[0]){
            if(this.board[row_][col_]===true){
                condition=true;
            }
        }
        for (var row_=row,col_=col;row_<8 && col_>=0;row_+=this.horizontal[1],col_+=this.vertical[1]){
            if(this.board[row_][col_]===true){
                condition=true;
            }
        }
        for (var row_=row,col_=col;row_>=0 && col_>=0;row_+=this.horizontal[2],col_+=this.vertical[2]){
            if(this.board[row_][col_]===true){
                condition=true;
            }
        }
        for (var row_=row,col_=col;row_<8 && col_<8;row_+=this.horizontal[3],col_+=this.vertical[3]){
            if(this.board[row_][col_]===true){
                condition=true;
            }
        }
        return condition;
    },
    //> public void displayBoard()
    displayBoard:function(){
        var counter=0; //<int
        for (var row=0;row<this.board.length;row++){
            for (var col=0;col<this.board[row].length;col++){
                if(this.board[row][col]===true){
                    this.vj$.System.out.printf("|%s|","x");
                    counter++;
                }else {
                    this.vj$.System.out.printf("|%s|","o");
                }
            }
            this.vj$.System.out.println();
        }
        this.vj$.System.out.printf("%d queens has been placed\n",counter);
    }
})
.endType();