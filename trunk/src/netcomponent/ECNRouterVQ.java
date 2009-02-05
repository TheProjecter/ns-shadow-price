package netcomponent;

public class ECNRouterVQ extends ECNResourceVQ {
	public ECNRouterVQ(Network network, int bufferSize, int capacity){
		super(network,bufferSize,capacity);
	}
	public void action(){
		Packet p = buffer.remove();
		transmitPacket(p);
	}
}
