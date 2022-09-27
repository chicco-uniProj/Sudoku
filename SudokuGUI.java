package poo.sudoku;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



//impostare celle e verificare se ci possono stare
//salvare celle impostate
//tasti per andare avanti e indietro tra le soluzioni
@SuppressWarnings("serial")
class FinestraGUI extends JFrame
{	private Sudoku s=new Sudoku();
	private File fileDiSalvataggio=null;
	private String titolo="Sudoku";
	private JMenuItem apri,salva,esci,about;
	JButton bottone=new JButton("RISOLVI NUOVO SUDOKU");
	private ArrayList<Integer>daSalvare=new ArrayList<>(); //sarebbero le celle impostate
	
	private JFrame FS;
	private JFrame FSoluz;
	
	public FinestraGUI()
	{	setTitle(titolo);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(consensoUscita())
					System.exit(0);
			}
			});
		AscoltatoreEventiAzione listener=new AscoltatoreEventiAzione();
		
		JMenuBar menuBar=new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu menuFile=new JMenu("File");
		menuBar.add(menuFile);
		//voci del menu --FILE

		menuFile.addSeparator();
		apri=new JMenuItem("Apri");
		apri.addActionListener(listener);
		menuFile.add(apri);
		salva=new JMenuItem("Salva");
		salva.addActionListener(listener);
		menuFile.add(salva);
		menuFile.addSeparator();
        esci=new JMenuItem("Esci");
        esci.addActionListener(listener);
        menuFile.add(esci);

        //menu Help
        JMenu menuHelp=new JMenu("Help");
        menuBar.add(menuHelp);
        about=new JMenuItem("About");
        about.addActionListener(listener);
        menuHelp.add(about);
        

        JTextArea benvenuto=new JTextArea("Benevenuto nel risolutore del Sudoku");
        benvenuto.setEditable(false);
        add(benvenuto,BorderLayout.NORTH);
        
        bottone.addActionListener(listener);
        add(bottone,BorderLayout.CENTER);
        
        menuIniziale();
        pack();
        setLocation(500,200);
        setSize(500,200);   
	}

	private class FrameSudoku extends JFrame implements ActionListener
	{
		private JPanel celle=new JPanel();
		private JButton ok=new JButton("Verifica e risolvi");
		private ArrayList<JTextField>jtf=new ArrayList<JTextField>();
		
		public FrameSudoku()
		{	setTitle("Sudoku");
			setLocation(500,200);
			setSize(500,500);   
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			
			celle.setLayout(new GridLayout(9,9,1,1));
			for(int i=0;i<81;i++)
			{	JTextField j=new JTextField();
				if(daSalvare.size()>0)			// mi serve quando devo ripristinare un sudoku
					j.setText(daSalvare.get(i)+"");
				
				jtf.add(j);
				celle.add(j);
			}
			ok.addActionListener(this);
			add(celle);
			add(ok,BorderLayout.SOUTH);
			   
  		}


		@Override
		public void actionPerformed(ActionEvent e)
		{	if(e.getSource()==ok)
			{	boolean flag=true;
				s=new Sudoku(); //lo ricreo se no quando clicco il bottone i valori si sovrappongono e da errore
				for(int i=0;i<81;i++)
				{	JTextField j=jtf.get(i);
					String a=j.getText();
					if(a.matches("[1-9]"))
					{	int n=Integer.parseInt(a);
						s.imposta(i, n);
					}
					else if(a.matches("")||a.matches("0"))
						continue;
					else
					{	JOptionPane.showMessageDialog(null, "Cella in posizione <"+i/9+","+i%9+"> non valida, reinserire");
						flag=false;
					}
				}
				if(flag)
				{	s.risolvi();
					daSalvare.addAll(s.getImpostate());
				
					FSoluz=new Soluzioni();
					FSoluz.setVisible(true);
				}
					
			}
		}//actionPerformed
	}//Frame sudoku
	private class Soluzioni extends JFrame implements ActionListener
	{	private List<Integer[][]> sol=s.getSoluzioni();
		private JButton prossima=new JButton("Next");
		private JButton precedente=new JButton("Prev");
		
		private int indiceSol=0;
		
		private JPanel celle=new JPanel();
		private ArrayList<JTextField>jtf=new ArrayList<JTextField>();

		
		
		public Soluzioni()
		{	setTitle("Soluzioni");
			setLocation(500,200);
			setSize(570,500);   
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			for(int i=0;i<81;i++)
				jtf.add(new JTextField());
			
			celle.setLayout(new GridLayout(9,9,1,1));
			if(sol.size()>0)
			{	scriviSoluzione(indiceSol, celle, sol);
			}
			else
				add(new JTextField("Nessuna soluzione disponibile"));
			
			prossima.addActionListener(this);
			precedente.addActionListener(this);
			add(celle);
			add(prossima,BorderLayout.AFTER_LINE_ENDS);
			add(precedente,BorderLayout.BEFORE_LINE_BEGINS);
			
		}
		public void scriviSoluzione(int indiceSol,JPanel p,List<Integer[][]> sol)
		{	
			Integer[][]soluzione=sol.get(indiceSol);
			for(int i=0;i<81;i++)
			{	int x=i/9,y=i%9;
				JTextField j=jtf.get(i);
				j.setText(soluzione[x][y]+"");
				celle.add(j);				
			}
			add(celle);
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{	if(e.getSource()==prossima)
			{	indiceSol++;
				if(sol.size()>indiceSol)	
				{	scriviSoluzione(indiceSol,celle,sol);
				}
				else
					JOptionPane.showMessageDialog(null, "Soluzione finite");
			}
			else if(e.getSource()==precedente)
			{	indiceSol--;
				if(indiceSol>=0)
				{	scriviSoluzione(indiceSol,celle,sol);
				}
				else
					JOptionPane.showMessageDialog(null, "Questa è la prima soluzione");
					
			}
		}
		
	}
	
	

	private void menuIniziale()
	{	apri.setEnabled(true);
		salva.setEnabled(false);
	}
	
	private boolean consensoUscita(){
	   int option=JOptionPane.showConfirmDialog(
			   null, "Continuare ?", "Uscendo si perderanno tutti i dati!",
			   JOptionPane.YES_NO_OPTION);
	   return option==JOptionPane.YES_OPTION;
	 }//consensoUscita
	
	private class AscoltatoreEventiAzione implements ActionListener
	{	@Override
		public void actionPerformed(ActionEvent e) 
		{	if(e.getSource()==esci)
			{	if (consensoUscita());
					System.exit(0);
			}

			else if(e.getSource()==apri)
			{	JFileChooser jfc=new JFileChooser();
				try {
					if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
					{	if(!jfc.getSelectedFile().exists())
							JOptionPane.showMessageDialog(null, "File inesistente");
						else {
							fileDiSalvataggio=jfc.getSelectedFile();
							try {
								ripristina(fileDiSalvataggio.getAbsolutePath());
								FS=new FrameSudoku();
								FS.setVisible(true);
							}catch(Exception io) {
								JOptionPane.showMessageDialog(null, "Impossibile aprire");
							}
						}
					}
					else {
						JOptionPane.showMessageDialog(null, "Operazione annullata");
						}
					}catch(Exception exc) {
						exc.printStackTrace();
				}
			}
			
			else if(e.getSource()==salva)
			{	JFileChooser jfc=new JFileChooser();
				try
				{	if(fileDiSalvataggio!=null)
					{	int risposta=JOptionPane.showConfirmDialog(null,"Sovrascrivere "+fileDiSalvataggio.getAbsolutePath()+" ?");
						if(risposta == 0)//si
							salva(fileDiSalvataggio.getAbsolutePath());
						else
							JOptionPane.showMessageDialog(null, "File mai salvato");
					}
					if(jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
					{	fileDiSalvataggio=jfc.getSelectedFile();
						salva(fileDiSalvataggio.getAbsolutePath());
					}
					
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}
			else if(e.getSource() == about) {
				JOptionPane.showMessageDialog( null,"Progetto POO - SudokuGUI - Francesco Zumpano,209693","About", 
						JOptionPane.PLAIN_MESSAGE );
			}
			else if(e.getSource()==bottone) {
				if(FS==null)
					FS=new FrameSudoku();
				FS.setVisible(true);
				salva.setEnabled(true);
			}
			
			
			
		}	
	}
    private void salva(String nome)throws IOException
    {	PrintWriter pw = new PrintWriter(new FileWriter(nome));
 		for(Integer j : daSalvare)
 			pw.println(j);
 		pw.close();
     }//salva
  
	@SuppressWarnings("resource")
	private void ripristina(String nome) throws IOException 
    {	BufferedReader br = new BufferedReader(new FileReader(nome));
		daSalvare.clear();
		for(;;) 
		{	String linea = br.readLine();
			if(linea == null)
				break;
			daSalvare.add(Integer.parseInt(linea));
	}
	br.close();
    }//ripristina
	
}//FinestraGUI
public class SudokuGUI {
	public static void main(String...args)
	{	FinestraGUI f=new FinestraGUI();
		f.setVisible(true);
	}

}
