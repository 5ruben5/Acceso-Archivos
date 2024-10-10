package AE01;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

/**
 * Clase principal que implementa una interfaz gráfica para buscar y reemplazar
 * texto en archivos dentro de una ruta especificada.
 */
public class AE01 {

	private JFrame frame;
	private JTextField textRuta;
	private JButton botoBuscar;
	private JTextField textBuscar;
	private JButton botoRemplazar;
	private JTextField textRemplazar;
	private JScrollPane scrollPane;

	private JCheckBox chckbxMayuscules;
	private JCheckBox chckbxAccents;

	/**
     * Lanza la aplicación.
     * 
     * @param args Argumentos de línea de comandos.
     */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AE01 window = new AE01();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Clase principal que implementa una interfaz gráfica para buscar y reemplazar
	 * texto en archivos dentro de una ruta especificada.
	 */
	public AE01() {
		initialize();
	}

	/**
     * Inicializa los contenidos del frame.
     */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1072, 606);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		frame.getContentPane().setBackground(new Color(30, 30, 30));

		scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 23, 992, 250);
		frame.getContentPane().add(scrollPane);

		JTextArea estructuraDirectoris = new JTextArea();
		estructuraDirectoris.setBackground(new Color(50, 50, 50));
		estructuraDirectoris.setForeground(Color.WHITE);
		estructuraDirectoris.setCaretColor(Color.WHITE);
		scrollPane.setViewportView(estructuraDirectoris);

		textRuta = new JTextField();
		textRuta.setBounds(23, 304, 857, 20);
		textRuta.setBackground(new Color(60, 60, 60));
		textRuta.setForeground(Color.WHITE);
		textRuta.setCaretColor(Color.WHITE);
		frame.getContentPane().add(textRuta);
		textRuta.setColumns(10);

		JButton botoRuta = new JButton("Ruta");
		botoRuta.setBounds(913, 303, 102, 23);
		botoRuta.setBackground(new Color(70, 70, 70));
		botoRuta.setForeground(Color.WHITE);
		botoRuta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ruta = textRuta.getText();
				if (!ruta.isEmpty()) {
					File file = new File(ruta);
					if (file.exists() && file.isDirectory()) {
						String estructura = mostrarEstructura(file, "", true);
						estructuraDirectoris.setText(estructura);
					} else {
						estructuraDirectoris.setText("Ruta no válida o no es un directorio.");
					}
				}
			}
		});
		frame.getContentPane().add(botoRuta);

		botoBuscar = new JButton("Buscar");
		botoBuscar.setBounds(913, 384, 102, 23);
		botoBuscar.setBackground(new Color(70, 70, 70));
		botoBuscar.setForeground(Color.WHITE);
		botoBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trobarCoincidencies(estructuraDirectoris);
			}
		});
		frame.getContentPane().add(botoBuscar);

		textBuscar = new JTextField();
		textBuscar.setBounds(23, 385, 857, 20);
		textBuscar.setBackground(new Color(60, 60, 60));
		textBuscar.setForeground(Color.WHITE);
		textBuscar.setCaretColor(Color.WHITE);
		frame.getContentPane().add(textBuscar);
		textBuscar.setColumns(10);

		botoRemplazar = new JButton("Remplazar");
		botoRemplazar.setBounds(913, 486, 102, 23);
		botoRemplazar.setBackground(new Color(70, 70, 70));
		botoRemplazar.setForeground(Color.WHITE);
		botoRemplazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reemplazarTexto(estructuraDirectoris);
			}
		});
		frame.getContentPane().add(botoRemplazar);

		textRemplazar = new JTextField();
		textRemplazar.setBounds(23, 487, 857, 20);
		textRemplazar.setBackground(new Color(60, 60, 60));
		textRemplazar.setForeground(Color.WHITE);
		textRemplazar.setCaretColor(Color.WHITE);
		frame.getContentPane().add(textRemplazar);
		textRemplazar.setColumns(10);

		chckbxMayuscules = new JCheckBox("Mayuscules");
		chckbxMayuscules.setBounds(913, 414, 97, 23);
		chckbxMayuscules.setBackground(new Color(30, 30, 30));
		chckbxMayuscules.setForeground(Color.WHITE);
		frame.getContentPane().add(chckbxMayuscules);

		chckbxAccents = new JCheckBox("Accents");
		chckbxAccents.setBounds(913, 440, 97, 23);
		chckbxAccents.setBackground(new Color(30, 30, 30));
		chckbxAccents.setForeground(Color.WHITE);
		frame.getContentPane().add(chckbxAccents);
	}

	
	/**
     * Función recursiva para mostrar la estructura de archivos y directorios.
     * 
     * @param dir       Directorio a inspeccionar.
     * @param indentacion Nivel de indentación para el formato.
     * @param esUltimo   Indica si es el último archivo/directorio en este nivel.
     * @return Estructura del directorio en formato de texto.
     */
	private String mostrarEstructura(File dir, String indentacion, boolean esUltimo) {
		StringBuilder sb = new StringBuilder();
		File[] arxiu = dir.listFiles();

		if (arxiu != null && arxiu.length > 0) {
			StringBuilder contingutDirectoris = new StringBuilder(); // Para almacenar contenido de subdirectorios
			for (int i = 0; i < arxiu.length; i++) {
				File archivo = arxiu[i];
				boolean esUltimArxiu = (i == arxiu.length - 1); // Determinar si es el último archivo en este nivel
				String prefix = esUltimArxiu ? "└── " : "├── ";

				if (archivo.isDirectory()) {
					String novaIndentacion = indentacion + (esUltimArxiu ? "    " : "|   ");

					String subEstructura = mostrarEstructura(archivo, novaIndentacion, esUltimArxiu);

					if (!subEstructura.isEmpty()) {
						contingutDirectoris.append(indentacion).append(prefix).append(archivo.getName()).append("\n");
						contingutDirectoris.append(subEstructura);
					}
				} else {
					// Mostrar el archivo con formato: nombre (tamaño – fecha)
					String detallsArxiu = formatarDetallsArxiu(archivo);
					sb.append(indentacion).append(prefix).append(archivo.getName()).append(detallsArxiu).append("\n");
				}
			}

			// Añadir el contenido de los subdirectorios si hay algo que mostrar
			sb.append(contingutDirectoris);
		}
		return sb.toString();
	}

	 /**
     * Formatea los detalles de un archivo (tamaño y fecha de modificación).
     * 
     * @param arxiu Archivo cuyo detalle se desea formatear.
     * @return Cadena formateada con el tamaño en KB y la fecha de modificación.
     */
	private String formatarDetallsArxiu(File arxiu) {
		long tamanyo = arxiu.length(); // Tamaño del archivo en bytes
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dataModificacion = sdf.format(new Date(arxiu.lastModified()));

		// Convertir tamaño a KB
		double tamanyoKB = tamanyo / 1024.0;
		return String.format(" (%.1f KB – %s)", tamanyoKB, dataModificacion);

	}

	
	/**
     * Busca coincidencias del texto especificado en los archivos del directorio.
     * 
     * @param estructuraDirectoris Área de texto donde se mostrará el resultado.
     */
	private void trobarCoincidencies(JTextArea estructuraDirectoris) {
		String ruta = textRuta.getText();
		String textTrobar = textBuscar.getText();
		StringBuilder resultats = new StringBuilder();

		File file = new File(ruta);
		if (file.exists() && file.isDirectory()) {
			trobarEnArxius(file, textTrobar, resultats, "");
		} else {
			resultats.append("Ruta no válida o no es un directorio.");
		}

		estructuraDirectoris.setText(resultats.toString());
	}

	
	/**
	 * Busca coincidencias del texto en archivos dentro del directorio especificado
	 * y sus subdirectorios, generando un informe con el número de coincidencias en
	 * cada archivo.
	 * 
	 * @param dir        Directorio raíz en el cual se inicia la búsqueda.
	 * @param textTrobar Texto que se desea buscar en los archivos.
	 * @param resultats  StringBuilder que contiene los resultados de la búsqueda.
	 * @param indentacion Nivel de indentación para estructurar visualmente el árbol
	 *                    de directorios.
	 */
	private void trobarEnArxius(File dir, String textTrobar, StringBuilder resultats, String indentacion) {
		File[] arxius = dir.listFiles();

		if (arxius != null) {
			for (int i = 0; i < arxius.length; i++) {
				File archivo = arxius[i];
				boolean esUltimArxiu = (i == arxius.length - 1);
				String prefijo = esUltimArxiu ? "└── " : "├── ";

				if (archivo.isDirectory()) {
					StringBuilder subResultats = new StringBuilder();
					trobarEnArxius(archivo, textTrobar, subResultats, indentacion + (esUltimArxiu ? "    " : "|   "));

					if (subResultats.length() > 0) {
						resultats.append(indentacion).append(prefijo).append(archivo.getName()).append("\n");
						resultats.append(subResultats);
					}
				} else {
					int coincidencias = comptarCoincidencies(archivo, textTrobar);
					resultats.append(indentacion).append(prefijo).append(archivo.getName()).append(" (")
							.append(coincidencias).append(" coincidències)\n");
				}
			}
		}
	}

	
	/**
	 * Cuenta el número de coincidencias de un texto específico dentro de un archivo.
	 * 
	 * @param arxiu    Archivo en el cual se buscarán las coincidencias.
	 * @param textTrobar Texto que se desea buscar dentro del archivo.
	 * @return El número de coincidencias encontradas en el archivo.
	 */
	private int comptarCoincidencies(File arxiu, String textTrobar) {
		if (!arxiu.isFile() || !arxiu.canRead()) {
			return 0;
		}

		int coincidencies = 0;

		// Si es un archivo PDF, contamos coincidencias en el contenido de texto
		if (arxiu.getName().endsWith(".pdf")) {
			coincidencies = comptarCoincidenciesInPDF(arxiu, textTrobar);
		} else {
			// Intentar leer el archivo como texto
			try (BufferedReader reader = new BufferedReader(new FileReader(arxiu))) {
				String line;
				while ((line = reader.readLine()) != null) {
					coincidencies += comptarEnLinea(line, textTrobar);
				}
			} catch (IOException e) {
				// Si ocurre una excepción, se asume 0 coincidencias
				return 0;
			}
		}

		return coincidencies;
	}
	
	
	/**
	 * Cuenta el número de coincidencias de un texto en un archivo PDF.
	 *
	 * @param arxiu      Archivo PDF en el cual se buscarán coincidencias.
	 * @param textTrobar Texto que se desea buscar en el PDF.
	 * @return Número de coincidencias encontradas en el archivo PDF.
	 */
	private int comptarCoincidenciesInPDF(File arxiu, String textTrobar) {
		int coincidencies = 0;

		try (PDDocument document = PDDocument.load(arxiu)) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);

			// Contamos coincidencias en el texto extraído del PDF
			coincidencies = comptarEnLinea(text, textTrobar);
		} catch (IOException e) {
			return 0;
		}

		return coincidencies;
	}

	/**
	 * Cuenta las coincidencias de un texto en una línea, con la opción de ignorar
	 * acentos y mayúsculas.
	 *
	 * @param linea      Texto en el que se buscarán las coincidencias.
	 * @param textTrobar Texto que se busca dentro de la línea.
	 * @return El número de coincidencias encontradas en la línea.
	 */
	private int comptarEnLinea(String linea, String textTrobar) {
		if (!chckbxAccents.isSelected()) {
			linea = eliminarAccents(linea);
			textTrobar = eliminarAccents(textTrobar);
		}

		if (!chckbxMayuscules.isSelected()) {
			linea = linea.toLowerCase();
			textTrobar = textTrobar.toLowerCase();
		}

		int index = 0;
		int coincidencies = 0;

		while ((index = linea.indexOf(textTrobar, index)) != -1) {
			coincidencies++;
			index += textTrobar.length();
		}

		return coincidencies;
	}
	
	/**
	 * Elimina acentos y diacríticos de un texto.
	 *
	 * @param text Texto del cual se eliminarán los acentos.
	 * @return El texto sin acentos.
	 */

	private String eliminarAccents(String text) {
	    // Creamos un StringBuilder para almacenar el texto sin acentos
	    StringBuilder result = new StringBuilder();
	    
	    // Recorremos cada carácter del texto original
	    for (char c : text.toCharArray()) {
	        // Comprobamos si el carácter es un acento
	        if (c == 'á' || c == 'à') {
	            result.append('a'); // Reemplazamos por 'a' sin acento
	        } else if (c == 'é' || c == 'è') {
	            result.append('e'); // Reemplazamos por 'e' sin acento
	        } else if (c == 'í' || c == 'ì') {
	            result.append('i'); // Reemplazamos por 'i' sin acento
	        } else if (c == 'ó' || c == 'ò') {
	            result.append('o'); // Reemplazamos por 'o' sin acento
	        } else if (c == 'ú' || c == 'ù') {
	            result.append('u'); // Reemplazamos por 'u' sin acento
	        } else {
	            result.append(c); // Mantenemos el carácter original si no es acentuado
	        }
	    }
	    
	    // Convertimos el StringBuilder a String y lo devolvemos
	    return result.toString();
	}

	
	/**
	 * Reemplaza un texto en los archivos de un directorio y genera un informe de los
	 * cambios realizados.
	 *
	 * @param estructuraDirectoris Área de texto donde se mostrarán los resultados de la operación.
	 */
	private void reemplazarTexto(JTextArea estructuraDirectoris) {
		String ruta = textRuta.getText();
		String textTrobar = textBuscar.getText();
		String textReemplazar = textRemplazar.getText();
		StringBuilder resultats = new StringBuilder();

		File file = new File(ruta);
		if (file.exists() && file.isDirectory()) {
			reemplazarEnArxius(file, textTrobar, textReemplazar, resultats, "");
		} else {
			resultats.append("Ruta no válida o no es un directorio.");
		}

		estructuraDirectoris.setText(resultats.toString());
	}

	
	/**
	 * Reemplaza el texto en todos los archivos de un directorio, incluidos sus subdirectorios.
	 *
	 * @param dir             Directorio en el cual se realiza el reemplazo de texto.
	 * @param textoBuscar     Texto que se desea buscar y reemplazar.
	 * @param textoReemplazar Texto que reemplazará al texto encontrado.
	 * @param resultats       StringBuilder que acumula los resultados de la operación.
	 * @param indentacion     Cadena utilizada para estructurar la salida jerárquicamente.
	 */
	private void reemplazarEnArxius(File dir, String textoBuscar, String textoReemplazar, StringBuilder resultats,
			String indentacion) {
		File[] archivos = dir.listFiles();

		if (archivos != null) {
			for (int i = 0; i < archivos.length; i++) {
				File archivo = archivos[i];
				boolean esUltimoArchivo = (i == archivos.length - 1);
				String prefijo = esUltimoArchivo ? "└── " : "├── ";

				if (archivo.isDirectory()) {
					StringBuilder subResultats = new StringBuilder();
					reemplazarEnArxius(archivo, textoBuscar, textoReemplazar, subResultats,
							indentacion + (esUltimoArchivo ? "    " : "|   "));

					if (subResultats.length() > 0) {
						resultats.append(indentacion).append(prefijo).append(archivo.getName()).append("\n");
						resultats.append(subResultats);
					}
				} else {
					int reemplazos = realizarReemplazo(archivo, textoBuscar, textoReemplazar);
					if (reemplazos > 0) {
						resultats.append(indentacion).append(prefijo).append(archivo.getName()).append(" (")
								.append(reemplazos).append(" Reemplaçaments)\n");
					} else {
						resultats.append(indentacion).append(prefijo).append(archivo.getName())
								.append(" (Sense canvis)\n");
					}
				}
			}
		}
	}

	
	/**
	 * Realiza el reemplazo de texto en un archivo de texto.
	 *
	 * @param archivo         Archivo en el que se realizará el reemplazo.
	 * @param textoBuscar     Texto que se desea buscar.
	 * @param textoReemplazar Texto que reemplazará al texto encontrado.
	 * @return Número de reemplazos realizados en el archivo.
	 */
	private int realizarReemplazo(File archivo, String textoBuscar, String textoReemplazar) {
		if (!archivo.isFile() || !archivo.canRead() || archivo.getName().endsWith(".pdf")) {
			return 0;
		}

		int reemplazos = 0;
		StringBuilder contingutOriginal = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Contar reemplazos en la línea actual
				int coincidencies = comptarEnLinea(line, textoBuscar);
				reemplazos += coincidencies;
				// Reemplazar el texto
				line = line.replace(textoBuscar, textoReemplazar);
				contingutOriginal.append(line).append(System.lineSeparator());
			}
		} catch (IOException e) {
			return 0;
		}

		// Crear nuevo archivo solo si hubo reemplazos
		if (reemplazos > 0) {
			String nouNom = "MOD_" + archivo.getName();
			File nouArxiu = new File(archivo.getParent(), nouNom);

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(nouArxiu))) {
				writer.write(contingutOriginal.toString());
			} catch (IOException e) {
				return 0;
			}
		}

		return reemplazos;
	}
}
