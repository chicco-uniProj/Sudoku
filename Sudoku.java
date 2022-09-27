package poo.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Sudoku extends Backtracking<Integer, Integer>
{	private List<Integer>puntiPossibili;
	private List<Integer[][]>soluzioni=new ArrayList<Integer[][]>();;
	private int [] impostate=new int[81];
	private int[][]grid;
	private int nrSol=0;
	
	public Sudoku()
	{	puntiPossibili=puntiDiScelta();
		grid=new int[9][9];
		for(int i=0;i<grid.length;i++)
			for(int j=0;j<grid.length;j++)
				grid[i][j]=0;	
	}
	

	@Override
	protected boolean assegnabile(Integer p, Integer s) {
		int riga=p/9;
		int col=p%9;
		if(grid[riga][col]!=0)
			return false;
		for(int i=8;i>=0;i--)
			if(grid[i][col]==s) 
				return false;
		for(int j=8;j>=0;j--)
			if(grid[riga][j]==s)
				return false;
		int r=riga-riga % 3;
		int c=col-col % 3;
		for(riga=r;riga<r+3;riga++)
			for(col=c;col<c+3;col++)
				if(grid[riga][col]==s)
					return false;
		return true;
	}

	@Override
	protected void assegna(Integer ps, Integer s) //colonne % riga /
	{	int riga=ps/9;
		int col=ps%9;
		grid[riga][col]=s;
		
	}

	@Override
	protected void deassegna(Integer ps, Integer s)
	{	int riga=ps/9;
		int col=ps%9;
		grid[riga][col]=0;	
	}

	@Override
	protected void scriviSoluzione(Integer p)
	{	nrSol++;
		
		Integer[][]soluzione=new Integer[9][9];
		for(int i=0;i<grid.length;i++)
			for(int j=0;j<grid.length;j++)
				soluzione[i][j]=grid[i][j];
		
/*		for(int i=0;i<grid.length;i++)
			System.out.println(Arrays.toString(soluzione[i]));
		System.out.println(soluzioni.size());
*/		
		soluzioni.add(soluzione);
	}

	@Override
	protected List<Integer> puntiDiScelta() {	//tutte le caselle
		List<Integer>ps=new ArrayList<Integer>();
		for(int i=0;i<81;i++)
			ps.add(i);
		return ps;
	}

	@Override
	protected Collection<Integer> scelte(Integer p) { // num 1-9
		List<Integer>scelto=new ArrayList<Integer>();
		for(int i=1;i<=9;i++)
			scelto.add(i);
		return scelto;
			
	}

	@Override
	protected void risolvi() {
		tentativo(puntiPossibili,puntiPossibili.get(0));

	}

	@Override
	protected boolean esisteSoluzione(Integer p)
	{	return puntiPossibili.indexOf(p)==puntiPossibili.size()-1;
	}
	
	@Override
	protected boolean ultimaSoluzione( Integer p )
	{	return nrSol==10;
	}
	
	public void imposta(Integer ps,int v)//punti gia messi
	{	if(!assegnabile(ps, v))
			throw new IllegalArgumentException();
		else
		{	assegna(ps, v);
			puntiPossibili.remove(ps);
		}
		impostate[ps]=v;
	}

	
	public List<Integer[][]>getSoluzioni()
	{	List<Integer[][]>ret=new ArrayList<Integer[][]>();
		for(int i=0;i<soluzioni.size();i++)
			ret.add(soluzioni.get(i));
		return ret;
	}
	public ArrayList<Integer> getImpostate() //mi serve nella gui per salvare
	{	ArrayList<Integer>ret=new ArrayList<>();
		for(int i=0;i<81;i++)
			ret.add(impostate[i]);
		return ret;
	}
	
	
	public static void  main(String...args)
	{	Sudoku s=new Sudoku();
		s.imposta(0, 5);
		s.imposta(4,6 );
		s.imposta(5,8);
		s.imposta(6, 7);
		s.imposta(8, 4);
		s.imposta(18,8);
		s.imposta(21,9);
		s.imposta(24,6);
		s.imposta(43,6);
		s.imposta(51, 5);
		s.imposta(39, 5);
		s.imposta(47, 4);
		s.imposta(36,9);
		s.imposta(56,2);
		s.imposta(65,5);
		s.imposta(72,4);
		s.imposta(73,1);
		s.imposta(59,4);
		s.imposta(77,5);
		s.imposta(60,1);
		s.imposta(80,6);
		s.imposta(78,2);
		s.risolvi();
		Integer[][]sol=s.getSoluzioni().get(0);
		
		for(int i=0;i<sol.length;i++)
			System.out.println(Arrays.toString(sol[i]));
	
		
		
		
	}


}
