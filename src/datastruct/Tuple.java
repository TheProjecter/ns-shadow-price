package datastruct;

public class Tuple<E,F> {
	private E x;
	private F y;
	
	public Tuple(E x, F y){
		this.x=x;
		this.y=y;
	}
	
	public E getX(){return x;}
	public F getY(){return y;}
	public void setX(E x){this.x=x;}
	public void setY(F y){this.y=y;}
}
