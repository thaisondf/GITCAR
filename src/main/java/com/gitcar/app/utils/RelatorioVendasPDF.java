package com.gitcar.app.utils;

import com.gitcar.app.dao.AgendamentoTestDriveDAO;
import com.gitcar.app.dao.EmpregadoDAO;
import com.gitcar.app.dao.VendaDAO;
import com.gitcar.app.models.AgendamentoTestDrive;
import com.gitcar.app.models.Empregado;
import com.gitcar.app.models.Venda;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioVendasPDF {

    private final VendaDAO vendaDAO;
    private final EmpregadoDAO empregadoDAO;
    private final AgendamentoTestDriveDAO testDriveDAO;
    private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RelatorioVendasPDF() {
        this.vendaDAO = new VendaDAO();
        this.empregadoDAO = new EmpregadoDAO();
        this.testDriveDAO = new AgendamentoTestDriveDAO();
    }

    public File gerarRelatorioPDF(LocalDate dataInicio, LocalDate dataFim) throws IOException {
        // Criar documento PDF
        PDDocument documento = new PDDocument();
        PDPage pagina = new PDPage(PDRectangle.A4);
        documento.addPage(pagina);

        // Buscar dados para o relatório
        List<Venda> vendas = vendaDAO.buscarVendasPorCriterio(dataInicio, dataFim, null, "");
        List<AgendamentoTestDrive> testDrives = buscarTestDrivesPorPeriodo(dataInicio, dataFim);
        
        // Calcular estatísticas
        List<Map.Entry<String, Integer>> topVendedores = calcularTopVendedores(vendas);
        Map.Entry<String, Integer> modeloMaisVendido = calcularModeloMaisVendido(vendas);
        Map.Entry<String, Integer> marcaMaisVendida = calcularMarcaMaisVendida(vendas);
        double faturamentoTotal = calcularFaturamentoTotal(vendas);
        int totalTestDrives = testDrives.size();

        // Gerar conteúdo do PDF
        try (PDPageContentStream contentStream = new PDPageContentStream(documento, pagina)) {
            // Título
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Relatório de Vendas - GIT CAR");
            contentStream.endText();

            // Período
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Período: " + dataInicio.format(formatadorData) + " até " + dataFim.format(formatadorData));
            contentStream.endText();

            // Data de geração
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, 710);
            contentStream.showText("Gerado em: " + LocalDate.now().format(formatadorData));
            contentStream.endText();

            // Linha separadora
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50, 700);
            contentStream.lineTo(550, 700);
            contentStream.stroke();

            // Top 3 vendedores
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 670);
            contentStream.showText("Top 3 Vendedores do Período");
            contentStream.endText();

            int yPosition = 650;
            for (int i = 0; i < Math.min(3, topVendedores.size()); i++) {
                Map.Entry<String, Integer> vendedor = topVendedores.get(i);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText((i + 1) + ". " + vendedor.getKey() + " - " + vendedor.getValue() + " vendas");
                contentStream.endText();
                yPosition -= 20;
            }

            // Modelo mais vendido
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, yPosition - 10);
            contentStream.showText("Modelo Mais Vendido");
            contentStream.endText();

            yPosition -= 30;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, yPosition);
            if (modeloMaisVendido != null) {
                contentStream.showText(modeloMaisVendido.getKey() + " - " + modeloMaisVendido.getValue() + " unidades");
            } else {
                contentStream.showText("Nenhum modelo vendido no período");
            }
            contentStream.endText();

            // Marca mais vendida
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, yPosition - 30);
            contentStream.showText("Marca Mais Vendida");
            contentStream.endText();

            yPosition -= 50;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, yPosition);
            if (marcaMaisVendida != null) {
                contentStream.showText(marcaMaisVendida.getKey() + " - " + marcaMaisVendida.getValue() + " unidades");
            } else {
                contentStream.showText("Nenhuma marca vendida no período");
            }
            contentStream.endText();

            // Faturamento total
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, yPosition - 30);
            contentStream.showText("Faturamento Total");
            contentStream.endText();

            yPosition -= 50;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(formatadorMoeda.format(faturamentoTotal));
            contentStream.endText();

            // Total de test drives
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, yPosition - 30);
            contentStream.showText("Total de Test Drives");
            contentStream.endText();

            yPosition -= 50;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(String.valueOf(totalTestDrives));
            contentStream.endText();

            // Resumo de vendas
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, yPosition - 30);
            contentStream.showText("Resumo de Vendas");
            contentStream.endText();

            yPosition -= 50;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total de veículos vendidos: " + vendas.size());
            contentStream.endText();

            // Rodapé
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
            contentStream.newLineAtOffset(50, 50);
            contentStream.showText("Relatório gerado automaticamente pelo sistema GIT CAR.");
            contentStream.endText();
        }

        // Salvar o documento
        File arquivoPDF = new File("relatorio_vendas_" + System.currentTimeMillis() + ".pdf");
        documento.save(arquivoPDF);
        documento.close();

        return arquivoPDF;
    }

    private List<Map.Entry<String, Integer>> calcularTopVendedores(List<Venda> vendas) {
        Map<String, Integer> vendasPorVendedor = new HashMap<>();
        
        for (Venda venda : vendas) {
            String nomeVendedor = venda.getNomeEmpregado();
            if (nomeVendedor != null && !nomeVendedor.isEmpty()) {
                vendasPorVendedor.put(nomeVendedor, vendasPorVendedor.getOrDefault(nomeVendedor, 0) + 1);
            }
        }
        
        return vendasPorVendedor.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    private Map.Entry<String, Integer> calcularModeloMaisVendido(List<Venda> vendas) {
        Map<String, Integer> vendasPorModelo = new HashMap<>();
        
        for (Venda venda : vendas) {
            String modelo = venda.getModeloVeiculo();
            if (modelo != null && !modelo.isEmpty()) {
                vendasPorModelo.put(modelo, vendasPorModelo.getOrDefault(modelo, 0) + 1);
            }
        }
        
        return vendasPorModelo.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .findFirst()
                .orElse(null);
    }

    private Map.Entry<String, Integer> calcularMarcaMaisVendida(List<Venda> vendas) {
        Map<String, Integer> vendasPorMarca = new HashMap<>();
        
        for (Venda venda : vendas) {
            String marca = venda.getMarcaVeiculo();
            if (marca != null && !marca.isEmpty()) {
                vendasPorMarca.put(marca, vendasPorMarca.getOrDefault(marca, 0) + 1);
            }
        }
        
        return vendasPorMarca.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .findFirst()
                .orElse(null);
    }

    private double calcularFaturamentoTotal(List<Venda> vendas) {
        return vendas.stream()
                .mapToDouble(Venda::getValorVenda)
                .sum();
    }

    private List<AgendamentoTestDrive> buscarTestDrivesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        // para buscar test drives por período
        List<AgendamentoTestDrive> todosTestDrives = testDriveDAO.buscarTodosAgendados();
        
        // Filtrar por período (simplificado)
        return todosTestDrives.stream()
                .filter(td -> {
                    try {
                        
                        String dataStr = td.getDataHoraTestDrive().split(" ")[0];
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate dataTestDrive = LocalDate.parse(dataStr, formatter);
                        
                        return !dataTestDrive.isBefore(dataInicio) && !dataTestDrive.isAfter(dataFim);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}

