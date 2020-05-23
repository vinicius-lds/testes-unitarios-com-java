package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.testutils.TestOrder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
//@RunWith(ParallelRunner.class)
public class CalculadoraTest {

    @InjectMocks Calculadora calculadora;

    @Mock Calculadora calculadoraMock;
    @Spy Calculadora calculadoraSpy;

    @Before public void beforeEach() {
        initMocks(this);
    }

    @AfterClass public static void afterAll() {
        TestOrder.flush();
    }

    @Test public void sumTwoValues() {
        TestOrder.appendInit();
        var v1 = 5;
        var v2 = 5;
        var result = calculadora.somar(v1, v2);
        assertEquals(10, result);
        TestOrder.appendFinish();
    }

    @Test public void subtractTwoValues() {
        TestOrder.appendInit();
        var v1 = 10;
        var v2 = 5;
        var result = calculadora.subtrair(v1, v2);
        assertEquals(5, result);
        TestOrder.appendFinish();
    }

    @Test public void divideTwoValues() throws NaoPodeDividirPorZeroException {
        TestOrder.appendInit();
        var v1 = 10;
        var v2 = 5;
        var result = calculadora.divide(v1, v2);
        assertEquals(2, result);
        TestOrder.appendFinish();
    }

    @Test public void shouldThrownWhenDividingByZero() {
        TestOrder.appendInit();
        var v1 = 10;
        var v2 = 0;
        assertThrows("Deve lançar uma exceção ao dividir por zero", NaoPodeDividirPorZeroException.class, () -> calculadora.divide(v1, v2));
        TestOrder.appendFinish();
    }

    @Test public void playingWithMocksAndSpies(){
        TestOrder.appendInit();
        when(calculadoraMock.somar(1, 2)).thenReturn(5);;
        Mockito.doReturn(5).when(calculadoraSpy).somar(1, 2);
        Mockito.doNothing().when(calculadoraSpy).imprime();

        System.out.println("Mock:" + calculadoraMock.somar(1, 2));
        System.out.println("Spy:" + calculadoraSpy.somar(1, 2));

        System.out.println("Mock");
        calculadoraMock.imprime();
        System.out.println("Spy");
        calculadoraSpy.imprime();
        TestOrder.appendFinish();
    }

}
