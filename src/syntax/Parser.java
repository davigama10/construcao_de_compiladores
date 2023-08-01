package syntax;

import exceptions.SyntaxException;
import lexical.Scanner;
import lexical.Token;
import utils.TokenType;

public class Parser {
	private Scanner scanner;
	private Token token;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	public void programa() {
		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.ASSIGN) {
			throw new SyntaxException("Expected ':', found " + token.getContent());
		}
		this.token = this.scanner.nextToken();
		if (!"DECLARACOES".equals(this.token.getContent())) {
			throw new SyntaxException("Expected 'DECLARACOES', found " + token.getContent());
		}
		listaDeclaracoes();
		this.token = this.scanner.nextToken();
		if (!"ALGORITIMO".equals(this.token.getContent())) {
			throw new SyntaxException("Expected 'DECLARACOES', found " + token.getContent());
		}
		listaComandos();
	}

	private void listaDeclaracoes() {
		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.ASSIGN) {
			declaracao();
			listaDeclaracoes();
		}
	}

	private void declaracao() {
		varLista();
		if (this.token.getType() != TokenType.ASSIGN) {
			throw new SyntaxException("Expected ':', found " + token.getContent());
		}
		tipoVar();
		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.DELIM) {
			throw new SyntaxException("Expected ';', found " + token.getContent());
		}
	}

	private void tipoVar() {
		this.token = this.scanner.nextToken();
		if (!"INTEIRO".equals(this.token.getContent()) && !"REAL".equals(this.token.getContent())) {
			throw new SyntaxException("Expected number, found " + token.getContent());
		}
	}

	public void varLista() {
		// this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.IDENTYFIER) {
			throw new SyntaxException("Expected identyfier, found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() == TokenType.COMMA) {
			varLista();
		}
	}

	private void listaComandos() {
		this.token = this.scanner.nextToken();
		if (this.token != null) {
			comando();
			listaComandos();
		}
	}

	private void listaComandos2() {
		this.token = this.scanner.nextToken();
		if (this.token != null && (this.token.getType() != TokenType.ELSE)) {
			comando();
			listaComandos2();
		}
	}

	private void comando() {
		if (this.token.getType() == TokenType.ASSIGN2) {
			comandoAtribuicao();
		} else if (this.token.getType() == TokenType.INPUT) {
			comandoEntrada();
		} else if (this.token.getType() == TokenType.PRINT) {
			comandoSaida();
		} else if (this.token.getType() == TokenType.IF) {
			comandoCondicao();
		} else if (this.token.getType() == TokenType.WHILE) {
			comandoRepeticao();
		}

	}

	private void comandoAtribuicao() {
		expressaoAritmetica();
		// this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.TO) {
			throw new SyntaxException("Expected 'TO', found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.IDENTYFIER) {
			throw new SyntaxException("Expected IDENTYFIER, found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.DELIM) {
			throw new SyntaxException("Expected ';', found " + token.getType());
		}

	}

	private void comandoEntrada() {
		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.IDENTYFIER) {
			throw new SyntaxException("Expected ':', found " + token.getContent());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.DELIM) {
			throw new SyntaxException("Expected ':', found " + token.getContent());
		}
	}

	private void comandoSaida() {
		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.L_PAR) {
			throw new SyntaxException("Expected L_PAR, found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.IDENTYFIER && this.token.getType() != TokenType.STRING) {
			throw new SyntaxException("Expected IDENTYFIER or STRING, found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.R_PAR) {
			throw new SyntaxException("Expected R_PAR, found " + token.getType());
		}

		this.token = this.scanner.nextToken();
		if (this.token.getType() != TokenType.DELIM) {
			throw new SyntaxException("Expected ';', found " + token.getType());
		}

	}

	private void comandoCondicao() {
		expressaoRelacional();
		if (this.token.getType() != TokenType.THEN) {
			throw new SyntaxException("Expected 'THEN', found " + token.getContent());
		}
		listaComandos2();
		comandoCondicao2();
	}

	private void comandoCondicao2() {
		if (this.token != null && (this.token.getType() != TokenType.ELSE)) {
			throw new SyntaxException("Expected 'ELSE', found " + token.getContent());

		}
		listaComandos();
	}

	private void comandoRepeticao() {
		expressaoRelacional();
		if (this.token.getType() != TokenType.ASSIGN) {
			throw new SyntaxException("Expected ':', found " + token.getType());
		}
		listaComandos();
	}

	private void expressaoRelacional() {
		termoRelacional();
		expressaoRelacional2();
	}

	private void expressaoRelacional2() {
		if (this.token.getType() == TokenType.AND || this.token.getType() == TokenType.OR) {
			expressaoRelacional();
		}
	}

	private void termoRelacional() {
		expressaoAritmetica();
		if (this.token.getType() != TokenType.REL_OP) {
			throw new SyntaxException("Expected REL_OP, found " + token.getType());
		}
		expressaoAritmetica();
	}

	private void expressaoAritmetica() {
		termoAritmetico();
		expressaoAritmetica2();
	}

	private void expressaoAritmetica2() {
		if (expressaoAritmetica3()) {
			expressaoAritmetica2();
		}
	}

	private boolean expressaoAritmetica3() {
		if ("+".equals(this.token.getContent()) || "-".equals(this.token.getContent())) {
			termoAritmetico();
			return true;
		} else {
			return false;
		}
	}

	private void termoAritmetico() {
		fatorAritmetico();
		termoAritmetico2();
	}

	private void termoAritmetico2() {
		if (termoAritmetico3()) {
			termoAritmetico2();
		}
	}

	private boolean termoAritmetico3() {
		this.token = this.scanner.nextToken();
		if ("*".equals(this.token.getContent()) || "/".equals(this.token.getContent())) {
			fatorAritmetico();
			return true;
		} else {
			return false;
		}
	}

	private void fatorAritmetico() {
		this.token = this.scanner.nextToken();
		if (this.token.getType() == TokenType.L_PAR) {
			expressaoAritmetica();
		}
		if (this.token.getType() != TokenType.INT && this.token.getType() != TokenType.FLOAT
				&& this.token.getType() != TokenType.IDENTYFIER && this.token.getType() != TokenType.R_PAR) {
			throw new SyntaxException("Expected 'INT or FLOAT or IDENTYFIER', found " + token.getContent());
		}
	}

}
