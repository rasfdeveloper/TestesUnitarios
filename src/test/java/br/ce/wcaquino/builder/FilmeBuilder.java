package br.ce.wcaquino.builder;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {
	
	private Filme filme;
	
	private FilmeBuilder(){}
	
	public static FilmeBuilder umFilme(){
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(2);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(5.0);
		return builder;
		
	}
	
	public static FilmeBuilder umFilmeSemEstoque(){
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(0);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(5.0);
		return builder;
	}
	
	public FilmeBuilder semEstoque(){
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder precoLocacao(Double preco){
		filme.setPrecoLocacao(preco);
		return this;
	}
	
	public FilmeBuilder nome(String nome){
		filme.setNome(nome);
		return this;
	}
	
	public Filme agora(){
		return filme;
	}
	
	
}
