/** Chimaltenango 30 de mayo 2026
Proyecto Final
Integrantes: 
1990-23-4406	Christopher Obryan Alexander Mazariegos Crúz
1990-23-17188	Luis Miguel Vaquiax Camey
1990-23-10442	Keyner Alejandro Rivera Axpuac
1990-23-22934	Freyder José Sequén Urlao
**/

package com.company.api_compilador.lexico;     
// Define una clase publica llamada 'Token'.
// Esta clase sirve como una estructura de datos para almacenar la informacion
// sobre una unidad lexica (un "token") reconocida por el analizador lexico.
/**
 * Representa una unidad léxica reconocida por el analizador léxico.
 *
 * Un token contiene el tipo, el texto exacto encontrado y su posición dentro
 * del código fuente.
 */
public class Token {
    
  //Declaramos final para que no se puedan modificar los valores en el futuro  
  public final TipoToken tipo;
  
  // Almacena el texto exacto (lexema) reconocido (ej: "VariableTipo", "123.4")
  public final String lexema;
  
  // Posicionamiento en el archivo fuente
  public final int linea;
  public final int columna;

  /**
   * Constructor para crear una nueva instancia de un token.
   * @param tipo El tipo de token (de la enumeración TipoToken).
   * @param lexema El texto original del token.
   * @param linea El número de línea donde se encontró.
   * @param columna El número de columna donde se encontró.
   */
  public Token(TipoToken tipo, String lexema, int linea, int columna) {
    this.tipo   = tipo;
    this.lexema = lexema;
    this.linea  = linea;
    this.columna = columna;
  }
  
  // --- GETTERS (Obligatorios para Spring Boot y JSON) ---

    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

  //Sobreescribimos el método
  @Override
  public String toString() {
    // Proporciona una representación legible: [IDENTIFICADOR] "miVariable" (L5,C10)
    return String.format("[%s] \"%s\" (L%d,C%d)", tipo, lexema, linea, columna);
  }
}
