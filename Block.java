import java.util.Arrays;

/**
 * 
 */

/**
 * @author Gaurav
 *
 */
public class Block {
	private int previoushash;
	private int blockHash;
	private String[] transactions;

	public Block(int previoushash, String[] transactions) {
		this.previoushash = previoushash;
		this.transactions = transactions;

		int contents = Arrays.hashCode(transactions);
		Object[] tmp  = {previoushash, contents};
		this.blockHash = Arrays.hashCode(tmp);

	}

	public int getBlockHash() {
		return this.blockHash;
	}

	@Override
	public String toString() {
		return "Block [previoushash=" + previoushash + ", blockHash=" + blockHash + ", transactions="
				+ Arrays.toString(transactions) + "]";
	}
	
	

}
