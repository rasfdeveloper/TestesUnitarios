package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Mock
	private EmailService email;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy(){
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
//		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		Mockito.doReturn(5).when(calcSpy).somar(1,2); //Mestre yoda
		
		Mockito.doNothing().when(calcSpy).imprime();
		
		System.out.println("Mock:" + calcMock.somar(1, 2));
		System.out.println("Spy:" + calcSpy.somar(1, 2));
		System.out.println("Mock");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
	}
	
	
	@Test
	public void teste(){
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5, calc.somar(123124,21412412));
//		System.out.println(argCapt.getAllValues());
		
	}

}
