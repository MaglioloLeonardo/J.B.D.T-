import java.io.*;

public class Translator {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;

	SymbolTable st = new SymbolTable();
	CodeGenerator code;
	int counter=0;

	public Translator(Lexer l, BufferedReader br, String path_out) {
		lex = l;
		pbr = br;
		move();
		code = new CodeGenerator(path_out);
	}

	void error(String s){
		throw new Error("Near line" + lex.line + ": " + s + " " +look);
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.out.println("token = " + look);
	}

	void match(int t, String message){
		if(look.tag == t){
			if(look.tag != Tag.EOF){
				move();
			}
		}else error(message);
	}

	public void prog(){
		String error = "prog syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.CASE:
			case Tag.WHILE:
			case '{':
			case Tag.EOF:
				int lnext_prog = code.newLabel();
				statlist(lnext_prog);
				code.emitLabel(lnext_prog);
				match(Tag.EOF, error);
				try{
					code.toJasmin();
				}catch (java.io.IOException e){
					System.out.println("Prog: IOExeption");
				}
				break;
			default: error(error);
		}
	}

	private void statlist(int lnext){
		String error = "statlist syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.CASE:
			case Tag.WHILE:
			case '{':
				int new_lab = code.newLabel();
				stat(new_lab);
				code.emitLabel(new_lab);
				statlistp(lnext);
				break;
			default: error(error);
		}
	}

	private void statlistp(int lnext){
		String error = "statlistp syntax error with token:";
		switch(look.tag){
			case ';':
				move();
				int new_lab = code.newLabel();
				stat(new_lab);
				code.emitLabel(new_lab);
				statlistp(lnext);
				break;
			case Tag.EOF:
			case '}':
				break;
			default: error(error);
		}
	}

	private void stat(int lnext){
		String error = 	"stat syntax error with token:";
		switch(look.tag){
			case Tag.ID:
				Word temp = (Word) look;
				move();
				match(Tag.ASSIGN, error);
				expr();
				int v = st.lookupAddress(temp.lexeme);
				if (v == -1) {
					v = counter;
					st.insert(temp.lexeme, counter++);
				}
				code.emit(OpCode.istore, v);
				break;
			case Tag.PRINT:
				move();
				match('(', error);
				expr();
				code.emit(OpCode.invokestatic, 1);
				match(')', error);
				break;
			case Tag.READ:
				move();
				match('(', error);
				if (look.tag == Tag.ID) {
					int read_id_addr = st.lookupAddress(((Word) look).lexeme);
					if (read_id_addr == -1) {
						read_id_addr = counter;
						st.insert(((Word) look).lexeme, counter++);
					}
					match(Tag.ID, error);
					match(')', error);
					code.emit(OpCode.invokestatic, 0);
					code.emit(OpCode.istore, read_id_addr);
				}else
					error("Error stat: after read '(' with look: " + look);
				break;
			case Tag.CASE:
				int lab_else = code.newLabel();
				move();
				whenlist(lab_else);
				match(Tag.ELSE, error);
				code.emit(OpCode.GOto, lnext);
				code.emitLabel(lab_else);
				stat(lnext);
				break;
			case Tag.WHILE:
				int ltrue = code.newLabel();
				int lfalse = lnext;
				int mid = code.newLabel();
				code.emitLabel(ltrue);
				move();
				match('(', error);
				bexpr(lfalse);
				match(')', error);
				code.emitLabel(mid);
				stat(mid);
				code.emit(OpCode.GOto, ltrue);
				break;
			case '{':
				move();
				statlist(lnext);
				match('}', error);
				break;
			default: error(error);
		}
	}

	private void whenlist(int lnext){
		String error = 	"whenlist syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				whenitem(lnext);
				whenlistp(lnext);
				break;
			default: error(error);
		}
	}


	private void whenlistp(int lnext){
		String error = 	"whenlistp syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				int new_lab = code.newLabel();
				whenitem(new_lab);
				code.emit(OpCode.GOto, new_lab);
				whenlistp(lnext);
				code.emitLabel(new_lab);
				break;
			case Tag.ELSE:
			case Tag.EOF:
				break;
			default:error(error);
		}
	}

	private void whenitem(int lnext){
		String error = 	"whenitem syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				int ltrue = code.newLabel();
				int lfalse = lnext;
				move();
				match('(', error);
				bexpr(lfalse);
				match(')', error);
				code.emitLabel(ltrue);
				stat(ltrue);
				break;
			default: error(error);
		}
	}

	private void bexpr(int ltrue){
		String error = "bexpr syntax error with token:";
		switch(look.tag) {
			case Tag.ID:
			case Tag.NUM:
			case '(':
				expr();
				Token temp = look;
				match(Tag.RELOP, error);
				expr();
				if (temp == Word.le) {
					code.emit(OpCode.if_icmpgt, ltrue);
				} else if (temp == Word.ge) {
					code.emit(OpCode.if_icmplt, ltrue);
				} else if (temp == Word.gt) {
					code.emit(OpCode.if_icmple, ltrue);
				} else if (temp == Word.eq){
					code.emit(OpCode.if_icmpne, ltrue);
				} else if (temp == Word.lt) {
					code.emit(OpCode.if_icmpge, ltrue);
				}else if(temp == Word.ne){
					code.emit(OpCode.if_icmpeq,ltrue);
				}
				break;
			default: error(error);
		}
	}

	private void expr(){
		String error = 	"term syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.NUM:
			case '(':
				term();
				exprp();
				break;
			default:error(error);
		}
	}

	private void exprp(){
		String error = "exprp syntax error with token:";
		switch(look.tag){
			case '+':
				move();
				term();
				code.emit(OpCode.iadd);
				exprp();
				break;
			case '-':
				move();
				term();
				code.emit(OpCode.isub);
				exprp();
				break;
			case Tag.WHEN:
			case Tag.ELSE:
			case Tag.RELOP:
			case Tag.EOF:
			case ';':
			case '}':
			case ')':
				break;
			default: error(error);
		}
	}

	private void term(){
		String error = 	"term syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.NUM:
			case '(':
				fact();
				termp();
				break;
			default: error(error);
		}
	}

	private void termp(){
		String error = "termp syntax error with token:";
		switch(look.tag){
			case '*':
				move();
				fact();
				termp();
				code.emit(OpCode.imul);
				break;
			case '/':
				move();
				fact();
				termp();
				code.emit(OpCode.idiv);
				break;
			case Tag.WHEN:
			case Tag.ELSE:
			case Tag.EOF:
			case Tag.RELOP:
			case ';':
			case '}':
			case ')':
			case '+':
			case '-':
				break;
			default: error(error);
		}
	}

	private void fact(){
		String error = "fact syntax error with token:";
		switch(look.tag){
			case '(':
				move();
				expr();
				match(')', error);
				break;
			case Tag.NUM:
				code.emit(OpCode.ldc, Integer.parseInt(((Word)look).lexeme));
				move();
				break;
			case Tag.ID:
				int v = st.lookupAddress(((Word) look).lexeme);
				if (v == -1) {
					error("(Fact-Tag.ID)Variable " + look + " not found");
				}
				move();
				code.emit(OpCode.iload,v);
				break;
			default: error(error);
		}
	}
}
