public abstract class NetworkComponent{
	private Network network;
	private String name;

	public NetworkComponent(Network network){
		this.network=network;
	}

	public void setName(String name){this.name=name;}

	public String getName(){return name;}

	public Network getNetwork(){return network;}

	public abstract void action();

	public abstract void receivePacket(Packet p);

	public abstract void transmitPacket(Packet p);
}