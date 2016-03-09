package maze.logi;

import java.util.LinkedList;
import java.util.Random;

public class Board {

	/**************************************** ATTRIBUTES */

	private char board[][];
	private Hero hero = new Hero();
	private LinkedList<Dragon> dragons = new  LinkedList<Dragon>();
	private Sword sword = new Sword();
	private Point exit = new Point();
	private Random rnd = new Random();

	/**************************************** FUNCTIONS */

	/*****************
	 * CONSTRUCTOR *
	 *****************/
	public Board() {
		char[][] b = { {} };
		board = b;
	}

	public Board(char[][] b) {
		setBoard(b);
	}

	/*****************
	 * GETS *
	 *****************/

	public char[][] getBoard() {
		return board;
	}

	public Hero getHero() {
		return hero;
	}

	public LinkedList<Dragon> getDragons() {
		return dragons;
	}

	public Sword getSword() {
		return sword;
	}

	public Point getExit() {
		return exit;
	}

	public char getBoardSymbol(Point p) {
		// x representa as col unas da matriz e y as linhas
		return board[p.getY()][p.getX()];
	}

	public Point getPositionSymbol(char symbol,int ignores) {
		Point pos = new Point();
		int ing = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == symbol) {
					if(ing == ignores)
					{
						pos.setXY(j, i);
						return pos;
					}
					else
						ing++;
				}
			}
		}
		return pos;
	}


	/*****************
	 * SETS *
	 *****************/

	public void setBoard(char[][] b) {
		board = b;
		Point pos;
		// hero
		pos = getPositionSymbol('H',0);

		if (pos.getX() == 0 && pos.getY() == 0)

		{

		}	// Lancar excpeçao
		// CASO N�O
		else
			hero.setPosition(pos);
		// dragon
		boolean allDragonsPicked = false;
		int picks = 0;
		do
		{
			pos = getPositionSymbol('D',picks);
			if (pos.getX() == 0 && pos.getY() == 0)
				allDragonsPicked = true;
			else
			{
				dragons.add(new Dragon(new Point(pos.getX(),pos.getY())));
				picks++;
			}
		}while(!allDragonsPicked);
		if(picks == 0)
		{
			//ERRO

		}
		// sword
		pos = getPositionSymbol('E',0);
		if (pos.getX() == 0 && pos.getY() == 0)
		{
			//FALTA lancar exception
		}
		else
			sword.setPosition(pos);

		// exit
		exit.setXY(getPositionSymbol('S',0).getX(), getPositionSymbol('S',0).getY());
	}

	public void setDragonsBehaviour(char dragon_MODE) {
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			if (dragon_MODE == 'P') {
				dragon.setParalysedMode(true);
				dragon.setSleepMode(false);
			} else if (dragon_MODE == 'S') {
				dragon.setSleepMode(true);
				dragon.setParalysedMode(false);
			}
		}
	}

	/*****************
	 * MOVEMENTS *
	 *****************/

	public void heroNextPosition(Point new_pos) {
		Point ini_pos = hero.getPosition();
		if (!betweenBoardLimits(new_pos)) // just in case ...
			return;
		if (getBoardSymbol(new_pos) == 'X' || getBoardSymbol(new_pos) == 'd')
			return;
		else if (getBoardSymbol(new_pos) == 'E') {
			hero.equipArmor();
			sword.setUse(true);
		}
		cleanPosition(ini_pos);
		hero.setPosition(new_pos);
		heroDragonsCollision();
	}

	public void moveHero(char direction) {

		Point new_pos = new Point();

		switch (direction) {
		case 'a': // esquerda
		{
			new_pos.setXY(hero.getX() - 1, hero.getY());
			heroNextPosition(new_pos);
			break;
		}
		case 's': // baixo
		{
			new_pos.setXY(hero.getX(), hero.getY() + 1);
			heroNextPosition(new_pos);
			break;
		}
		case 'd': // direita
		{
			new_pos.setXY(hero.getX() + 1, hero.getY());
			heroNextPosition(new_pos);
			break;
		}
		case 'w': // cima
		{
			new_pos.setXY(hero.getX(), hero.getY() - 1);
			heroNextPosition(new_pos);
			break;
		}
		default:
			break;
		}
	}

	private boolean dragonNextPosition(Point new_pos,Dragon dragon) {
		Point ini_pos = dragon.getPosition();
		if (!betweenBoardLimits(new_pos)) // just in case
			return false;
		if (getBoardSymbol(new_pos) == 'X' || getBoardSymbol(new_pos) == 'S')
			return false;
		else if (getBoardSymbol(new_pos) == 'E')
			dragon.setSymbol('F');
		else
			dragon.setSymbol('D');
		cleanPosition(ini_pos);
		dragon.setPosition(new_pos);
		return true;
	}

	public boolean moveDragon(int d,Dragon dragon) {
		/*
		 * d - 0 down d - 1 up d - 2 left d - 3 right
		 */
		if (dragon.getParalysedMode())
			return false;

		Point new_pos = new Point();
		boolean move = false;

		switch (d) {
		case 0:// down
			new_pos.setXY(dragon.getX(), dragon.getY() + 1);
			move = dragonNextPosition(new_pos,dragon);
			break;
		case 1:// up
			new_pos.setXY(dragon.getX(), dragon.getY() - 1);
			move = dragonNextPosition(new_pos,dragon);
			break;
		case 2:// left
			new_pos.setXY(dragon.getX() - 1, dragon.getY());
			move = dragonNextPosition(new_pos,dragon);
			break;
		case 3:// right
			new_pos.setXY(dragon.getX() + 1, dragon.getY());
			move = dragonNextPosition(new_pos,dragon);
			break;
		case 4:
			move = true;// Sleep
			dragon.setAwake(false);
			break;
		}
		return move;
	}

	public void moveRandomDragons() {
		int mov;
		for(int i = 0; i < dragons.size();i++)
		{
			Dragon dragon = dragons.get(i);
			if (dragon.getParalysedMode())
				return;
			boolean move = false;
			do {
				if (dragon.getSleepMode())
					mov = rnd.nextInt(5);
				else
					mov = rnd.nextInt() % 4;
				move = moveDragon(mov,dragon);
			} while (!move);
		}
	}

	/*****************
	 * BOOLEAN *
	 *****************/

	public boolean exitBoard() {

		if (heroWins()) {
			return true;
		} else if (hero.isAlive() == false) {
			return true;
		} else
			return false;
	}

	// for tests
	public boolean betweenBoardLimits(Point p) {
		if (p.getX() >= board.length || p.getY() >= board.length)
			return false;
		else
			return true;
	}

	public boolean heroWins() {
		if (hero.getPosition().equals(exit) && dragons.isEmpty())
			return true;
		else
			return false;
	}

	/*****************
	 * OTHERS *
	 *****************/

	public void updateBoard() {
		heroDragonsCollision();

		// update exit
		placeOnBoard(exit, 'S');

		// update sword
		if (!sword.inUse())
			placeOnBoard(sword.getPosition(), sword.getSymbol());
		// update dragon
		for(int i = 0; i < dragons.size();i++)
		{
			Dragon dragon = dragons.get(i);
			if (dragon.isAlive())
				placeOnBoard(dragon.getPosition(), dragon.getSymbol());
			else 
				cleanPosition(dragon.getPosition());
		}
		// update hero
		if (hero.isAlive())
			placeOnBoard(hero.getPosition(), hero.getSymbol());
		else
			cleanPosition(hero.getPosition());
	}

	public void cleanPosition(Point p) {
		placeOnBoard(p, ' ');
	}

	public void placeOnBoard(Point position, char symbol) {
		board[position.getY()][position.getX()] = symbol;
	}

	public void heroDragonsCollision() 
	{
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			int dist_x = Math.abs(hero.getPosition().getX() - dragon.getPosition().getX());
			int dist_y = Math.abs(hero.getPosition().getY() - dragon.getPosition().getY());
			//int dist = (int) Math.sqrt(dist_y * dist_y + dist_x * dist_x);
			int emlinha = dist_x + dist_y;// ANULAR A dist em diagonal
			if (emlinha == 1 || emlinha == 0)
			{
				if (hero.getSymbol() == 'H' && (dragon.getSymbol() == 'D'))
					hero.setAlive(false);
				if (hero.getSymbol() == 'A')
				{
					dragon.setAlive(false);
					dragons.remove(i);
					i--;
				}
			}
		}

	}
}
