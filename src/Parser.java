import java.io.*;
public class Parser {
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	private int counter = 0;

	public Parser(Lexer l, BufferedReader br){
		lex = l;
		pbr = br;
		move();
	}

	void move(){
		look = lex.lexical_scan(pbr);
		System.out.println("token = " + look);
		counter++;
	}

	void error(String s){
		throw new Error("tagcounter:" + counter +" near line" + lex.line + ": " + s + " " +look);
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
				statlist();
				match(Tag.EOF, error);
				break;
			default: error(error);
		}
	}

	private void statlist(){
		String error = "statlist syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.CASE:
			case Tag.WHILE:
			case '{':
				stat();
				statlistp();
				break;
			default: error(error);
		}
	}

	private void statlistp(){
		String error = "statlistp syntax error with token:";
		switch(look.tag){
			case ';':
				move();
				stat();
				statlistp();
				break;

			case Tag.EOF:
			case '}':
				break;
			default: error(error);
		}
	}

	private void stat(){
		String error = 	"stat syntax error with token:";
		switch(look.tag){
			case Tag.ID:
				move();
				match(Tag.ASSIGN, error);
				expr();
				break;
			case Tag.PRINT:
				move();
				match('(', error);
				expr();
				match(')', error);
				break;
			case Tag.READ:
				move();
				match('(', error);
				match(Tag.ID, error);
				match(')', error);
				break;
			case Tag.CASE:
				move();
				whenlist();
				match(Tag.ELSE, error);
				stat();
				break;
			case Tag.WHILE:
				move();
				match('(', error);
				bexpr();
				match(')', error);
				stat();
				break;
			case '{':
				move();
				statlist();
				match('}', error);
				break;
			default: error(error);
		}
	}

	private void whenlist(){
		String error = 	"whenlist syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				whenitem();
				whenlistp();
				break;
			default: error(error);
		}
	}


	private void whenlistp(){
		String error = 	"whenlistp syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				whenitem();
				whenlistp();
				break;
			case Tag.ELSE:
				break;
			default:error(error);
		}
	}

	private void whenitem(){
		String error = 	"whenitem syntax error with token:";
		switch(look.tag){
			case Tag.WHEN:
				move();
				match('(', error);
				bexpr();
				match(')', error);
				stat();
				break;
			default: error(error);
		}
	}

	private void bexpr(){
		String error = "bexpr syntax error with token:";
		switch(look.tag){
			case Tag.ID:
			case Tag.NUM:
			case '(':
				expr();
				match(Tag.RELOP, error);
				expr();
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
				exprp();
				break;
			case '-':
				move();
				term();
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
				break;
			case '/':
				move();
				fact();
				termp();
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
				move();
				break;
			case Tag.ID:
				move();
				break;
			default: error(error);
		}
	}
}









