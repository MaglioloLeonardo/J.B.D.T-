import java.io.*;
public class main {

        public static void main(String[] args) {
            Lexer lex = new Lexer();
            String path_input = "in_out/input.txt";
            String path_output = "in_out/out.j";
            try {
                BufferedReader br = new BufferedReader(new FileReader(path_input));
                Translator translator = new Translator(lex, br, path_output);
                translator.prog();
                System.out.println("Input OK");
                br.close();
            } catch (IOException e) {e.printStackTrace();}
        }
}


