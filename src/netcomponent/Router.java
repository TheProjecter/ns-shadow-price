package netcomponent;

public class Router extends Resource{
	
	public Router(Network network, int bufferSize, int capacity){
		super(network,bufferSize,capacity);
	}
	
	public void action(){
		Packet p = buffer.remove();
		transmitPacket(p);
	}
}
