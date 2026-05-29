/*
 * COMENTARIO DE MANTENIMIENTO:
 * Este archivo es generado por JFlex a partir de Lexer.flex.
 * No conviene modificar su lógica manualmente porque se perdería al regenerar.
 * La función general de esta clase es leer caracteres del código fuente y
 * devolver tokens mediante proximoToken().
 */
/** Chimaltenango 30 de mayo 2026
Proyecto Final Compiladores
Integrantes: 
1990-23-4406	Christopher Obryan Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
*/

package com.company.api_compilador.lexico;



@SuppressWarnings("fallthrough")
public class AnalizadorLexico {

  /** This character denotes the end of file. */
  public static final int YYEOF = -1;

  /** Initial size of the lookahead buffer. */
  private static final int ZZ_BUFFERSIZE = 16384;

  // Lexical states.
  public static final int YYINITIAL = 0;
  public static final int COMENTARIO_BLOQUE = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1, 1
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\37\u0100\1\u0200\267\u0100\10\u0300\u1020\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\1\3\1\4\1\5\22\0\1\1"+
    "\1\6\1\7\2\0\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\11\24"+
    "\1\25\1\26\1\27\1\30\1\31\2\0\1\32\1\33"+
    "\1\34\1\35\1\36\1\37\1\40\1\41\1\42\2\33"+
    "\1\43\1\44\1\45\1\46\1\47\1\33\1\50\1\51"+
    "\1\52\1\53\1\54\1\33\1\55\1\56\1\33\1\0"+
    "\1\57\1\0\1\60\1\33\1\0\1\61\1\33\1\62"+
    "\1\63\1\64\1\65\1\66\1\67\1\70\2\33\1\71"+
    "\1\72\1\73\1\74\1\75\1\33\1\76\1\77\1\100"+
    "\1\101\1\102\1\33\1\103\1\56\1\33\1\104\1\105"+
    "\1\106\7\0\1\3\73\0\1\107\21\0\1\110\15\0"+
    "\1\111\21\0\1\112\u0134\0\2\3\326\0\u0100\3";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[1024];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\3\2\1\3\1\1\1\4\2\1\1\5"+
    "\1\6\1\7\1\10\1\11\1\12\1\13\2\14\1\15"+
    "\1\16\1\17\1\20\1\21\13\22\1\23\5\22\1\24"+
    "\1\25\12\22\1\23\5\22\1\26\1\1\1\27\2\30"+
    "\1\31\1\0\1\32\1\0\1\24\2\0\1\33\1\34"+
    "\1\35\1\36\1\37\1\40\30\22\2\0\2\22\1\3"+
    "\10\22\2\41\4\22\1\23\1\42\1\43\1\44\15\22"+
    "\1\45\15\22\2\0\22\22\1\0\1\46\2\22\1\0"+
    "\22\22\1\47\2\22\2\0\2\22\2\50\4\22\1\51"+
    "\1\52\6\22\1\0\2\22\1\0\6\22\1\53\2\22"+
    "\1\54\6\22\2\0\2\22\1\50\1\55\2\22\1\56"+
    "\3\22\1\0\1\22\1\0\1\57\7\22\1\60\1\22"+
    "\2\61\7\22\1\0\1\22\1\0\1\22\1\62\13\22"+
    "\2\63\1\64\1\65\1\66\1\67\1\70\2\22\1\71";

  private static int [] zzUnpackAction() {
    int [] result = new int[296];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\113\0\226\0\341\0\226\0\u012c\0\u0177\0\u01c2"+
    "\0\226\0\u020d\0\u0258\0\226\0\226\0\u02a3\0\226\0\226"+
    "\0\226\0\u02ee\0\u0339\0\u0384\0\226\0\226\0\u03cf\0\u041a"+
    "\0\u0465\0\u04b0\0\u04fb\0\u0546\0\u0591\0\u05dc\0\u0627\0\u0672"+
    "\0\u06bd\0\u0708\0\u0753\0\u079e\0\u07e9\0\u0834\0\u087f\0\u08ca"+
    "\0\u0915\0\u0960\0\u04b0\0\226\0\u09ab\0\u09f6\0\u0a41\0\u0a8c"+
    "\0\u0ad7\0\u0b22\0\u0b6d\0\u0bb8\0\u0c03\0\u0c4e\0\u0c99\0\u0ce4"+
    "\0\u0d2f\0\u0d7a\0\u0dc5\0\u0e10\0\226\0\u0e5b\0\226\0\226"+
    "\0\u0ea6\0\226\0\u01c2\0\226\0\u0ef1\0\226\0\u0f3c\0\u0f87"+
    "\0\226\0\u0fd2\0\u101d\0\226\0\226\0\226\0\u1068\0\u10b3"+
    "\0\u10fe\0\u1149\0\u1194\0\u11df\0\u122a\0\u1275\0\u12c0\0\u130b"+
    "\0\u1356\0\u13a1\0\u13ec\0\u1437\0\u1482\0\u14cd\0\u1518\0\u1563"+
    "\0\u15ae\0\u15f9\0\u1644\0\u168f\0\u16da\0\u1725\0\u1770\0\u17bb"+
    "\0\u1806\0\u1851\0\u04b0\0\u189c\0\u18e7\0\u1932\0\u197d\0\u19c8"+
    "\0\u1a13\0\u1a5e\0\u1aa9\0\u1af4\0\u1b3f\0\u1b8a\0\u1bd5\0\u1c20"+
    "\0\u1c6b\0\226\0\226\0\226\0\u101d\0\u1cb6\0\u1d01\0\u1d4c"+
    "\0\u1d97\0\u1de2\0\u1e2d\0\u1e78\0\u1ec3\0\u1f0e\0\u1f59\0\u1fa4"+
    "\0\u1fef\0\u203a\0\u04b0\0\u2085\0\u20d0\0\u211b\0\u2166\0\u21b1"+
    "\0\u21fc\0\u2247\0\u2292\0\u22dd\0\u2328\0\u2373\0\u23be\0\u2409"+
    "\0\u2454\0\u249f\0\u24ea\0\u2535\0\u2580\0\u25cb\0\u2616\0\u2661"+
    "\0\u26ac\0\u26f7\0\u2742\0\u278d\0\u27d8\0\u2823\0\u286e\0\u28b9"+
    "\0\u2904\0\u294f\0\u299a\0\u29e5\0\u2a30\0\u04b0\0\u2a7b\0\u2ac6"+
    "\0\u2b11\0\u2b5c\0\u2ba7\0\u2bf2\0\u2c3d\0\u2c88\0\u2cd3\0\u2d1e"+
    "\0\u2d69\0\u2db4\0\u2dff\0\u2e4a\0\u2e95\0\u2ee0\0\u2f2b\0\u2f76"+
    "\0\u2fc1\0\u300c\0\u3057\0\u04b0\0\u30a2\0\u30ed\0\u3138\0\u3183"+
    "\0\u31ce\0\u3219\0\u3264\0\u32af\0\u32fa\0\u3345\0\u3390\0\u33db"+
    "\0\u04b0\0\u04b0\0\u3426\0\u3471\0\u34bc\0\u3507\0\u3552\0\u359d"+
    "\0\u35e8\0\u3633\0\u367e\0\u36c9\0\u3714\0\u375f\0\u37aa\0\u37f5"+
    "\0\u3840\0\u388b\0\u04b0\0\u38d6\0\u3921\0\u04b0\0\u396c\0\u39b7"+
    "\0\u3a02\0\u3a4d\0\u3a98\0\u3ae3\0\u3b2e\0\u3b79\0\u3bc4\0\u3c0f"+
    "\0\u04b0\0\u04b0\0\u3c5a\0\u3ca5\0\u04b0\0\u3cf0\0\u3d3b\0\u3d86"+
    "\0\u3dd1\0\u3e1c\0\u3e67\0\u04b0\0\u3eb2\0\u3efd\0\u3f48\0\u3f93"+
    "\0\u3fde\0\u4029\0\u4074\0\u04b0\0\u40bf\0\u04b0\0\226\0\u410a"+
    "\0\u4155\0\u41a0\0\u41eb\0\u4236\0\u4281\0\u42cc\0\u4317\0\u4362"+
    "\0\u43ad\0\u43f8\0\u04b0\0\u4443\0\u448e\0\u44d9\0\u4524\0\u456f"+
    "\0\u45ba\0\u4605\0\u4650\0\u469b\0\u46e6\0\u4731\0\u04b0\0\226"+
    "\0\u04b0\0\u04b0\0\u04b0\0\u04b0\0\u04b0\0\u477c\0\u47c7\0\u04b0";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[296];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\0\1\4\1\6\1\7\1\10"+
    "\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20"+
    "\1\21\1\3\1\22\1\23\1\24\1\25\1\26\1\27"+
    "\1\30\1\31\2\32\1\33\1\34\1\35\1\36\1\37"+
    "\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
    "\1\50\1\51\1\32\1\52\1\32\1\53\1\3\1\54"+
    "\1\32\1\55\1\56\1\57\1\60\1\61\1\62\1\63"+
    "\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73"+
    "\1\32\1\74\1\32\1\75\1\76\1\77\4\3\15\100"+
    "\1\101\75\100\114\0\1\4\2\0\1\4\110\0\1\5"+
    "\140\0\1\102\62\0\2\103\1\0\2\103\1\0\1\103"+
    "\1\104\47\103\1\105\33\103\11\0\1\106\101\0\2\107"+
    "\1\0\2\107\1\0\4\107\1\0\44\107\1\110\33\107"+
    "\15\0\1\54\112\0\1\111\4\0\1\112\111\0\1\113"+
    "\112\0\1\113\1\0\2\24\116\0\1\114\112\0\1\115"+
    "\112\0\1\116\105\0\2\32\5\0\25\32\2\0\23\32"+
    "\32\0\2\32\5\0\1\117\24\32\2\0\1\120\22\32"+
    "\32\0\2\32\5\0\4\32\1\121\20\32\2\0\3\32"+
    "\1\122\17\32\32\0\2\32\5\0\13\32\1\123\6\32"+
    "\1\124\2\32\2\0\12\32\1\125\6\32\1\126\1\32"+
    "\32\0\2\32\5\0\1\127\7\32\1\130\14\32\2\0"+
    "\1\131\6\32\1\132\13\32\32\0\2\32\5\0\16\32"+
    "\1\133\6\32\2\0\15\32\1\134\5\32\32\0\2\32"+
    "\5\0\1\135\24\32\2\0\1\136\22\32\32\0\2\32"+
    "\5\0\12\32\1\137\1\140\11\32\2\0\11\32\1\141"+
    "\1\142\10\32\32\0\2\32\5\0\4\32\1\143\7\32"+
    "\1\144\10\32\2\0\3\32\1\145\7\32\1\146\7\32"+
    "\4\0\1\147\1\0\1\150\23\0\2\32\5\0\10\32"+
    "\1\151\14\32\2\0\7\32\1\152\13\32\32\0\2\32"+
    "\5\0\14\32\1\153\10\32\2\0\13\32\1\153\7\32"+
    "\32\0\2\32\5\0\20\32\1\154\4\32\2\0\17\32"+
    "\1\155\3\32\32\0\2\32\5\0\1\156\15\32\1\157"+
    "\6\32\2\0\1\160\14\32\1\161\5\32\32\0\2\32"+
    "\5\0\4\32\1\162\20\32\2\0\3\32\1\163\17\32"+
    "\32\0\2\32\5\0\10\32\1\164\14\32\2\0\7\32"+
    "\1\165\13\32\32\0\2\32\5\0\4\32\1\166\20\32"+
    "\2\0\3\32\1\167\17\32\32\0\2\32\5\0\4\32"+
    "\1\170\20\32\2\0\3\32\1\171\17\32\32\0\2\32"+
    "\5\0\25\32\2\0\1\120\22\32\32\0\2\32\5\0"+
    "\25\32\2\0\3\32\1\122\17\32\32\0\2\32\5\0"+
    "\25\32\2\0\12\32\1\125\6\32\1\126\1\32\32\0"+
    "\2\32\5\0\25\32\2\0\1\131\6\32\1\132\13\32"+
    "\32\0\2\32\5\0\25\32\2\0\15\32\1\134\5\32"+
    "\32\0\2\32\5\0\25\32\2\0\1\136\22\32\32\0"+
    "\2\32\5\0\25\32\2\0\11\32\1\141\1\142\10\32"+
    "\32\0\2\32\5\0\25\32\2\0\3\32\1\145\7\32"+
    "\1\146\7\32\6\0\1\150\23\0\2\32\5\0\25\32"+
    "\2\0\7\32\1\152\13\32\32\0\2\32\5\0\25\32"+
    "\2\0\13\32\1\153\7\32\32\0\2\32\5\0\25\32"+
    "\2\0\17\32\1\155\3\32\32\0\2\32\5\0\25\32"+
    "\2\0\1\160\14\32\1\161\5\32\32\0\2\32\5\0"+
    "\25\32\2\0\3\32\1\163\17\32\32\0\2\32\5\0"+
    "\25\32\2\0\7\32\1\165\13\32\32\0\2\32\5\0"+
    "\25\32\2\0\3\32\1\167\17\32\32\0\2\32\5\0"+
    "\25\32\2\0\3\32\1\171\17\32\114\0\1\172\27\0"+
    "\1\173\70\0\2\103\4\0\105\103\12\0\1\174\100\0"+
    "\2\107\4\0\105\107\2\112\1\0\2\112\1\0\105\112"+
    "\23\0\2\175\111\0\2\32\5\0\3\32\1\176\12\32"+
    "\1\177\1\200\5\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\2\32\1\201\12\32\1\202\1\203\4\32"+
    "\32\0\2\32\5\0\2\32\1\204\22\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\1\32\1\205\21\32"+
    "\32\0\2\32\5\0\20\32\1\206\4\32\2\0\23\32"+
    "\32\0\2\32\5\0\1\207\24\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\17\32\1\210\3\32\32\0"+
    "\2\32\5\0\25\32\2\0\1\211\22\32\32\0\2\32"+
    "\5\0\11\32\1\212\13\32\2\0\23\32\32\0\2\32"+
    "\5\0\13\32\1\213\11\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\10\32\1\214\12\32\32\0\2\32"+
    "\5\0\25\32\2\0\12\32\1\213\10\32\32\0\2\32"+
    "\5\0\1\215\24\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\1\216\22\32\32\0\2\32\5\0\2\32"+
    "\1\217\22\32\2\0\23\32\32\0\2\32\5\0\25\32"+
    "\2\0\1\32\1\220\21\32\32\0\2\32\5\0\15\32"+
    "\1\221\7\32\2\0\23\32\32\0\2\32\5\0\10\32"+
    "\1\222\14\32\2\0\23\32\32\0\2\32\5\0\25\32"+
    "\2\0\14\32\1\223\6\32\32\0\2\32\5\0\25\32"+
    "\2\0\7\32\1\224\13\32\32\0\2\32\5\0\4\32"+
    "\1\225\20\32\2\0\23\32\32\0\2\32\5\0\6\32"+
    "\1\226\16\32\2\0\23\32\32\0\2\32\5\0\25\32"+
    "\2\0\3\32\1\227\17\32\32\0\2\32\5\0\25\32"+
    "\2\0\5\32\1\230\15\32\47\0\1\231\140\0\1\232"+
    "\47\0\2\32\5\0\4\32\1\233\20\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\3\32\1\234\17\32"+
    "\32\0\2\32\5\0\16\32\1\235\6\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\15\32\1\236\5\32"+
    "\32\0\2\32\5\0\16\32\1\237\6\32\2\0\23\32"+
    "\32\0\2\32\5\0\14\32\1\240\10\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\15\32\1\241\5\32"+
    "\32\0\2\32\5\0\25\32\2\0\13\32\1\242\7\32"+
    "\32\0\2\32\5\0\1\243\24\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\1\244\22\32\32\0\2\32"+
    "\5\0\13\32\1\245\11\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\12\32\1\246\10\32\32\0\2\32"+
    "\5\0\23\32\1\247\1\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\22\32\1\250\32\0\2\32\5\0"+
    "\16\32\1\251\6\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\15\32\1\252\5\32\32\0\2\32\5\0"+
    "\4\32\1\253\20\32\2\0\23\32\32\0\2\32\5\0"+
    "\1\254\24\32\2\0\23\32\3\0\1\255\26\0\2\32"+
    "\5\0\14\32\1\256\10\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\3\32\1\257\17\32\32\0\2\32"+
    "\5\0\25\32\2\0\1\260\22\32\5\0\1\261\24\0"+
    "\2\32\5\0\25\32\2\0\13\32\1\256\7\32\32\0"+
    "\2\32\5\0\10\32\1\262\14\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\7\32\1\263\13\32\32\0"+
    "\2\32\5\0\4\32\1\264\7\32\1\265\10\32\2\0"+
    "\23\32\32\0\2\32\5\0\11\32\1\266\13\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\3\32\1\267"+
    "\7\32\1\270\7\32\32\0\2\32\5\0\25\32\2\0"+
    "\10\32\1\271\12\32\32\0\2\32\5\0\17\32\1\272"+
    "\5\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\16\32\1\273\4\32\32\0\2\32\5\0\5\32\1\274"+
    "\17\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\4\32\1\275\16\32\32\0\2\32\5\0\4\32\1\276"+
    "\20\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\3\32\1\277\17\32\32\0\2\32\5\0\16\32\1\300"+
    "\6\32\2\0\23\32\32\0\2\32\5\0\2\32\1\301"+
    "\22\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\15\32\1\302\5\32\32\0\2\32\5\0\25\32\2\0"+
    "\1\32\1\303\21\32\32\0\2\32\5\0\16\32\1\304"+
    "\6\32\2\0\23\32\32\0\2\32\5\0\10\32\1\305"+
    "\14\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\15\32\1\304\5\32\32\0\2\32\5\0\25\32\2\0"+
    "\7\32\1\306\13\32\51\0\1\307\140\0\1\310\45\0"+
    "\2\32\5\0\13\32\1\311\11\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\12\32\1\312\10\32\32\0"+
    "\2\32\5\0\14\32\1\313\10\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\13\32\1\314\7\32\32\0"+
    "\2\32\5\0\1\315\24\32\2\0\23\32\32\0\2\32"+
    "\5\0\6\32\1\316\16\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\1\317\22\32\32\0\2\32\5\0"+
    "\25\32\2\0\5\32\1\320\15\32\32\0\2\32\5\0"+
    "\11\32\1\321\13\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\10\32\1\321\12\32\32\0\2\32\5\0"+
    "\14\32\1\322\10\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\13\32\1\322\7\32\32\0\2\32\5\0"+
    "\20\32\1\323\4\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\17\32\1\324\3\32\32\0\2\32\5\0"+
    "\3\32\1\325\21\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\2\32\1\326\20\32\32\0\2\32\5\0"+
    "\13\32\1\327\11\32\2\0\23\32\32\0\2\32\5\0"+
    "\2\32\1\330\22\32\2\0\23\32\43\0\1\331\101\0"+
    "\2\32\5\0\25\32\2\0\12\32\1\332\10\32\32\0"+
    "\2\32\5\0\25\32\2\0\1\32\1\333\21\32\71\0"+
    "\1\334\53\0\2\32\5\0\12\32\1\162\12\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\11\32\1\163"+
    "\11\32\32\0\2\32\5\0\16\32\1\335\6\32\2\0"+
    "\23\32\32\0\2\32\5\0\13\32\1\336\11\32\2\0"+
    "\23\32\32\0\2\32\5\0\21\32\1\337\3\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\15\32\1\340"+
    "\5\32\32\0\2\32\5\0\25\32\2\0\12\32\1\341"+
    "\10\32\32\0\2\32\5\0\25\32\2\0\20\32\1\342"+
    "\2\32\32\0\2\32\5\0\14\32\1\343\10\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\13\32\1\343"+
    "\7\32\32\0\2\32\5\0\10\32\1\344\14\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\7\32\1\345"+
    "\13\32\32\0\2\32\5\0\16\32\1\346\6\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\15\32\1\346"+
    "\5\32\32\0\2\32\5\0\10\32\1\347\14\32\2\0"+
    "\23\32\32\0\2\32\5\0\10\32\1\350\14\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\7\32\1\351"+
    "\13\32\32\0\2\32\5\0\25\32\2\0\7\32\1\352"+
    "\13\32\32\0\2\32\5\0\2\32\1\353\22\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\1\32\1\354"+
    "\21\32\43\0\1\355\140\0\1\356\53\0\2\32\5\0"+
    "\20\32\1\357\4\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\17\32\1\360\3\32\32\0\2\32\5\0"+
    "\17\32\1\361\5\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\16\32\1\361\4\32\32\0\2\32\5\0"+
    "\16\32\1\362\6\32\2\0\23\32\32\0\2\32\5\0"+
    "\16\32\1\363\6\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\15\32\1\362\5\32\32\0\2\32\5\0"+
    "\25\32\2\0\15\32\1\364\5\32\32\0\2\32\5\0"+
    "\14\32\1\365\10\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\13\32\1\365\7\32\32\0\2\32\5\0"+
    "\1\366\24\32\2\0\23\32\32\0\2\32\5\0\25\32"+
    "\2\0\1\367\22\32\32\0\2\32\5\0\1\365\24\32"+
    "\2\0\23\32\32\0\2\32\5\0\20\32\1\370\4\32"+
    "\2\0\23\32\61\0\1\371\63\0\2\32\5\0\25\32"+
    "\2\0\1\365\22\32\32\0\2\32\5\0\25\32\2\0"+
    "\17\32\1\372\3\32\107\0\1\373\35\0\2\32\5\0"+
    "\14\32\1\374\10\32\2\0\23\32\32\0\2\32\5\0"+
    "\2\32\1\375\22\32\2\0\23\32\32\0\2\32\5\0"+
    "\1\376\24\32\2\0\23\32\32\0\2\32\5\0\25\32"+
    "\2\0\13\32\1\374\7\32\32\0\2\32\5\0\25\32"+
    "\2\0\1\32\1\377\21\32\32\0\2\32\5\0\25\32"+
    "\2\0\1\u0100\22\32\32\0\2\32\5\0\2\32\1\u0101"+
    "\22\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\1\32\1\u0102\21\32\32\0\2\32\5\0\12\32\1\u0103"+
    "\12\32\2\0\23\32\32\0\2\32\5\0\14\32\1\u0104"+
    "\10\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\11\32\1\u0105\11\32\32\0\2\32\5\0\25\32\2\0"+
    "\13\32\1\u0104\7\32\32\0\2\32\5\0\14\32\1\u0106"+
    "\10\32\2\0\23\32\32\0\2\32\5\0\25\32\2\0"+
    "\13\32\1\u0106\7\32\55\0\1\u0107\140\0\1\u0107\41\0"+
    "\2\32\5\0\16\32\1\u0108\6\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\15\32\1\u0109\5\32\32\0"+
    "\2\32\5\0\1\u010a\24\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\1\u010b\22\32\32\0\2\32\5\0"+
    "\3\32\1\u010c\21\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\2\32\1\u010d\20\32\32\0\2\32\5\0"+
    "\4\32\1\u010e\20\32\2\0\23\32\45\0\1\u010f\77\0"+
    "\2\32\5\0\25\32\2\0\3\32\1\u0110\17\32\73\0"+
    "\1\u0111\51\0\2\32\5\0\4\32\1\u0112\20\32\2\0"+
    "\23\32\32\0\2\32\5\0\16\32\1\u0113\6\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\3\32\1\u0114"+
    "\17\32\32\0\2\32\5\0\25\32\2\0\15\32\1\u0113"+
    "\5\32\32\0\2\32\5\0\1\u0115\24\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\1\u0116\22\32\32\0"+
    "\2\32\5\0\10\32\1\u0117\14\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\7\32\1\u0118\13\32\32\0"+
    "\2\32\5\0\1\u0119\24\32\2\0\23\32\32\0\2\32"+
    "\5\0\25\32\2\0\1\u011a\22\32\32\0\2\32\5\0"+
    "\12\32\1\u011b\12\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\11\32\1\u011c\11\32\32\0\2\32\5\0"+
    "\4\32\1\u011d\20\32\2\0\23\32\32\0\2\32\5\0"+
    "\25\32\2\0\3\32\1\u011e\17\32\32\0\2\32\5\0"+
    "\16\32\1\u011f\6\32\2\0\23\32\57\0\1\u0120\65\0"+
    "\2\32\5\0\25\32\2\0\15\32\1\u011f\5\32\105\0"+
    "\1\u0120\37\0\2\32\5\0\17\32\1\u0121\5\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\16\32\1\u0121"+
    "\4\32\32\0\2\32\5\0\16\32\1\u0122\6\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\15\32\1\u0122"+
    "\5\32\32\0\2\32\5\0\16\32\1\u0123\6\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\15\32\1\u0123"+
    "\5\32\32\0\2\32\5\0\17\32\1\u0124\5\32\2\0"+
    "\23\32\32\0\2\32\5\0\25\32\2\0\16\32\1\u0124"+
    "\4\32\32\0\2\32\5\0\1\u0125\24\32\2\0\23\32"+
    "\32\0\2\32\5\0\25\32\2\0\1\u0125\22\32\32\0"+
    "\2\32\5\0\16\32\1\u0126\6\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\15\32\1\u0127\5\32\32\0"+
    "\2\32\5\0\14\32\1\u0128\10\32\2\0\23\32\32\0"+
    "\2\32\5\0\25\32\2\0\13\32\1\u0128\7\32\7\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[18450];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** Error code for "Unknown internal scanner error". */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  /** Error code for "could not match input". */
  private static final int ZZ_NO_MATCH = 1;
  /** Error code for "pushback value was too large". */
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /**
   * Error messages for {@link #ZZ_UNKNOWN_ERROR}, {@link #ZZ_NO_MATCH}, and
   * {@link #ZZ_PUSHBACK_2BIG} respectively.
   */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\1\1\1\11\3\1\1\11\2\1\2\11"+
    "\1\1\3\11\3\1\2\11\25\1\1\11\20\1\1\11"+
    "\1\1\2\11\1\1\1\11\1\0\1\11\1\0\1\11"+
    "\2\0\1\11\2\1\3\11\30\1\2\0\21\1\3\11"+
    "\34\1\2\0\22\1\1\0\3\1\1\0\25\1\2\0"+
    "\20\1\1\0\2\1\1\0\20\1\2\0\12\1\1\0"+
    "\1\1\1\0\13\1\1\11\7\1\1\0\1\1\1\0"+
    "\16\1\1\11\10\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[296];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** Input device. */
  private java.io.Reader zzReader;

  /** Current state of the DFA. */
  private int zzState;

  /** Current lexical state. */
  private int zzLexicalState = YYINITIAL;

  /**
   * This buffer contains the current text to be matched and is the source of the {@link #yytext()}
   * string.
   */
  private char zzBuffer[] = new char[Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen())];

  /** Text position at the last accepting state. */
  private int zzMarkedPos;

  /** Current text position in the buffer. */
  private int zzCurrentPos;

  /** Marks the beginning of the {@link #yytext()} string in the buffer. */
  private int zzStartRead;

  /** Marks the last character in the buffer, that has been read from input. */
  private int zzEndRead;

  /**
   * Whether the scanner is at the end of file.
   * @see #yyatEOF
   */
  private boolean zzAtEOF;

  /**
   * The number of occupied positions in {@link #zzBuffer} beyond {@link #zzEndRead}.
   *
   * <p>When a lead/high surrogate has been read from the input stream into the final
   * {@link #zzBuffer} position, this will have a value of 1; otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** Number of newlines encountered up to the start of the matched text. */
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  private int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
    private Token t(TipoToken tipo, String lexema) {
        return new Token(tipo, lexema, yyline + 1, yycolumn + 1);
    }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public AnalizadorLexico(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  /**
   * Refills the input buffer.
   *
   * @return {@code false} iff there was new input.
   * @exception java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead - zzStartRead);

      /* translate stored positions */
      zzEndRead -= zzStartRead;
      zzCurrentPos -= zzStartRead;
      zzMarkedPos -= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate && zzCanGrow()) {
      /* if not, and it can grow: blow it up */
      char newBuffer[] = new char[Math.min(zzBuffer.length * 2, zzMaxBufferLen())];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      if (requested == 0) {
        throw new java.io.EOFException("Scan buffer limit reached ["+zzBuffer.length+"]");
      }
      else {
        throw new java.io.IOException(
            "Reader returned 0 characters. See JFlex examples/zero-reader for a workaround.");
      }
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
        if (numRead == requested) { // We requested too few chars to encode a full Unicode character
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        } else {                    // There is room in the buffer for at least one more char
          int c = zzReader.read();  // Expecting to read a paired low surrogate char
          if (c == -1) {
            return true;
          } else {
            zzBuffer[zzEndRead++] = (char)c;
          }
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }


  /**
   * Closes the input reader.
   *
   * @throws java.io.IOException if the reader could not be closed.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true; // indicate end of file
    zzEndRead = zzStartRead; // invalidate buffer

    if (zzReader != null) {
      zzReader.close();
    }
  }


  /**
   * Resets the scanner to read from a new input stream.
   *
   * <p>Does not close the old reader.
   *
   * <p>All internal variables are reset, the old input stream <b>cannot</b> be reused (internal
   * buffer is discarded and lost). Lexical state is set to {@code ZZ_INITIAL}.
   *
   * <p>Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader The new input stream.
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzEOFDone = false;
    yyResetPosition();
    zzLexicalState = YYINITIAL;
    int initBufferSize = Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen());
    if (zzBuffer.length > initBufferSize) {
      zzBuffer = new char[initBufferSize];
    }
  }

  /**
   * Resets the input position.
   */
  private final void yyResetPosition() {
      zzAtBOL  = true;
      zzAtEOF  = false;
      zzCurrentPos = 0;
      zzMarkedPos = 0;
      zzStartRead = 0;
      zzEndRead = 0;
      zzFinalHighSurrogate = 0;
      yyline = 0;
      yycolumn = 0;
      yychar = 0L;
  }


  /**
   * Returns whether the scanner has reached the end of the reader it reads from.
   *
   * @return whether the scanner has reached EOF.
   */
  public final boolean yyatEOF() {
    return zzAtEOF;
  }


  /**
   * Returns the current lexical state.
   *
   * @return the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state.
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   *
   * @return the matched text.
   */
  public final String yytext() {
    return new String(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
  }


  /**
   * Returns the character at the given position from the matched text.
   *
   * <p>It is equivalent to {@code yytext().charAt(pos)}, but faster.
   *
   * @param position the position of the character to fetch. A value from 0 to {@code yylength()-1}.
   *
   * @return the character at {@code position}.
   */
  public final char yycharat(int position) {
    return zzBuffer[zzStartRead + position];
  }


  /**
   * How many characters were matched.
   *
   * @return the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * <p>In a well-formed scanner (no or only correct usage of {@code yypushback(int)} and a
   * match-all fallback rule) this method will only be called with things that
   * "Can't Possibly Happen".
   *
   * <p>If this method is called, something is seriously wrong (e.g. a JFlex bug producing a faulty
   * scanner etc.).
   *
   * <p>Usual syntax/scanner level error handling should be done in error fallback rules.
   *
   * @param errorCode the code of the error message to display.
   */
  private static void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    } catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * <p>They will be read again by then next call of the scanning method.
   *
   * @param number the number of characters to be read again. This number must not be greater than
   *     {@link #yylength()}.
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }




  /**
   * Resumes scanning until the next regular expression is matched, the end of input is encountered
   * or an I/O-Error occurs.
   *
   * @return the next token.
   * @exception java.io.IOException if any I/O-Error occurs.
   */
  public Token proximoToken() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char[] zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is
        // (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof)
            zzPeek = false;
          else
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            switch (zzLexicalState) {
            case YYINITIAL: {
              return t(TipoToken.EOF, "EOF");
            }  // fall though
            case 297: break;
            default:
        return null;
        }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { return t(TipoToken.ERROR, yytext());
            }
          // fall through
          case 58: break;
          case 2:
            { /* Ignorar espacios */
            }
          // fall through
          case 59: break;
          case 3:
            { return t(TipoToken.OP_NO, yytext());
            }
          // fall through
          case 60: break;
          case 4:
            { return t(TipoToken.OP_MOD, yytext());
            }
          // fall through
          case 61: break;
          case 5:
            { return t(TipoToken.PARENT_ABRE, yytext());
            }
          // fall through
          case 62: break;
          case 6:
            { return t(TipoToken.PARENT_CIERRA, yytext());
            }
          // fall through
          case 63: break;
          case 7:
            { return t(TipoToken.OP_MULT, yytext());
            }
          // fall through
          case 64: break;
          case 8:
            { return t(TipoToken.OP_SUMA, yytext());
            }
          // fall through
          case 65: break;
          case 9:
            { return t(TipoToken.COMA, yytext());
            }
          // fall through
          case 66: break;
          case 10:
            { return t(TipoToken.OP_RESTA, yytext());
            }
          // fall through
          case 67: break;
          case 11:
            { return t(TipoToken.OP_DIV, yytext());
            }
          // fall through
          case 68: break;
          case 12:
            { return t(TipoToken.NUMERO_ENTERO, yytext());
            }
          // fall through
          case 69: break;
          case 13:
            { return t(TipoToken.DOS_PUNTOS, yytext());
            }
          // fall through
          case 70: break;
          case 14:
            { return t(TipoToken.FIN_SENTENCIA, yytext());
            }
          // fall through
          case 71: break;
          case 15:
            { return t(TipoToken.MENOR_QUE, yytext());
            }
          // fall through
          case 72: break;
          case 16:
            { return t(TipoToken.OP_ASIG, yytext());
            }
          // fall through
          case 73: break;
          case 17:
            { return t(TipoToken.MAYOR_QUE, yytext());
            }
          // fall through
          case 74: break;
          case 18:
            { return t(TipoToken.IDENTIFICADOR, yytext());
            }
          // fall through
          case 75: break;
          case 19:
            { return t(TipoToken.OP_O, yytext());
            }
          // fall through
          case 76: break;
          case 20:
            { return t(TipoToken.OP_Y, yytext());
            }
          // fall through
          case 77: break;
          case 21:
            { return t(TipoToken.OP_POT, yytext());
            }
          // fall through
          case 78: break;
          case 22:
            { return t(TipoToken.LLAVE_ABRE, yytext());
            }
          // fall through
          case 79: break;
          case 23:
            { return t(TipoToken.LLAVE_CIERRA, yytext());
            }
          // fall through
          case 80: break;
          case 24:
            { /* Ignorar contenido del comentario */
            }
          // fall through
          case 81: break;
          case 25:
            { return t(TipoToken.DIFERENTE_QUE, yytext());
            }
          // fall through
          case 82: break;
          case 26:
            { return t(TipoToken.VALOR_CADENA, yytext());
            }
          // fall through
          case 83: break;
          case 27:
            { yybegin(COMENTARIO_BLOQUE);
            }
          // fall through
          case 84: break;
          case 28:
            { return t(TipoToken.COMENTARIO, yytext());
            }
          // fall through
          case 85: break;
          case 29:
            { return t(TipoToken.ERROR_NUMERO, yytext());
            }
          // fall through
          case 86: break;
          case 30:
            { return t(TipoToken.MENOR_IGUAL, yytext());
            }
          // fall through
          case 87: break;
          case 31:
            { return t(TipoToken.IGUAL_QUE, yytext());
            }
          // fall through
          case 88: break;
          case 32:
            { return t(TipoToken.MAYOR_IGUAL, yytext());
            }
          // fall through
          case 89: break;
          case 33:
            { return t(TipoToken.SI, yytext());
            }
          // fall through
          case 90: break;
          case 34:
            { yybegin(YYINITIAL);
            }
          // fall through
          case 91: break;
          case 35:
            { return t(TipoToken.VALOR_CARACTER, yytext());
            }
          // fall through
          case 92: break;
          case 36:
            { return t(TipoToken.NUMERO_REAL, yytext());
            }
          // fall through
          case 93: break;
          case 37:
            { return t(TipoToken.FIN, yytext());
            }
          // fall through
          case 94: break;
          case 38:
            { return t(TipoToken.CASO, yytext());
            }
          // fall through
          case 95: break;
          case 39:
            { return t(TipoToken.LEER, yytext());
            }
          // fall through
          case 96: break;
          case 40:
            { return t(TipoToken.OTRO, yytext());
            }
          // fall through
          case 97: break;
          case 41:
            { return t(TipoToken.TIPO_REAL, yytext());
            }
          // fall through
          case 98: break;
          case 42:
            { return t(TipoToken.SINO, yytext());
            }
          // fall through
          case 99: break;
          case 43:
            { return t(TipoToken.VALOR_FALSO, yytext());
            }
          // fall through
          case 100: break;
          case 44:
            { return t(TipoToken.HACER, yytext());
            }
          // fall through
          case 101: break;
          case 45:
            { return t(TipoToken.PARAR, yytext());
            }
          // fall through
          case 102: break;
          case 46:
            { return t(TipoToken.TIPO_CADENA, yytext());
            }
          // fall through
          case 103: break;
          case 47:
            { return t(TipoToken.TIPO_ENTERO, yytext());
            }
          // fall through
          case 104: break;
          case 48:
            { return t(TipoToken.INICIO, yytext());
            }
          // fall through
          case 105: break;
          case 49:
            { return t(TipoToken.TIPO_LOGICO, yytext());
            }
          // fall through
          case 106: break;
          case 50:
            { return t(TipoToken.EVALUAR, yytext());
            }
          // fall through
          case 107: break;
          case 51:
            { return t(TipoToken.TIPO_CARACTER, yytext());
            }
          // fall through
          case 108: break;
          case 52:
            { return t(TipoToken.ENTONCES, yytext());
            }
          // fall through
          case 109: break;
          case 53:
            { return t(TipoToken.GRAFICAR, yytext());
            }
          // fall through
          case 110: break;
          case 54:
            { return t(TipoToken.IMPRIMIR, yytext());
            }
          // fall through
          case 111: break;
          case 55:
            { return t(TipoToken.MIENTRAS, yytext());
            }
          // fall through
          case 112: break;
          case 56:
            { return t(TipoToken.PROGRAMA, yytext());
            }
          // fall through
          case 113: break;
          case 57:
            { return t(TipoToken.VALOR_VERDADERO, yytext());
            }
          // fall through
          case 114: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
