package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private LocacaoDAO dao;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	private static Filme filme1 = umFilme().nome("Filme 1").precoLocacao(4.0).agora();
	private static Filme filme2 = umFilme().nome("Filme 2").precoLocacao(4.0).agora();
	private static Filme filme3 = umFilme().nome("Filme 3").precoLocacao(4.0).agora();
	private static Filme filme4 = umFilme().nome("Filme 4").precoLocacao(4.0).agora();
	private static Filme filme5 = umFilme().nome("Filme 5").precoLocacao(4.0).agora();
	private static Filme filme6 = umFilme().nome("Filme 6").precoLocacao(4.0).agora();
	private static Filme filme7 = umFilme().nome("Filme 7").precoLocacao(4.0).agora();
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem Desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem Desconto"}
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = umUsuario().agora();
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(valorLocacao));
		
	}
}
