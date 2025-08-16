package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.models.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();


    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=9797c560";
    private List<DadosSerie> dadosSeries = new ArrayList<>();


    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1- Buscar series
                    2- Buscar episodios
                    3- Listar Series buscadas
                    4- Buscar serie por titulo
                    5- Buscar series por ator
                    6- Top 5 Series
                    7- Pesquisa por Genero
                    8- Pesquisa quantidade de temporada
                    9- Pesquisa por trecho de episodio
                    10- Busca top 5 Episodeos de uma serie
                    11- Buscar episodeos a partir de uma data
                    
                    0- Sair
                    """;

            System.out.println(menu);

            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSerieBuscada();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    BuscarQuantidadeTemporada();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodeosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisUmaData();
                    break;

                case 0:
                    System.out.println("Saindo....");
                    break;
                default:
                    System.out.println("Opcao invalida");
            }
        }
    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
//        dadosSeries.add(dados);
          repositorio.save(serie);

        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da serie para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO+ nomeSerie.replace(" ", "+")+API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSerieBuscada();
        System.out.println("Escolha uma serie pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();


            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
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
            System.out.println("Serie nao encontrada");
        }
    }

    private void listarSerieBuscada() {

        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriePorTitulo() {

        System.out.println("Escolha uma serie pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()){
            System.out.println("Dados da serie: "+ serieBusca.get());

        }else {
            System.out.println("Serie nao encontrada");
        }
    }

    private void buscarSeriePorAtor(){
        System.out.println("Qual o nome do autor?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliacoes a partir que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que "+ nomeAtor+ " trabalhou");

        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo()+" avalicacao: "+ s.getAvaliacao()));
    }

    private void buscarTop5Series(){
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo()+" avalicacao: "+ s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Deseja busca serie de qual categoria/genero");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Serie da categoria "+ nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void BuscarQuantidadeTemporada(){
        System.out.println("Digite a quantidade minima de temporada?");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Digita a avaliacao minima");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriePesquisado = repositorio.seriePorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        seriePesquisado.forEach(s ->
                System.out.println(s.getTitulo()+" avalicacao: "+ s.getAvaliacao()));

    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual o nome do episodio?");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);

        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));

    }
    private void topEpisodeosPorSerie(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodeosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));

        }
    }
    private void buscarEpisodiosDepoisUmaData(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lancamento ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorTrecho(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}





















//        System.out.println("Digite o nome da serie?");
//        var nomeSerie = leitura.nextLine();
//        var consumoApi = new ConsumoApi();
//        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
//        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//
//        System.out.println(dados);
//
//
//		List<DadosTemporada> temporadas = new ArrayList<>();
//
//		for (int i = 1; i<= dados.totalTemporadas(); i++){
//			json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+")+"&season="+ i + API_KEY);
//			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
//			temporadas.add(dadosTemporada);
//
//		}
//
//		temporadas.forEach(System.out::println);
//
////        for (int i = 0; i< dados.totalTemporadas(); i++){
////            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
////            for (int j= 0; j < episodiosTemporada.size(); j++){
////                System.out.println(episodiosTemporada.get(j).titulo());
////            }
////        }
//
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
////        List<String> nomes = Arrays.asList("Leo", "Manu", "Juse", "nico");
////        nomes.stream()
////                .sorted()
////                .limit(3)
////                .filter(n -> n.startsWith("N"))
////                .map(n -> n.toUpperCase())
////                .forEach(System.out::println);
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
////                .toList(); <- esse e a lista imudavel
////        System.out.println("\nTop 10 Episodios");
////        dadosEpisodios.stream()
////                .filter(e -> !e.avalia().equalsIgnoreCase("N/A"))
////                .peek(e -> System.out.println("Primeiro filtro(N/A)" + e))
////                .sorted(Comparator.comparing(DadosEpisodio::avalia).reversed())
////                .peek(e -> System.out.println("Ordenacao "+ e))
////                .limit(10)
////                .peek(e -> System.out.println("Limite " + e))
////                 .map(e -> e.titulo().toUpperCase())
////                .peek(e -> System.out.println("Mapeamento "+e))
////                .forEach(System.out::println);
//
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodio(t.numero(), d))
//                ).collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
////        System.out.println("Digite um trecho do titulo do episodio");
////        var trechoTitulo = leitura.nextLine();
////        Optional<Episodio> episodioBuscado = episodios.stream()
////                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
////                .findFirst();
////
////        if (episodioBuscado.isPresent()){
////            System.out.println("Episodio encontrado!");
////            System.out.println("Temporada" + episodioBuscado.get().getTemporada());
////        }else {
////            System.out.println("Epsodio nao encontrado");
////        }
//
////        System.out.println("A partir que ano voce deseja ver os episodios");
////        var ano = leitura.nextInt();
////        leitura.nextLine();
////
////        LocalDate dataBusca = LocalDate.of(ano, 1,1);
////
////        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
////
////        episodios.stream()
////                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
////                .forEach(e -> System.out.println(
////                        "Temporada: "+ e.getTemporada() +
////                        "Episodio: "+ e.getTitulo() +
////                        "Data Lancamento" + e.getDataLancamento().format(formatador)));
//
//
//
////        dadosEpisodios.add(new DadosEpisodio("Teste", 3, "10", "2020-01-01"));
////        dadosEpisodios.forEach(System.out::println);
//
//
//         Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
//                 .filter(e -> e.getAvaliacao() > 0.0)
//                 .collect(Collectors.groupingBy(Episodio::getTemporada,
//                         Collectors.averagingDouble(Episodio::getAvaliacao)));
//
//        System.out.println(avaliacoesPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println("Media: "+ est.getAverage());
//        System.out.println("Melhor episodio: " + est.getMax());
//        System.out.println("Pior episodio: " + est.getMin());
//        System.out.println("Quantos episodios total: "+ est.getCount());
//
//
//
//
//
//        //https://www.omdbapi.com/?t=gilmore+girls&season="+ i +"&apikey=9797c560
//    }
//}
