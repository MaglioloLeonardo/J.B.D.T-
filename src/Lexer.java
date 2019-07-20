import java.io.*;
import java.util.*;

public class Lexer {


    public static int line = 1;
    private char peek = ' ';
    private char prevpeek = ' ';

    private void readch(BufferedReader br) {
        try {
            prevpeek = peek;
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }

            switch (peek) {
                // ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //

                case '!':
                    peek = ' ';
                    return Token.not;

                case '(':
                    peek = ' ';
                    return Token.lpt;

                case ')':
                    peek = ' ';
                    return Token.rpt;

                case '{':
                    peek = ' ';
                    return Token.lpg;


                case '}':
                    peek = ' ';
                    return Token.rpg;

                case '+':
                    peek = ' ';
                    return Token.plus;

                case '-':
                    peek = ' ';
                    return Token.minus;

                case '*':
                    peek = ' ';
                    return Token.mult;

                case '/':
                    readch(br);
                    if(peek == '*'){
                        while(!(prevpeek == '*' && peek == '/')){
                            if(peek == (char) -1)return new Token(Tag.EOF);
                            readch(br);
                        }
                        readch(br);
                        return lexical_scan(br);
                    }else if(peek == '/'){
                        while(peek != '\n' && peek != '\r' && peek != 13){
                            if(peek == (char) -1){
                                System.out.println("-->LEXER ALLERT: \"/*\" was never closed by: \"*/\"");
                                return new Token(Tag.EOF);
                            }
                            readch(br);
                        }
                        return lexical_scan(br);
                    }else{
                        return Token.div;
                    }


                case ';':
                    peek = ' ';
                    return Token.semicolon;

                case '&':
                    readch(br);
                    if (peek == '&') {
                        peek = ' ';
                        return Word.and;
                    } else {
                        errGen();
                        return null;
                    }

                    // ... gestire i casi di ||, <, >, <=, >=, ==, <>, := ... //

                case '|':
                    readch(br);
                    if (peek == '|') {
                        peek = ' ';
                        return Word.or;
                    } else {
                        errGen();
                        return null;
                    }

                case '<':
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.le;
                    } else if (peek == '>') {
                        peek = ' ';
                        return Word.ne;
                    } else {
                        return Word.lt;
                    }

                case '>':
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.ge;
                    } else {
                        return Word.gt;
                    }

                case '=':
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.eq;
                    } else {
                        errGen();
                        return null;
                    }

                case ':':
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.assign;
                    } else {
                        errGen();
                        return null;
                    }

                case '_':
                    readch(br);
                    if(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){
                        return isIDent("_", br);
                    }else{
                        errGen();
                        return null;
                    }


                    //--------------------------------------------------------------------------------
                case (char) -1:
                    return new Token(Tag.EOF);

                default:
                    if (Character.isLetter(peek)) {
                        switch (peek) {
                            case 'c':
                                readch(br);
                                if (peek != 'a') {
                                    return isIDent("c",br);
                                }
                                readch(br);
                                if (peek != 's') {
                                    return isIDent("ca",br);
                                }
                                readch(br);
                                if (peek != 'e') {
                                    return isIDent("cas",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("case",br);
                                }
                                return Word.casetok;

                            case 't':
                                readch(br);
                                if (peek != 'h') {
                                    return isIDent("t",br);
                                }
                                readch(br);
                                if (peek != 'e') {
                                    return isIDent("th",br);
                                }
                                readch(br);
                                if (peek != 'n') {
                                    return isIDent("the",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("then",br);
                                }
                                return Word.then;

                            case 'e':
                                readch(br);
                                if (peek != 'l') {
                                    return isIDent("e",br);
                                }
                                readch(br);
                                if (peek != 's') {
                                    return isIDent("el",br);
                                }
                                readch(br);
                                if (peek != 'e') {
                                    return isIDent("els",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("else",br);
                                }
                                return Word.elsetok;

                            case 'd':
                                readch(br);
                                if (peek != 'o') {
                                    return isIDent("d",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("do",br);
                                }
                                return Word.dotok;

                            case 'p':
                                readch(br);
                                if (peek != 'r') {
                                    return isIDent("p",br);
                                }
                                readch(br);
                                if (peek != 'i') {
                                    return isIDent("pr",br);
                                }
                                readch(br);
                                if (peek != 'n') {
                                    return isIDent("pri",br);
                                }
                                readch(br);
                                if (peek != 't') {
                                    return isIDent("prin",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("print",br);
                                }
                                return Word.print;

                            case 'r':
                                readch(br);
                                if (peek != 'e') {
                                    return isIDent("r",br);
                                }
                                readch(br);
                                if (peek != 'a') {
                                    return isIDent("re",br);
                                }
                                readch(br);
                                if (peek != 'd') {
                                    return isIDent("rea",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("read",br);
                                }
                                return Word.read;

                            case 'w':
                                readch(br);
                                if (peek != 'h') {
                                    return isIDent("w",br);
                                }
                                readch(br);
                                if (peek != 'e') {
                                    //controllo while
                                    if (peek != 'i') {
                                        return isIDent("wh",br);
                                    }
                                    readch(br);
                                    if (peek != 'l') {
                                        return isIDent("whi",br);
                                    }
                                    readch(br);
                                    if (peek != 'e') {
                                        return isIDent("whil",br);
                                    }
                                    readch(br);
                                    if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                        return isIDent("while",br);
                                    }
                                    return Word.whiletok;
                                }
                                readch(br);
                                if (peek != 'n') {
                                    return isIDent("whe",br);
                                }
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                                    return isIDent("when",br);
                                }
                                return Word.when;

                            default:
                                return isIDent(Character.toString((char) 0),br);
                        }
                    } else if (Character.isDigit(peek)) {
                        return isNumber(br);
                    } else {
                        errGen();
                        return null;
                    }
            }



    }

//___________________________________________________________________________________________________

    //___________________________________________________________________________________________________
    private Word isIDent(String preLitteral, BufferedReader br){
        String thisLitteral = "";
        char[] arrayPreLitteral = preLitteral.toCharArray();
        boolean preLitteralControl = false;

        while(peek == '_'){
            thisLitteral = thisLitteral + '_';
            readch(br);
        }

        for(int i=0; i<arrayPreLitteral.length; i++){
            if(Character.isLetter(arrayPreLitteral[i]))preLitteralControl = true;
        }

        if(!preLitteralControl){
            if(!Character.isLetter(peek)  && !Character.isDigit(peek)){
                errGen();
                return null;
            }
        }


        while(peek != ' ' && peek != '\t' && peek != '\n' && peek != '\r'&& peek != (char)65535){
            if(!Character.isLetter(peek)  && !Character.isDigit(peek) && peek != '_'){
                break;
            }
            thisLitteral = thisLitteral + peek;
            readch(br);
        }

        return new Word(Tag.ID,preLitteral+thisLitteral );
    }

    private Word isNumber(BufferedReader br){
        String thisLitteral = "";

        if(peek == '0'){
            readch(br);
            if(Character.isLetterOrDigit(peek)){
                errGen();
            }
            return new Word(Tag.NUM, "0");
        }

        while(peek != ' ' && peek != '\t' && peek != '\n' && peek != '\r'&& peek != (char)65535){
            if(!Character.isDigit(peek)){
                if(Character.isLetter(peek)){
                    errGen();
                }
                break;
            }
            thisLitteral = thisLitteral + peek;
            readch(br);
        }

        return new Word(Tag.NUM,thisLitteral );
    }

    private void errGen(){
        throw new Error("Erroneous character" + " after '"+ prevpeek  +"' :" + peek);
    }
//__________________________________________________________________________________________________
}