package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.service.ConsultaTradutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private final SerieRepository repositorio;
    private final ConsultaTradutor tradutor;


    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    @Autowired
    public Principal(SerieRepository repositorio, ConsultaTradutor tradutor) {
        this.repositorio = repositorio;
        this.tradutor = tradutor;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar séries por titulo
                    5 - Buscar séries por Ator
                    6 - Top 5 séries
                    7 - Buscar séries por categoria
                    8 - Filtrar séries
                    9 - Buscar episódio por trecho
                    10 - Melhores episódios por série (top 5)
                    11 - Buscar episódios a partir de uma data
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> listarSeriesBuscadas();
                case 4 -> buscarSeriePorTitulo();
                case 5 -> buscarSeriesPorAtor();
                case 6 -> buscarTop5Series();
                case 7 -> buscarSeriesPorCategoria();
                case 8 -> filtrarSeriesPorTemporadaEAvaliacao();
                case 9 -> buscarEpisodioPorTrecho();
                case 10 -> topEpisodiosPorSerie();
                case 11 -> buscarEpisodiosPorData();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida");
            }
        }
    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();

        Optional<Serie> serieExistente = repositorio.findByTituloIgnoreCase(dados.titulo());

        if (serieExistente.isPresent()) {
            System.out.println("Série já cadastrada no banco: " + dados.titulo());
            return;
        }

        Serie serie = new Serie(dados);
        String sinopseTraduzida = tradutor.obterTraducao(dados.sinopse());
        serie.setSinopse(sinopseTraduzida);

        repositorio.save(serie);
        System.out.println("Série cadastrada com sucesso!");
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        String nomeSerie = leitura.nextLine();
        String json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Ops! Série não encontrada x_x");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome para buscar? ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
            System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar série de qual gênero? ");
        var nomeGenero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Série do gênero " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

        private void buscarEpisodioPorTrecho(){
            System.out.println("Qual o trecho do episódio para busca?");
            var trechoEpisodio = leitura.nextLine();
            List<Episodio> episodioEncotrados = repositorio.episodioPorTrecho(trechoEpisodio);
            episodioEncotrados.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episodio %s - %s Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
       Serie serie = serieBusca.get();
        List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
        topEpisodios.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
    }
    }
    private void buscarEpisodiosPorData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite do lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}
