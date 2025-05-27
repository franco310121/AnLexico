package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class InterfazAnalizador extends JFrame {
    private JTextArea areaEntrada;
    private JTextPane areaSalida; // Cambiado
    private JButton botonAnalizar;

    private static final String RUTA_TABLA_TOKENS = "tabla_tokens.xlsx";

    public InterfazAnalizador() {
        setTitle("Analizador Léxico");
        setSize(720, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.decode("#f4f4f4"));

        boolean cargaExitosa = AnalizadorLexico.cargarTabla(RUTA_TABLA_TOKENS);
        if (!cargaExitosa) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la tabla de tokens desde:\n" + RUTA_TABLA_TOKENS,
                    "Error de carga", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

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

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setBackground(Color.decode("#f4f4f4"));
        panelCentral.setBorder(new EmptyBorder(10, 10, 10, 10));

        areaEntrada = crearTextArea();
        JScrollPane scrollEntrada = new JScrollPane(areaEntrada);
        scrollEntrada.setBorder(BorderFactory.createTitledBorder("Entrada"));

        areaSalida = new JTextPane(); // JTextPane
        areaSalida.setEditable(false);
        areaSalida.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaSalida.setBackground(Color.WHITE);
        JScrollPane scrollSalida = new JScrollPane(areaSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida"));

        panelCentral.add(scrollEntrada);
        panelCentral.add(scrollSalida);

        add(panelCentral, BorderLayout.CENTER);
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
        return area;
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

        if (errores == 0) {
            JOptionPane.showMessageDialog(this, "Análisis completado con éxito. No se encontraron errores.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    String.format("Se encontraron %d errores. Primer error en línea %d, columna %d: \"%s\"",
                            errores, primerError.linea, primerError.columna, primerError.lexema),
                    "Errores encontrados", JOptionPane.ERROR_MESSAGE);
        }

        setTitle(String.format("Analizador Léxico - %d tokens, %d errores", tokens.size(), errores));
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazAnalizador().setVisible(true));
    }
}
