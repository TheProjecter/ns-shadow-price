package netcomponent;

public class ECNRouterRED extends ECNResourceRED{
	
	public ECNRouterRED(Network network, int bufferSize, int capacity){
		super(network,bufferSize,capacity);
	}
	
	public void action(){
		Packet p = buffer.remove();
		transmitPacket(p);
	}
}
