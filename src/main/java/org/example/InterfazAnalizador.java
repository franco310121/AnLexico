package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.List;

public class InterfazAnalizador extends JFrame {
    private JTextArea areaEntrada;
    private JTextArea areaNumerosLineas;  // Para los números de línea
    private JTextPane areaSalida;
    private JButton botonAnalizar;

    private static final String RUTA_TABLA_TOKENS = "tabla_tokens.xlsx";

    public InterfazAnalizador() {
        setTitle("Analizador Léxico");
        setSize(800, 600);  // Aumentamos el tamaño de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#f4f4f4"));

        // Intentar cargar la tabla de tokens
        boolean cargaExitosa = AnalizadorLexico.cargarTabla(RUTA_TABLA_TOKENS);
        if (!cargaExitosa) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la tabla de tokens desde:\n" + RUTA_TABLA_TOKENS,
                    "Error de carga", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Panel izquierdo para el botón
        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        panelIzquierdo.setBackground(Color.decode("#f4f4f4"));
        panelIzquierdo.setBorder(new EmptyBorder(10, 10, 10, 10));

        botonAnalizar = new JButton("Analizar");
        botonAnalizar.setFocusPainted(false);
        botonAnalizar.setForeground(Color.WHITE);
        botonAnalizar.setBackground(new Color(159, 172, 196));
        botonAnalizar.setFont(new Font("SansSerif", Font.BOLD, 14));
        botonAnalizar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        botonAnalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonAnalizar.addActionListener(e -> analizar());

        panelIzquierdo.add(botonAnalizar, new GridBagConstraints());
        add(panelIzquierdo, BorderLayout.WEST);

        // Panel central para las áreas de texto
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.decode("#f4f4f4"));
        panelCentral.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Crear área de texto para entrada
        JPanel panelEntrada = new JPanel(new BorderLayout());
        areaEntrada = crearTextArea();
        JScrollPane scrollEntrada = new JScrollPane(areaEntrada);
        scrollEntrada.setBorder(BorderFactory.createTitledBorder("Entrada"));

        // Crear área de texto para numeración de líneas
        areaNumerosLineas = new JTextArea();
        areaNumerosLineas.setEditable(false);
        areaNumerosLineas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaNumerosLineas.setBackground(Color.LIGHT_GRAY);

        // Ajustar el margen superior para la numeración de líneas (10 píxeles más abajo)
        areaNumerosLineas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 8, 8, 8) // 10 píxeles de margen superior
        ));

        JScrollPane scrollNumerosLineas = new JScrollPane(areaNumerosLineas);
        scrollNumerosLineas.setBorder(BorderFactory.createEmptyBorder());
        scrollNumerosLineas.setPreferredSize(new Dimension(50, 0)); // Espacio fijo para los números

        // Ocultar las barras de desplazamiento en el área de numeración de líneas
        scrollNumerosLineas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollNumerosLineas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelEntrada.add(scrollNumerosLineas, BorderLayout.WEST);
        panelEntrada.add(scrollEntrada, BorderLayout.CENTER);

        // Panel para salida (resultados)
        areaSalida = new JTextPane();
        areaSalida.setEditable(false);
        areaSalida.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaSalida.setBackground(Color.WHITE);
        JScrollPane scrollSalida = new JScrollPane(areaSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida"));
        scrollSalida.setPreferredSize(new Dimension(0, 200)); // Ajuste del tamaño de la salida

        // Sincronizar las barras de desplazamiento
        JScrollPane finalScrollNumerosLineas = scrollNumerosLineas;
        JScrollPane finalScrollSalida = scrollSalida;

        scrollEntrada.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                finalScrollNumerosLineas.getVerticalScrollBar().setValue(e.getValue());
                finalScrollSalida.getVerticalScrollBar().setValue(e.getValue());
            }
        });

        panelCentral.add(panelEntrada, BorderLayout.CENTER);
        panelCentral.add(scrollSalida, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // Actualizar números de línea al cambiar el tamaño de la ventana
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                actualizarNumerosLineas();
            }
        });
    }

    private JTextArea crearTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(8, 8, 8, 8)
        ));
        area.setBackground(Color.WHITE);
        area.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumerosLineas();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumerosLineas();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarNumerosLineas();
            }
        });
        return area;
    }

    private void actualizarNumerosLineas() {
        String texto = areaEntrada.getText();
        String[] lineas = texto.split("\n");
        StringBuilder numeros = new StringBuilder();
        for (int i = 1; i <= lineas.length; i++) {
            numeros.append(i).append("\n");
        }
        areaNumerosLineas.setText(numeros.toString());
    }

    private void analizar() {
        String codigo = areaEntrada.getText();
        List<TokenDetectado> tokens = AnalizadorLexico.analizarCodigo(codigo);

        StyledDocument doc = areaSalida.getStyledDocument();

        // Limpiar contenido anterior correctamente
        areaSalida.setText("");

        // Crear estilos
        Style estiloCorrecto = areaSalida.addStyle("correcto", null);
        StyleConstants.setForeground(estiloCorrecto, new Color(46, 125, 50)); // Verde oscuro

        Style estiloError = areaSalida.addStyle("error", null);
        StyleConstants.setForeground(estiloError, new Color(198, 40, 40)); // Rojo oscuro

        int errores = 0;
        TokenDetectado primerError = null;

        // Recorrer los tokens detectados
        for (TokenDetectado t : tokens) {
            String linea = String.format("[%s, %s] (Línea %d, Columna %d)\n",
                    t.tipo, t.lexema, t.linea, t.columna);
            Style estilo = "ERROR".equals(t.tipo) ? estiloError : estiloCorrecto;
            try {
                doc.insertString(doc.getLength(), linea, estilo);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            if ("ERROR".equals(t.tipo)) {
                errores++;
                if (primerError == null) primerError = t;
            }
        }

        // Mostrar mensaje dependiendo de si hay errores o no
        if (errores == 0) {
            JOptionPane.showMessageDialog(this, "Análisis completado con éxito. No se encontraron errores.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.format("Se encontraron %d errores. Primer error en línea %d, columna %d: \"%s\"",
                            errores, primerError.linea, primerError.columna, primerError.lexema),
                    "Errores encontrados", JOptionPane.ERROR_MESSAGE);
        }

        // Actualizar el título con la cantidad de tokens y errores
        setTitle(String.format("Analizador Léxico - %d tokens, %d errores", tokens.size(), errores));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazAnalizador().setVisible(true));
    }
}
