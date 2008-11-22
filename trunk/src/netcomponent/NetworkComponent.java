package netcomponent;

public abstract class NetworkComponent{
	private Network network;
	private String name;

	public NetworkComponent(Network network){
		this.network=network;
		network.registerNetObj(this);
	}

	public void setName(String name){this.name=name;}

	public String getName(){return name;}

	public Network getNetwork(){return network;}

	public abstract void action();

	public abstract void receivePacket(Packet p);

	public abstract void transmitPacket(Packet p);
	
	public NetworkData generateDataEntry(Packet p, NetworkComponent nextHop){
		return new NetworkData(p,this,nextHop,getNetwork().getTime());
	}
	public NetworkData generateDataEntry(Packet p){
		return new NetworkData(p,this,this,getNetwork().getTime());
	}
	public NetworkData generateDataEntry(Packet p, int aux){
		return new NetworkData(p,this,this,getNetwork().getTime(),aux);
	}
}