package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import utils.TokenType;

public class Scanner {

	int pos;
	char[] contentTXT;
	int state;
	int line = 1;
	int column = 1;
	String[] keyWords = new String[] { "INT", "FLOAT" };
	String ASSIGN = "TO";
	String PRINT = "PRINT";
	String IF = "IF";
	String ELSE = "ELSE";
	String THEN = "THEN";
	String WHILE = "WHILE";
	String AND = "AND";
	String OR = "OR";
	String ASSIGN2 = "ASSIGN";
	String INPUT = "INPUT";
	List<String> lista = Arrays.asList(keyWords);

	public Scanner(String filename) {
		try {
			String contentBuffer = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			this.contentTXT = contentBuffer.toCharArray();
			this.pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Token nextToken() {
		this.state = 0;
		String content = "";
		char currentChar;

		while (true) {
			if (isEOF()) {
				return null;
			}
			currentChar = this.nextChar();

			switch (state) {
				case 0:
					if (this.isLetter(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (isSpace(currentChar)) {
						state = 0;
					} else if (isDigit(currentChar)) {
						content += currentChar;
						state = 2;
					} else if (isAssignment(currentChar)) {
						content += currentChar;
						state = 4;
					} else if (isOperator(currentChar)) {
						content += currentChar;
						state = 3;
					} else if (isOperatorMath(currentChar)) {
						content += currentChar;
						state = 5;
					} else if (isLParenth(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isRParenth(currentChar)) {
						content += currentChar;
						state = 7;
					} else if (isComment(currentChar)) {
						state = 8;
					} else if (isDot(currentChar)) {
						content += currentChar;
						state = 9;
					} else if (isAssign(currentChar)) {
						content += currentChar;
						state = 10;
					} else if (isDelim(currentChar)) {
						content += currentChar;
						state = 11;
					} else if (isComma(currentChar)) {
						content += currentChar;
						state = 12;
					} else if (isString(currentChar)) {
						content += currentChar;
						state = 13;
					}

					break;
				case 1:
					if (this.isLetter(currentChar) || this.isDigit(currentChar)) {
						content += currentChar;
						state = 1;
					} else {
						this.back();
						if (lista.contains(content)) {
							return new Token(TokenType.KEYWORD, content);
						} else if (Objects.equals(content, ASSIGN)) {
							return new Token(TokenType.TO, content);
						} else if (Objects.equals(content, PRINT)) {
							return new Token(TokenType.PRINT, content);
						} else if (Objects.equals(content, IF)) {
							return new Token(TokenType.IF, content);
						} else if (Objects.equals(content, ELSE)) {
							return new Token(TokenType.ELSE, content);
						} else if (Objects.equals(content, THEN)) {
							return new Token(TokenType.THEN, content);
						} else if (Objects.equals(content, WHILE)) {
							return new Token(TokenType.WHILE, content);
						} else if (Objects.equals(content, AND)) {
							return new Token(TokenType.AND, content);
						} else if (Objects.equals(content, OR)) {
							return new Token(TokenType.OR, content);
						} else if (Objects.equals(content, INPUT)) {
							return new Token(TokenType.INPUT, content);
						} else if (Objects.equals(content, ASSIGN2)) {
							return new Token(TokenType.ASSIGN2, content);
						}
						return new Token(TokenType.IDENTYFIER, content);
					}
					break;
				case 2:
					if (isDot(currentChar)) {
						content += currentChar;
						state = 9;
					} else if (isDigit(currentChar)) {
						content += currentChar;
						state = 2;
					} else if ((isSpace(currentChar) || isOperatorMath(currentChar) || isOperator(currentChar)
							|| isAssign(currentChar))) {
						this.back();
						return new Token(TokenType.INT, content);
					} else {
						throw new RuntimeException("Number malformed: " + error_pos(this.pos));
					}
					break;
				case 3:
					if (isAssignment(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OP, content);
					} else {
						this.back();
						return new Token(TokenType.REL_OP, content);
					}
				case 4:
					if (currentChar == '=') {
						content += currentChar;
					}
					return new Token(TokenType.ASSIGNMENT, content);
				case 5:
					this.back();
					return new Token(TokenType.MATH_OP, content);
				case 6:
					this.back();
					return new Token(TokenType.L_PAR, content);
				case 7:
					this.back();
					return new Token(TokenType.R_PAR, content);
				case 8:
					if (currentChar == '\n' || currentChar == '\r') {
						state = 0;
					} else {
						state = 8;
					}
				case 9: // Caso seja um ponto depois de um número
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 9;
					} else if (isSpace(currentChar)) {
						this.back();
						return new Token(TokenType.FLOAT, content);
					} else {
						throw new RuntimeException("Number malformed: " + error_pos(this.pos));
					}
				case 10:
					this.back();
					return new Token(TokenType.ASSIGN, content);
				case 11:
					this.back();
					return new Token(TokenType.DELIM, content);
				case 12:
					this.back();
					return new Token(TokenType.COMMA, content);
				case 13:
					if (currentChar == '"') {
						content += currentChar;
						return new Token(TokenType.STRING, content);
					} else {
						content += currentChar;
						state = 13;
					}
					break;
			}
		}
	}

	private char nextChar() {
		return this.contentTXT[this.pos++];
	}

	private void back() {
		this.pos--;
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
	}

	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	private boolean isOperator(char c) {
		return c == '>' || c == '<' || c == '!' || c == '=';
	}

	private boolean isAssignment(char c) {
		return c == '=';
	}

	private boolean isAssign(char c) {
		return c == ':';
	}

	private boolean isOperatorMath(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}

	private boolean isLParenth(char c) {
		return c == '(';
	}

	private boolean isRParenth(char c) {
		return c == ')';
	}

	private boolean isDot(char c) {
		return c == '.';
	}

	private boolean isComment(char c) {
		return c == '#';
	}

	private boolean isDelim(char c) {
		return c == ';';
	}

	private boolean isComma(char c) {
		return c == ',';
	}

	private boolean isString(char c) {
		return c == '"';
	}

	private String error_pos(int length) {
		for (int i = 0; i < (length - 1); i++) {
			char c = this.contentTXT[i];
			if (c == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
		}
		String mensage = "Line: " + line + " Column: " + (column);
		return mensage;

	}

	private boolean isEOF() {
		if (this.pos >= this.contentTXT.length) {
			return true;
		}
		return false;
	}

}
