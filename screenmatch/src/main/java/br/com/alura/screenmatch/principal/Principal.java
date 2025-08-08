package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.models.DadosSerie;
import br.com.alura.screenmatch.models.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();


    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=9797c560";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1- Buscar series
                    2- Buscar episodios
                    3- Listar Series buscadas
                    
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
        dadosSeries.add(dados);
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
       DadosSerie dadosSerie = getDadosSerie();
       List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO+ dadosSerie.titulo().replace(" ", "+")+ "&season="+ i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

    private void listarSerieBuscada() {
        dadosSeries.forEach(System.out::println);
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
