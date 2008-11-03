package datastruct;
import java.util.LinkedList;

import netcomponent.NetworkComponent;

public class ActionQueue{
	private LinkedList<RandomList<NetworkComponent>> q;

	public ActionQueue(){
		q = new LinkedList<RandomList<NetworkComponent>>();
		q.add(new RandomList<NetworkComponent>());
	}

	public void addActor(NetworkComponent actor){addActor(actor,0);}

	public void addActor(NetworkComponent actor, int delay){
		int holderSize = delay-q.size();
		if (holderSize>=0)
			for(int i=0; i<=holderSize; i++)
				q.add(new RandomList<NetworkComponent>());
		q.get(delay).add(actor);
	}

	public RandomList<NetworkComponent> getHead(){return q.getFirst();}

	public void expungeHead(){
		q.remove();
		if (q.size()==0)
			q.add(new RandomList<NetworkComponent>());
	}

	public int size(){return q.size();}
}