package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.testutils.TestOrder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RunWith(Parameterized.class)
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CalculoValorLocacaoTest {

    static Filme filme1 = umFilme().agora();
    static Filme filme2 = umFilme().agora();
    static Filme filme3 = umFilme().agora();
    static Filme filme4 = umFilme().agora();
    static Filme filme5 = umFilme().agora();
    static Filme filme6 = umFilme().agora();
    static Filme filme7 = umFilme().agora();

    @Parameterized.Parameter
    List<Filme> filmes;
    @Parameterized.Parameter(value = 1)
    double valorlocacao;
    @Parameterized.Parameter(value = 2)
    String failMessage;

    @InjectMocks LocacaoService locacaoService;
    @Mock SPCService spcService;
    @Mock LocacaoDAO locacaoDAO;
    @Mock EmailService emailService;

    @Before public void beforeEach() {
        initMocks(this);
    }

    @AfterClass public static void afterAll() {
        TestOrder.flush();
    }

    @Parameterized.Parameters(name = "{2}") public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                { Collections.singletonList(filme1), 4.0, "Um filme não deve ter desconto" },
                { Arrays.asList(filme1, filme2), 8.0, "Dois filmes não devem ter desconto" },
                { Arrays.asList(filme1, filme2, filme3), 11.0, "Terceiro filme deve ter desconto de 25%" },
                { Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "Quarto filme deve ter desconto de 50%" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "Quinto filme deve ter desconto de 75%" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "Sexto filme deve ser de graça" },
                { Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "Sétimo filme não deve ter desconto" },
        });
    }

    @Test public void moviesShouldHaveProperDiscounts() throws FilmeSemEstoqueException, LocadoraException {
        TestOrder.appendInit();

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var usuario = new Usuario("Usuário 1");
        var locacao = locacaoService.alugarFilme(usuario, filmes);
        assertEquals(failMessage, valorlocacao, locacao.getValor(), 0.01);
        TestOrder.appendFinish();
    }
}
