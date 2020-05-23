package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.TestMatchers.isDate;
import static br.ce.wcaquino.matchers.TestMatchers.isMonday;
import static br.ce.wcaquino.matchers.TestMatchers.isToday;
import static br.ce.wcaquino.matchers.TestMatchers.isTomorrow;
import static br.ce.wcaquino.matchers.TestMatchers.isTreeDaysFromToday;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.testutils.TestOrder;
import br.ce.wcaquino.utils.DataUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
@FieldDefaults(level = AccessLevel.PUBLIC)
public class LocacaoServiceTest {

    @InjectMocks LocacaoService locacaoServico;
    @Mock SPCService spcService;
    @Mock LocacaoDAO locacaoDAO;
    @Mock EmailService emailService;
    @Rule ErrorCollector errorCollector = new ErrorCollector();

    // Deprecado pois a forma recomendada desde o junit 5 é o assertThrows
    @Rule ExpectedException expectedException = ExpectedException.none();

    @Before public void beforeEach() {
        initMocks(this);
        locacaoServico = PowerMockito.spy(locacaoServico);
    }

    @AfterClass public static void afterAll() {
        TestOrder.flush();
    }

    @Ignore @Test public void testeWithAssumption() throws Exception {
        TestOrder.appendInit();
        assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().agora();

        var locacao = locacaoServico.alugarFilme(usuario, singletonList(filme));

        errorCollector.checkThat(locacao.getValor(), is(equalTo(4.0)));
        errorCollector.checkThat(locacao.getDataLocacao(), isToday());
        errorCollector.checkThat(locacao.getDataRetorno(), isTomorrow());
        TestOrder.appendFinish();
    }

    @Test public void teste() throws Exception {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().agora();

         PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(28, 4, 2017));
        // var calendar = Calendar.getInstance();
        // calendar.set(Calendar.DAY_OF_MONTH, 28);
        // calendar.set(Calendar.MONTH, 4);
        // calendar.set(Calendar.YEAR, 2017);

        // PowerMockito.mockStatic(Calendar.class);
        // PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        var locacao = locacaoServico.alugarFilme(usuario, singletonList(filme));

        errorCollector.checkThat(locacao.getValor(), is(equalTo(4.0)));
        errorCollector.checkThat(locacao.getDataLocacao(), isDate(2017, 4, 28));
        errorCollector.checkThat(locacao.getDataRetorno(), isDate(2017, 4, 29));

        // PowerMockito.verifyStatic(Calendar.class, times(2));
        // Calendar.getInstance();
        TestOrder.appendFinish();
    }

    @Test(expected = FilmeSemEstoqueException.class) public void testeExceptionExpectedAnnotation() throws Exception {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().semEstoque().agora();

        locacaoServico.alugarFilme(usuario, singletonList(filme));
        TestOrder.appendFinish();
    }

    @Test public void testeExceptionTryCatch() throws LocadoraException {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().semEstoque().agora();

        try {
            locacaoServico.alugarFilme(usuario, singletonList(filme));
            fail("Deveria lançar uma exception");
        } catch (FilmeSemEstoqueException ignored) {
        }
        TestOrder.appendFinish();
    }

    @Test public void testeExceptionExpectedException() throws Exception {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().semEstoque().agora();

        expectedException.expect(FilmeSemEstoqueException.class);

        locacaoServico.alugarFilme(usuario, singletonList(filme));
        TestOrder.appendFinish();
    }

    @Test public void testeExceptionAssertThrows() {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filme = umFilme().semEstoque().agora();

        assertThrows("Deveria lançar uma exception", FilmeSemEstoqueException.class, () -> locacaoServico.alugarFilme(usuario, singletonList(filme)));
        TestOrder.appendFinish();
    }

    @Ignore @Test public void whenCurrentDayIsSaturdayReturnDateShouldBeMondayWithAssumption() throws FilmeSemEstoqueException, LocadoraException {
        TestOrder.appendInit();
        assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        var usuario = new Usuario("Usuário 1");
        var filmes = singletonList(umFilme().agora());

        var locacao = locacaoServico.alugarFilme(usuario, filmes);

        assertThat(locacao.getDataRetorno(), isMonday());
        TestOrder.appendFinish();
    }

    @Test public void whenCurrentDayIsSaturdayReturnDateShouldBeMonday() throws Exception {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filmes = singletonList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(29, 4, 2017));

        var locacao = locacaoServico.alugarFilme(usuario, filmes);

        assertThat(locacao.getDataRetorno(), isMonday());
        PowerMockito.verifyNew(Date.class, times(2)).withNoArguments();
        TestOrder.appendInit();
    }

    @Test public void shouldNotRentToIndebtedPerson() throws Exception {
        TestOrder.appendInit();
        var usuario = new Usuario("Usuário 1");
        var filmes = singletonList(umFilme().agora());

        when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        var thrown = assertThrows(LocadoraException.class, () -> locacaoServico.alugarFilme(usuario, filmes));

        assertThat(thrown.getMessage(), is("Usuário Negativado"));
        TestOrder.appendFinish();
    }

    @Test public void shouldNotifyLateReturns() {
        TestOrder.appendInit();
        var usuarioComLocacaoAtrasada = umUsuario().comNome("Usuário 1").agora();
        var usuario = umUsuario().comNome("Usuário 2").agora();
        var locacaoAtrasada = umLocacao().atrasada().comUsuario(usuarioComLocacaoAtrasada).agora();
        var locacao = umLocacao().comUsuario(usuario).agora();
        var locacoes = asList(locacaoAtrasada, locacao, locacaoAtrasada);

        when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);

        locacaoServico.notificarAtrasos();

        verify(emailService, atLeastOnce()).notificarAtraso(usuarioComLocacaoAtrasada);
        verify(emailService, never()).notificarAtraso(usuario);
        verifyNoMoreInteractions(emailService);
        TestOrder.appendFinish();
    }

    @Test public void shouldHandleSPCError() throws Exception {
        TestOrder.appendInit();
        var usuario = umUsuario().agora();
        var filmes = singletonList(umFilme().agora());

        when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha Catastrófica"));

        var thrown  = assertThrows(LocadoraException.class, () -> locacaoServico.alugarFilme(usuario, filmes));

        assertThat(thrown.getMessage(), is("Problemas com SPC, tente novamente"));
        TestOrder.appendFinish();
    }

    @Test public void shouldExtendReturnDate() {
        TestOrder.appendInit();
        var locacao = umLocacao().agora();

        locacaoServico.prorrogarLocacao(locacao, 3);

        var argCaptor = ArgumentCaptor.forClass(Locacao.class);
        verify(locacaoDAO).salvar(argCaptor.capture());
        var captedArg = argCaptor.getValue();

        errorCollector.checkThat(captedArg.getValor(), is(12.0));
        errorCollector.checkThat(captedArg.getDataLocacao(), isToday());
        errorCollector.checkThat(captedArg.getDataRetorno(), isTreeDaysFromToday());
        TestOrder.appendInit();
    }

    @Test public void shouldRentMovieWithoutCalculatingValue() throws Exception {
        TestOrder.appendInit();
        var usuario = umUsuario().agora();
        var filmes = singletonList(umFilme().agora());

        PowerMockito.doReturn(1.0).when(locacaoServico, "calcularValorLocacao", filmes);

        var locacao = locacaoServico.alugarFilme(usuario, filmes);

        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(locacaoServico).invoke("calcularValorLocacao", filmes);
        TestOrder.appendFinish();
    }

    @Test public void shouldCalculateRentValue() throws Exception {
        TestOrder.appendInit();
        var filmes = singletonList(umFilme().agora());

        var value = (double) Whitebox.invokeMethod(locacaoServico, "calcularValorLocacao", filmes);

        assertThat(value, is(4.0));
        TestOrder.appendFinish();
    }

}
