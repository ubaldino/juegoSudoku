
package escucha;

import java.io.FileReader;
import java.util.Locale;
import javax.speech.Central;
import javax.speech.EngineModeDesc;
import javax.speech.recognition.*;

public class Escucha extends ResultAdapter {
  //  77449885
  // jose25.flash@gmail.com
  static Recognizer recognizer;
  String gst;
  private String resp;
  private Sudoku s;
  public Escucha (){
    s = new Sudoku();
    s.nuevoSudoku();
  }
  
  @Override
  public void resultAccepted(ResultEvent re) {
    try {
      Result res = (Result) (re.getSource());
      ResultToken tokens[] = res.getBestTokens();

      String args[] = new String[1];
      args[0] = "";
      
      for (int i = 0; i < tokens.length; i++) {
        gst = tokens[i].getSpokenText();
        args[0] += gst + " ";
        System.out.print(gst + "");
        resp = gst+"";
        s.getCasilla().setText(parser(gst+""));
      }
      System.out.println();
      
      //cerramos el sistema
      if (gst.equals("salir")) {
        recognizer.deallocate();
        args[0] = "Hasta la proxima!";
        System.out.println(args[0]);
        System.exit(0);
      } else {
        recognizer.suspend();
        recognizer.resume();
      }
    } catch (Exception ex) {
      System.out.println("Ha ocurrido algo inesperado " + ex);
    }
  }
  
  public String parser(String cadena){
    String aux = "";
    switch(cadena){
      case "cero":aux="0";break;
      case "uno":aux="1";break;
      case "dos":aux="2";break;
      case "tres":aux="3";break;
      case "cuatro":aux="4";break;
      case "cinco":aux="5";break;
      case "seis":aux="6";break;
      case "siete":aux="7";break;
      case "ocho":aux="8";break;
      case "nueve":aux="9";break;
    }
    return aux;
  }
  

  public static void main(String args[]) {
    try {
      recognizer = Central.createRecognizer(new EngineModeDesc(Locale.ROOT));
      recognizer.allocate();
      String dir = System.getProperty("user.dir")+"\\src\\escucha";
      FileReader grammar1 = new FileReader(dir + "\\SimpleGrammarES2.txt");

      RuleGrammar rg = recognizer.loadJSGF(grammar1);
      rg.setEnabled(true);

      recognizer.addResultListener(new Escucha());

      System.out.println("Empieze Dictado");
      
      recognizer.commitChanges();

      recognizer.requestFocus();
      recognizer.resume();
    } catch (Exception e) {
      System.out.println("Exception en " + e.toString());
      e.printStackTrace();
      System.exit(0);
    }
  }
}