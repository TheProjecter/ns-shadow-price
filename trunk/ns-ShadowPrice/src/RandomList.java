import java.util.LinkedList;
import java.util.Random;

public class RandomList<E> extends LinkedList<E>{
	public RandomList(){super();}

	public E pick(){
		if (size()==1) {return remove();}
		else {return remove((new Random()).nextInt(size()-1));}
	}
}