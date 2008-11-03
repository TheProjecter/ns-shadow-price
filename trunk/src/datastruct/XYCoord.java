package datastruct;

public class XYCoord<E extends Number> {
	private E x;
	private E y;
	
	public XYCoord(E x, E y){
		this.x=x;
		this.y=y;
	}
	
	public E getX() {return x;}
	public E getY() {return y;}
}
