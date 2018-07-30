package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;


public class LocacaoServiceTest {

	@InjectMocks @Spy
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		System.out.println("Inicialiando 2 ...");
		CalculadoraTest.ordem.append("2 ");
	}
	
	@After
	public void tearDown(){
		System.out.println("Filanizando 2 ...");
	}
	
	@AfterClass
	public static void tearDownClass(){
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(30, 7, 2018)).when(service).obterData();
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(4.0)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(30, 7, 2018)), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(31, 7, 2018)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception{
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().semEstoque().agora());
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
//		Usuario usuario2 = umUsuario().nome("Usuario 2").agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			//verificacao
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().nome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().nome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao()
					.comUsuario(usuario)
					.atrasada()
					.agora(),
				umLocacao()
					.comUsuario(usuario2)
					.agora(),
				umLocacao()
					.comUsuario(usuario3)
					.atrasada()
					.agora(),
				umLocacao()
					.comUsuario(usuario3)
					.atrasada()
					.agora()
				);
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(emailService).notificarAtraso(usuario);
		verify(emailService, atLeastOnce()).notificarAtraso(usuario3);
		verify(emailService, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(emailService);
		
		
	}
	
	@Test
	public void deveTratarErronoSPC() throws Exception{
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha Catastrofica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC");
		
		//acao
		service.alugarFilme(usuario, filmes);
			
	}
	
	@Test
	public void deveProrrogarUmaLocacao(){
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 1);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(),is(4.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(1));
		
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception{
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method method = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		method.setAccessible(true);
		Double valor = (Double) method.invoke(service, filmes);
			
		//verificacao
		assertThat(valor, is(4.0));
		
	}
	
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
	
	
}
