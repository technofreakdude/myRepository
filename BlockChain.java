
public class BlockChain {

	public BlockChain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String[] genesisTransations = {"abc","xyz","entire Blockchain is here"};
		Block genesisBlock = new Block(0, genesisTransations);
		
		String[] block1Transations = {"i am legend","writing Block Chain","Blockchain"};
		Block block1 = new Block(genesisBlock.getBlockHash(), block1Transations);
		String[] block2Transations = {"legendendary","satoshi","nakamoto"};
		Block block2 = new Block(block1.getBlockHash(), block2Transations);
		
		
		//System.out.println("hash of genesis block:"+ genesisBlock.getBlockHash());
//-1008032270
		System.out.println(genesisBlock);
		System.out.println(block1);
		System.out.println(block2);		
	}
}
